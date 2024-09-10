package payselection.payments.sdk.models.requests.pay.enum

import com.google.gson.annotations.SerializedName

enum class Sno {
    @SerializedName("osn")
    Osn,
    @SerializedName("usn_income")
    UsnIncome,
    @SerializedName("usn_income_outcome")
    UsnIncomeOutcome,
    @SerializedName("envd")
    Envd,
    @SerializedName("esn")
    Esn,
    @SerializedName("patent")
    Patent,
}