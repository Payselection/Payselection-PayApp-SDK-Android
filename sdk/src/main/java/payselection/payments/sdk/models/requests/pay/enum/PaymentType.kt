package payselection.payments.sdk.models.requests.pay.enum

import com.google.gson.annotations.SerializedName

enum class PaymentType(val code:String) {
    @SerializedName("0")
    Cash("0"),

    @SerializedName("1")
    NonCash("1"),

    @SerializedName("2")
    Prepayment("2"),

    @SerializedName("3")
    Postpayment("3"),

    @SerializedName("4")
    Other("4"),
}