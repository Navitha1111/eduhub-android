package com.eduhub.android.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ServerPingResult(
    val ok: Boolean,
    val serverName: String = "EDUHUB Central Online Server",
    val status: String = "OK",
    val version: String = "2.0",
    val coursesCount: Int = 39,
    val lessonsCount: Int = 936,
    val usersCount: Int = 0,
    val rawMessage: String = ""
)

data class AuthResult(
    val ok: Boolean,
    val userId: Long = 0L,
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val role: String = "STUDENT",
    val adminToken: String = "",
    val errorMessage: String = ""
)

data class CertificateVerificationData(
    val isValid: Boolean,
    val certificateId: String,
    val studentName: String,
    val courseTitle: String,
    val issueDate: String,
    val sha256Signature: String,
    val grade: String = "Mastery with Honors (100% Course Completion)",
    val issuer: String = "EDUHUB Global Autonomous Academy",
    val aiCommendation: String = "",
    val message: String = ""
)

object VpsApiClient {

    const val DEFAULT_VPS_URL = "http://162.35.9.208:8080"

    private fun normalizeBaseUrl(rawUrl: String): String {
        var u = rawUrl.trim()
        if (!u.startsWith("http://") && !u.startsWith("https://")) {
            u = "http://$u"
        }
        while (u.endsWith("/")) {
            u = u.substring(0, u.length - 1)
        }
        return u
    }

    suspend fun pingServer(serverUrl: String = DEFAULT_VPS_URL): ServerPingResult = withContext(Dispatchers.IO) {
        val base = normalizeBaseUrl(serverUrl)
        try {
            val url = URL("$base/api/ping")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000
                setRequestProperty("Accept", "application/json")
            }

            val code = conn.responseCode
            if (code in 200..299) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(responseText)
                ServerPingResult(
                    ok = json.optBoolean("ok", true),
                    status = json.optString("status", "OK"),
                    version = json.optString("version", "2.0"),
                    coursesCount = json.optInt("courses", 39),
                    lessonsCount = json.optInt("lessons", 936),
                    usersCount = json.optInt("users", 0),
                    rawMessage = "Connected to EDUHUB VPS ($base)"
                )
            } else {
                ServerPingResult(
                    ok = false,
                    rawMessage = "VPS returned HTTP status $code"
                )
            }
        } catch (e: Exception) {
            ServerPingResult(
                ok = false,
                rawMessage = "Cannot connect to VPS ($base): ${e.localizedMessage ?: "Connection timed out"}"
            )
        }
    }

    suspend fun registerStudent(
        serverUrl: String = DEFAULT_VPS_URL,
        fullName: String,
        username: String,
        email: String,
        password: String
    ): AuthResult = withContext(Dispatchers.IO) {
        val base = normalizeBaseUrl(serverUrl)
        try {
            val payload = JSONObject().apply {
                put("fullName", fullName.trim())
                put("username", username.trim())
                put("email", email.trim().lowercase(Locale.ROOT))
                put("password", password)
            }

            val url = URL("$base/api/auth/register")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 8000
                readTimeout = 8000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("Accept", "application/json")
            }

            OutputStreamWriter(conn.outputStream, StandardCharsets.UTF_8).use {
                it.write(payload.toString())
                it.flush()
            }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val respStr = stream?.bufferedReader()?.use { it.readText() } ?: ""

            if (code in 200..299) {
                val json = JSONObject(respStr)
                AuthResult(
                    ok = json.optBoolean("ok", true),
                    userId = json.optLong("id", 0L),
                    fullName = json.optString("fullName", fullName),
                    username = json.optString("username", username),
                    email = json.optString("email", email),
                    role = json.optString("role", "STUDENT")
                )
            } else {
                val err = try {
                    JSONObject(respStr).optString("error", "Registration rejected by VPS ($code)")
                } catch (ignored: Exception) {
                    "Registration failed ($code)"
                }
                AuthResult(ok = false, errorMessage = err)
            }
        } catch (e: Exception) {
            AuthResult(ok = false, errorMessage = "Network error: ${e.localizedMessage ?: "Unable to contact VPS"}")
        }
    }

    suspend fun login(
        serverUrl: String = DEFAULT_VPS_URL,
        identity: String,
        password: String
    ): AuthResult = withContext(Dispatchers.IO) {
        val base = normalizeBaseUrl(serverUrl)
        try {
            val payload = JSONObject().apply {
                put("identity", identity.trim())
                put("password", password)
            }

            val url = URL("$base/api/auth/login")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 8000
                readTimeout = 8000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("Accept", "application/json")
            }

            OutputStreamWriter(conn.outputStream, StandardCharsets.UTF_8).use {
                it.write(payload.toString())
                it.flush()
            }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val respStr = stream?.bufferedReader()?.use { it.readText() } ?: ""

            if (code in 200..299) {
                val json = JSONObject(respStr)
                AuthResult(
                    ok = json.optBoolean("ok", true),
                    userId = json.optLong("id", 0L),
                    fullName = json.optString("fullName", identity),
                    username = json.optString("username", identity),
                    email = json.optString("email", ""),
                    role = json.optString("role", "STUDENT"),
                    adminToken = json.optString("adminToken", "")
                )
            } else {
                val err = try {
                    JSONObject(respStr).optString("error", "Incorrect credentials or account not found.")
                } catch (ignored: Exception) {
                    "Authentication failed ($code)"
                }
                AuthResult(ok = false, errorMessage = err)
            }
        } catch (e: Exception) {
            AuthResult(ok = false, errorMessage = "Network error: ${e.localizedMessage ?: "Unable to contact VPS"}")
        }
    }

    suspend fun pushProgress(
        serverUrl: String = DEFAULT_VPS_URL,
        username: String,
        xp: Int,
        streak: Int,
        completedLessons: Int
    ): Boolean = withContext(Dispatchers.IO) {
        if (username.isBlank()) return@withContext false
        val base = normalizeBaseUrl(serverUrl)
        try {
            val payload = JSONObject().apply {
                put("username", username)
                put("xp", xp)
                put("streak", streak)
                put("completedLessons", completedLessons)
            }

            val url = URL("$base/api/progress/push")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 6000
                readTimeout = 6000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            }

            OutputStreamWriter(conn.outputStream, StandardCharsets.UTF_8).use {
                it.write(payload.toString())
                it.flush()
            }

            conn.responseCode in 200..299
        } catch (ignored: Exception) {
            false
        }
    }

    // SHA-256 Digital Tamper-Proof Signature generator matching EduHub Desktop
    fun computeCertificateHash(certificateId: String, studentName: String, courseTitle: String, issueDate: String): String {
        return try {
            val rawData = "$certificateId|$studentName|$courseTitle|$issueDate"
            val digest = MessageDigest.getInstance("SHA-256").digest(rawData.toByteArray(StandardCharsets.UTF_8))
            val hex = StringBuilder()
            for (b in digest) {
                hex.append(String.format("%02X", b))
            }
            "SHA256:" + hex.toString().take(16)
        } catch (e: Exception) {
            "SHA256:" + Integer.toHexString(certificateId.hashCode()).uppercase(Locale.ROOT)
        }
    }

    fun generateUniqueCertificateId(userId: Long, languageSlug: String, courseTitle: String): String {
        val currentYear = LocalDate.now().year
        val cleanLang = languageSlug.uppercase(Locale.ROOT).replace("[^A-Z0-9]".toRegex(), "").take(8)
        val courseHash = String.format("%04X", Math.abs(courseTitle.hashCode() % 65535))
        val uId = if (userId > 0) userId else 101L
        return "EDUHUB-$currentYear-$cleanLang-$uId$courseHash"
    }

    fun generateAiCommendation(languageSlug: String, studentName: String): String {
        val slug = languageSlug.lowercase(Locale.ROOT)
        return when {
            slug.contains("python") -> "Awarded to $studentName for demonstrating superior proficiency in Pythonic architecture, dynamic data structures, clean functional pipelines, and algorithmic efficiency. Certified as ready for production data science, automation, and backend engineering."
            slug.contains("javascript") || slug.contains("typescript") || slug.contains("js") || slug.contains("ts") || slug.contains("react") || slug.contains("node") -> "Awarded to $studentName for mastering modern ECMAScript asynchronous patterns, event-driven reactive design, modular component architecture, and full-stack software development."
            slug.contains("kotlin") || slug.contains("compose") || slug.contains("android") -> "Awarded to $studentName for demonstrating mastery in Kotlin idiomatic idioms, coroutines, reactive StateFlow patterns, and enterprise Android architecture."
            slug.contains("java") -> "Awarded to $studentName for exemplary mastery of Java object-oriented principles, type-safe generic architecture, memory management, and enterprise application reliability."
            slug.contains("rust") -> "Awarded to $studentName for mastering fearless concurrency, zero-cost abstractions, borrow-checker semantics, and memory-safe systems engineering."
            slug.contains("go") -> "Awarded to $studentName for outstanding capability in concurrent microservice design, goroutine orchestration, channels, and cloud-native software architecture."
            slug.contains("sql") || slug.contains("mongo") -> "Awarded to $studentName for rigorous competence in relational and document database modeling, index optimization, ACID compliance, and high-performance querying."
            slug.contains("docker") || slug.contains("linux") || slug.contains("bash") -> "Awarded to $studentName for exemplary competence in modern DevOps orchestration, container virtualization, shell automation, and Linux kernel fundamentals."
            slug.contains("cyber") || slug.contains("security") -> "Awarded to $studentName for demonstrating superior competence in defensive security architecture, cryptographic hashing, vulnerability mitigation, and ethical system analysis."
            else -> "Awarded to $studentName for successfully completing all 4 progressive mastery tiers, passing all rubric coding challenges, and achieving full course distinction on the EDUHUB Global Academy."
        }
    }
}
