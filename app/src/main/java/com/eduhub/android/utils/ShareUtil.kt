package com.eduhub.android.utils

import android.content.Context
import android.content.Intent
import com.eduhub.android.data.local.entities.CertificateEntity

object ShareUtil {

    fun shareCertificate(context: Context, cert: CertificateEntity) {
        val shareText = """
            🎓 OFFICIAL CERTIFICATE OF MASTERY — EDUHUB 🎓
            
            Student Name: ${cert.studentName}
            Course: ${cert.courseTitle}
            Grade: ${cert.grade} (${cert.scorePercentage}%)
            Date Issued: ${cert.issueDate}
            
            Credential ID: ${cert.verificationId}
            Digital SHA-256 Signature: ${cert.sha256Signature}
            ${if (cert.aiCommendation.isNotBlank()) "Honors Commendation: \"${cert.aiCommendation}\"\n" else ""}
            Verified by: EDUHUB Global Autonomous Academy (100% Free Forever)
            Verification In-App Portal: Code ${cert.verificationId}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "EDUHUB Certificate - ${cert.courseTitle}")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, "Share Certificate via"))
    }

    fun shareSnippet(context: Context, title: String, lang: String, code: String) {
        val text = "Check out this $lang snippet from EDUHUB Playground: $title\n\n```$lang\n$code\n```"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Code Snippet: $title")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share Snippet via"))
    }
}
