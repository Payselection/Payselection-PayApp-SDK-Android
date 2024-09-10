package payselection.payments.sdk.models.requests.pay.enum

import com.google.gson.annotations.SerializedName

enum class MeasurementUnit(val code: String) {
    @SerializedName("0")
    Unit("0"),

    @SerializedName("10")
    Gram("10"),

    @SerializedName("11")
    Kilogram("11"),

    @SerializedName("12")
    Ton("12"),

    @SerializedName("20")
    Centimeter("20"),

    @SerializedName("21")
    Decimeter("21"),

    @SerializedName("22")
    Meter("22"),

    @SerializedName("30")
    SquareCentimeter("30"),

    @SerializedName("31")
    SquareDecimeter("31"),

    @SerializedName("32")
    SquareMeter("32"),

    @SerializedName("40")
    Milliliter("40"),

    @SerializedName("41")
    Liter("41"),

    @SerializedName("42")
    CubicMeter("42"),

    @SerializedName("50")
    KilowattHour("50"),

    @SerializedName("51")
    Gigacalorie("51"),

    @SerializedName("70")
    Day("70"),

    @SerializedName("71")
    Hour("71"),

    @SerializedName("72")
    Minute("72"),

    @SerializedName("73")
    Second("73"),

    @SerializedName("80")
    Kilobyte("80"),

    @SerializedName("81")
    Megabyte("81"),

    @SerializedName("82")
    Gigabyte("82"),

    @SerializedName("83")
    Terabyte("83"),

    @SerializedName("255")
    Other("255")
}