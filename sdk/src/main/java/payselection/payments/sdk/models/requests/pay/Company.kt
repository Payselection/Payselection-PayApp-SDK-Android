package payselection.payments.sdk.models.requests.pay

import com.google.gson.annotations.SerializedName
import payselection.payments.sdk.models.requests.pay.enum.Sno

data class Company(
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("sno")
    val sno: Sno? = null,
    @SerializedName("inn")
    val inn: String,
    @SerializedName("payment_address")
    val paymentAddress: String
)