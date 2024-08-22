package payselection.payments.sdk.crypto

import android.util.Base64
import com.google.gson.JsonObject
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.interfaces.ECPrivateKey
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECPublicKeySpec
import org.bouncycastle.util.encoders.Hex
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import java.util.Arrays
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal object CryptoModule {
    private val random = SecureRandom()
    private val ecCurve = ECNamedCurveTable.getParameterSpec("secp256k1")
    private val provider = BouncyCastleProvider()

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    private fun getPublicKey(pub_key: ByteArray): ECPublicKey {
        val point = ecCurve.curve.decodePoint(pub_key)
        val publicKeySpec = ECPublicKeySpec(point, ecCurve)
        val keyFactory = KeyFactory.getInstance("EC", provider)
        return keyFactory.generatePublic(publicKeySpec) as ECPublicKey
    }

    private fun derivePublicKey(pkey: ECPrivateKey): ByteArray {
        return ecCurve.g.multiply(pkey.d).getEncoded(false)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    private fun deriveSharedKey(pkey: Key, peer_pub_key: PublicKey): ByteArray {
        val keyAgreement = KeyAgreement.getInstance("ECDH", provider)
        keyAgreement.init(pkey)
        keyAgreement.doPhase(peer_pub_key, true)
        return keyAgreement.generateSecret()
    }

    @Throws(NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class)
    private fun randomEphemeralKey(): ECPrivateKey {
        val keyPairGenerator = KeyPairGenerator.getInstance("EC", provider)
        keyPairGenerator.initialize(ecCurve, random)
        return keyPairGenerator.generateKeyPair().private as ECPrivateKey
    }

    private fun randomIv(cipher: Cipher): ByteArray {
        val iv = ByteArray(cipher.blockSize)
        random.nextBytes(iv)
        return iv
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        InvalidKeySpecException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    private fun encrypt(key: String, msg: String): EncryptedData {
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", provider)
        val iv = randomIv(cipher)
        val publicKey = getPublicKey(Hex.decode(key))
        val ephemeralPrivateKey: ECPrivateKey = randomEphemeralKey()
        val ephemeralPublicKey: ByteArray = derivePublicKey(ephemeralPrivateKey)
        val sharedKeyHash = MessageDigest.getInstance("SHA512", provider).digest(
            deriveSharedKey(ephemeralPrivateKey, publicKey)
        )
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(
                Arrays.copyOfRange(sharedKeyHash, 0, 32),
                cipher.algorithm
            ),
            IvParameterSpec(iv)
        )
        val cipherMsg = cipher.doFinal(msg.toByteArray(StandardCharsets.UTF_8))
        val macGenerator = Mac.getInstance("HMAC/SHA256", provider)
        macGenerator.init(
            SecretKeySpec(
                Arrays.copyOfRange(sharedKeyHash, 32, 64),
                macGenerator.algorithm
            )
        )
        macGenerator.update(iv)
        macGenerator.update(ephemeralPublicKey)

        return EncryptedData(
            cipherMsg,
            ephemeralPublicKey,
            iv,
            macGenerator.doFinal(cipherMsg)
        )
    }

    fun createCryptogram(paymentData: String, publicKey: String): String {
        val encryptedData = encrypt(publicKey, paymentData)

        val sendData = JsonObject()
        sendData.addProperty("signedMessage", JsonObject().apply {
            addProperty("encryptedMessage", toString(encryptedData.encrypted))
            addProperty("ephemeralPublicKey", toString(encryptedData.key))
        }.toString())
        sendData.addProperty("iv", toString(encryptedData.iv))
        sendData.addProperty("tag", toString(encryptedData.tag))

        return toString(sendData.toString().toByteArray())
    }

    fun createCryptogramRsa(paymentData: String, key: String): String {
        val pemString = String(Base64.decode(key, Base64.DEFAULT), StandardCharsets.UTF_8)
        val cleanPublicKey = pemString
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .trim()

        val publicKeyBytes = Base64.decode(cleanPublicKey, Base64.DEFAULT)

        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec  = X509EncodedKeySpec(publicKeyBytes)
        val publicKey = keyFactory.generatePublic(keySpec)

        val cipher = Cipher.getInstance("RSA/ECB/OAEPwithSHA-256andMGF1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = cipher.doFinal(paymentData.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT).trim()
    }

    private fun toString(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}