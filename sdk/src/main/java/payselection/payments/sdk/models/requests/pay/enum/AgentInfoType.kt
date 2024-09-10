package payselection.payments.sdk.models.requests.pay.enum

import com.google.gson.annotations.SerializedName

enum class AgentInfoType {
    @SerializedName("bank_paying_agent")
    BankPayingAgent,

    @SerializedName("bank_paying_subagent")
    BankPayingSubagent,

    @SerializedName("paying_agent")
    PayingAgent,

    @SerializedName("paying_subagent")
    PayingSubagent,

    @SerializedName("attorney")
    Attorney,

    @SerializedName("commission_agent")
    CommissionAgent,

    @SerializedName("another")
    Another,
}