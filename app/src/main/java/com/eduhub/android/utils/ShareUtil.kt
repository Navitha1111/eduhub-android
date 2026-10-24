package com.eduhub.android.utils

import android.content.Context
import android.content.Intent
import com.eduhub.android.data.local.entities.CertificateEntity

object ShareUtil {

    fun shareCertificate(context: Context, cert: CertificateEntity) {
        val shareText = """
            🎓 OFFICIAL CERTIFICATE OF COMPLETION 🎓
            
            This certifies that ${cert.studentName} has successfully completed 
            the entire 0-to-Advanced curriculum for ${cert.courseTitle} on EDUHUB!
            
            Grade: ${cert.grade} (${cert.scorePercentage}%)
            Verification ID: ${cert.verificationId}
            Date Issued: ${cert.issueDate}
            
            Verified by EDUHUB — Global Programming Self-Study Platform (100% Free Forever).
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
