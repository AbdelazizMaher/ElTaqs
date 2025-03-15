package com.example.eltaqs.data.model

import com.google.gson.annotations.SerializedName

class GeocodingResponse : ArrayList<GeocodingResponseItem>()

data class GeocodingResponseItem(
    @SerializedName("country")
    val country: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("local_names")
    val localNames: LocalNames?,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("name")
    val name: String,
    @SerializedName("state")
    val state: String
)

data class LocalNames(
    @SerializedName("ab")
    val ab: String?,
    @SerializedName("af")
    val af: String?,
    @SerializedName("am")
    val am: String?,
    @SerializedName("an")
    val an: String?,
    @SerializedName("ar")
    val ar: String?,
    @SerializedName("ascii")
    val ascii: String?,
    @SerializedName("av")
    val av: String?,
    @SerializedName("ay")
    val ay: String?,
    @SerializedName("az")
    val az: String?,
    @SerializedName("ba")
    val ba: String?,
    @SerializedName("be")
    val be: String?,
    @SerializedName("bg")
    val bg: String?,
    @SerializedName("bh")
    val bh: String?,
    @SerializedName("bi")
    val bi: String?,
    @SerializedName("bm")
    val bm: String?,
    @SerializedName("bn")
    val bn: String?,
    @SerializedName("bo")
    val bo: String?,
    @SerializedName("br")
    val br: String?,
    @SerializedName("bs")
    val bs: String?,
    @SerializedName("ca")
    val ca: String?,
    @SerializedName("ce")
    val ce: String?,
    @SerializedName("co")
    val co: String?,
    @SerializedName("cr")
    val cr: String?,
    @SerializedName("cs")
    val cs: String?,
    @SerializedName("cu")
    val cu: String?,
    @SerializedName("cv")
    val cv: String?,
    @SerializedName("cy")
    val cy: String?,
    @SerializedName("da")
    val da: String?,
    @SerializedName("de")
    val de: String?,
    @SerializedName("ee")
    val ee: String?,
    @SerializedName("el")
    val el: String?,
    @SerializedName("en")
    val en: String,
    @SerializedName("eo")
    val eo: String?,
    @SerializedName("es")
    val es: String?,
    @SerializedName("et")
    val et: String?,
    @SerializedName("eu")
    val eu: String?,
    @SerializedName("fa")
    val fa: String?,
    @SerializedName("feature_name")
    val featureName: String?,
    @SerializedName("ff")
    val ff: String?,
    @SerializedName("fi")
    val fi: String?,
    @SerializedName("fj")
    val fj: String?,
    @SerializedName("fo")
    val fo: String?,
    @SerializedName("fr")
    val fr: String,
    @SerializedName("fy")
    val fy: String?,
    @SerializedName("ga")
    val ga: String?,
    @SerializedName("gd")
    val gd: String?,
    @SerializedName("gl")
    val gl: String?,
    @SerializedName("gn")
    val gn: String?,
    @SerializedName("gu")
    val gu: String?,
    @SerializedName("gv")
    val gv: String?,
    @SerializedName("ha")
    val ha: String?,
    @SerializedName("he")
    val he: String,
    @SerializedName("hi")
    val hi: String?,
    @SerializedName("hr")
    val hr: String?,
    @SerializedName("ht")
    val ht: String?,
    @SerializedName("hu")
    val hu: String?,
    @SerializedName("hy")
    val hy: String?,
    @SerializedName("ia")
    val ia: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("ie")
    val ie: String?,
    @SerializedName("ig")
    val ig: String?,
    @SerializedName("io")
    val io: String?,
    @SerializedName("is")
    val isX: String?,
    @SerializedName("it")
    val `it`: String?,
    @SerializedName("iu")
    val iu: String?,
    @SerializedName("ja")
    val ja: String?,
    @SerializedName("jv")
    val jv: String?,
    @SerializedName("ka")
    val ka: String?,
    @SerializedName("kk")
    val kk: String?,
    @SerializedName("kl")
    val kl: String?,
    @SerializedName("km")
    val km: String?,
    @SerializedName("kn")
    val kn: String?,
    @SerializedName("ko")
    val ko: String,
    @SerializedName("ku")
    val ku: String?,
    @SerializedName("kv")
    val kv: String?,
    @SerializedName("kw")
    val kw: String?,
    @SerializedName("ky")
    val ky: String?,
    @SerializedName("lb")
    val lb: String?,
    @SerializedName("li")
    val li: String?,
    @SerializedName("ln")
    val ln: String?,
    @SerializedName("lo")
    val lo: String?,
    @SerializedName("lt")
    val lt: String?,
    @SerializedName("lv")
    val lv: String?,
    @SerializedName("mg")
    val mg: String?,
    @SerializedName("mi")
    val mi: String?,
    @SerializedName("mk")
    val mk: String?,
    @SerializedName("ml")
    val ml: String?,
    @SerializedName("mn")
    val mn: String?,
    @SerializedName("mr")
    val mr: String?,
    @SerializedName("ms")
    val ms: String?,
    @SerializedName("mt")
    val mt: String?,
    @SerializedName("my")
    val my: String?,
    @SerializedName("na")
    val na: String?,
    @SerializedName("ne")
    val ne: String?,
    @SerializedName("nl")
    val nl: String?,
    @SerializedName("nn")
    val nn: String?,
    @SerializedName("no")
    val no: String?,
    @SerializedName("nv")
    val nv: String?,
    @SerializedName("ny")
    val ny: String?,
    @SerializedName("oc")
    val oc: String?,
    @SerializedName("oj")
    val oj: String?,
    @SerializedName("om")
    val om: String?,
    @SerializedName("or")
    val or: String?,
    @SerializedName("os")
    val os: String?,
    @SerializedName("pa")
    val pa: String?,
    @SerializedName("pl")
    val pl: String?,
    @SerializedName("ps")
    val ps: String?,
    @SerializedName("pt")
    val pt: String?,
    @SerializedName("qu")
    val qu: String?,
    @SerializedName("rm")
    val rm: String?,
    @SerializedName("ro")
    val ro: String?,
    @SerializedName("ru")
    val ru: String,
    @SerializedName("sa")
    val sa: String?,
    @SerializedName("sc")
    val sc: String?,
    @SerializedName("sd")
    val sd: String?,
    @SerializedName("se")
    val se: String?,
    @SerializedName("sh")
    val sh: String?,
    @SerializedName("si")
    val si: String?,
    @SerializedName("sk")
    val sk: String?,
    @SerializedName("sl")
    val sl: String?,
    @SerializedName("sm")
    val sm: String?,
    @SerializedName("sn")
    val sn: String?,
    @SerializedName("so")
    val so: String?,
    @SerializedName("sq")
    val sq: String?,
    @SerializedName("sr")
    val sr: String?,
    @SerializedName("st")
    val st: String?,
    @SerializedName("su")
    val su: String?,
    @SerializedName("sv")
    val sv: String?,
    @SerializedName("sw")
    val sw: String?,
    @SerializedName("ta")
    val ta: String?,
    @SerializedName("te")
    val te: String?,
    @SerializedName("tg")
    val tg: String?,
    @SerializedName("th")
    val th: String?,
    @SerializedName("tk")
    val tk: String?,
    @SerializedName("tl")
    val tl: String?,
    @SerializedName("to")
    val to: String?,
    @SerializedName("tr")
    val tr: String?,
    @SerializedName("tt")
    val tt: String?,
    @SerializedName("tw")
    val tw: String?,
    @SerializedName("ug")
    val ug: String?,
    @SerializedName("uk")
    val uk: String?,
    @SerializedName("ur")
    val ur: String?,
    @SerializedName("uz")
    val uz: String?,
    @SerializedName("vi")
    val vi: String?,
    @SerializedName("vo")
    val vo: String?,
    @SerializedName("wa")
    val wa: String?,
    @SerializedName("wo")
    val wo: String?,
    @SerializedName("yi")
    val yi: String?,
    @SerializedName("yo")
    val yo: String?,
    @SerializedName("zh")
    val zh: String?,
    @SerializedName("zu")
    val zu: String?
)


