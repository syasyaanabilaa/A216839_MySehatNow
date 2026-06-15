package com.example.a216839_wan_project2.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

// ── DATA MODELS ───────────────────────────────────────────────────────────────

data class OverpassResponse(
    val elements: List<OverpassElement> = emptyList()
)

data class OverpassElement(
    val id  : Long                = 0,
    val lat : Double              = 0.0,
    val lon : Double              = 0.0,
    val tags: Map<String, String> = emptyMap()
) {
    val name: String get() =
        tags["name"] ?: tags["name:en"] ?: tags["name:ms"] ?: "Unnamed Clinic"

    val address: String get() {
        val street = tags["addr:street"] ?: ""
        val city   = tags["addr:city"]   ?: ""
        val state  = tags["addr:state"]  ?: ""
        return listOf(street, city, state)
            .filter { it.isNotBlank() }
            .joinToString(", ")
            .ifBlank { "Address not available" }
    }

    val phone: String get() =
        tags["phone"] ?: tags["contact:phone"] ?: ""
}

// ── OKHTTP DIRECT CALL ────────────────────────────────────────────────────────

object OverpassApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val endpoints = listOf(
        "https://overpass-api.de/api/interpreter",
        "https://overpass.kumi.systems/api/interpreter",
        "https://maps.mail.ru/osm/tools/overpass/api/interpreter"
    )

    // Malaysia bounding box constants
    private const val MY_MIN_LAT = 0.8
    private const val MY_MAX_LAT = 7.5
    private const val MY_MIN_LON = 99.5
    private const val MY_MAX_LON = 119.5

    fun isInMalaysia(lat: Double, lon: Double): Boolean {
        return lat in MY_MIN_LAT..MY_MAX_LAT && lon in MY_MIN_LON..MY_MAX_LON
    }

    suspend fun fetchClinics(query: String): OverpassResponse = withContext(Dispatchers.IO) {
        var lastError: Exception? = null

        for (endpoint in endpoints) {
            try {
                val body = FormBody.Builder()
                    .add("data", query)
                    .build()

                val request = Request.Builder()
                    .url(endpoint)
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "HealthCentreApp/1.0 (Malaysia)")
                    .build()

                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    lastError = Exception("HTTP ${response.code} from $endpoint")
                    response.close()
                    continue
                }

                val bodyString = response.body?.string()
                    ?: throw Exception("Empty response from $endpoint")

                return@withContext parseOverpassJson(bodyString)

            } catch (e: Exception) {
                lastError = e
            }
        }

        throw lastError ?: Exception("All Overpass endpoints failed")
    }

    private fun parseOverpassJson(json: String): OverpassResponse {
        val root     = JSONObject(json)
        val array    = root.optJSONArray("elements") ?: return OverpassResponse()
        val elements = mutableListOf<OverpassElement>()

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)

            val lat = if (obj.has("center"))
                obj.getJSONObject("center").optDouble("lat", 0.0)
            else
                obj.optDouble("lat", 0.0)

            val lon = if (obj.has("center"))
                obj.getJSONObject("center").optDouble("lon", 0.0)
            else
                obj.optDouble("lon", 0.0)

            // ── Extra safety: skip anything outside Malaysia ──────────────
            if (!isInMalaysia(lat, lon)) continue

            val tagsObj = obj.optJSONObject("tags")
            val tags    = mutableMapOf<String, String>()
            tagsObj?.keys()?.forEach { key -> tags[key] = tagsObj.getString(key) }

            elements.add(
                OverpassElement(
                    id   = obj.optLong("id", 0),
                    lat  = lat,
                    lon  = lon,
                    tags = tags
                )
            )
        }

        return OverpassResponse(elements)
    }

    fun buildClinicQuery(lat: Double, lon: Double, radiusMetres: Int = 5000): String {
        // Clamp coordinates safely inside Malaysia
        val safeLat = lat.coerceIn(MY_MIN_LAT, MY_MAX_LAT)
        val safeLon = lon.coerceIn(MY_MIN_LON, MY_MAX_LON)

        return """
            [out:json][timeout:25][bbox:$MY_MIN_LAT,$MY_MIN_LON,$MY_MAX_LAT,$MY_MAX_LON];
            (
              node["amenity"="clinic"](around:$radiusMetres,$safeLat,$safeLon);
              node["amenity"="hospital"](around:$radiusMetres,$safeLat,$safeLon);
              node["amenity"="doctors"](around:$radiusMetres,$safeLat,$safeLon);
              node["healthcare"="clinic"](around:$radiusMetres,$safeLat,$safeLon);
              node["healthcare"="hospital"](around:$radiusMetres,$safeLat,$safeLon);
              node["amenity"="pharmacy"](around:$radiusMetres,$safeLat,$safeLon);
              way["amenity"="clinic"](around:$radiusMetres,$safeLat,$safeLon);
              way["amenity"="hospital"](around:$radiusMetres,$safeLat,$safeLon);
              way["healthcare"="clinic"](around:$radiusMetres,$safeLat,$safeLon);
            );
            out center;
        """.trimIndent()
    }
}
