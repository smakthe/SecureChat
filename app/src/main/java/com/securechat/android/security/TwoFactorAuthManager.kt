package com.securechat.android.security

import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import java.security.SecureRandom
import javax.crypto.spec.SecretKeySpec
class TwoFactorAuthManager {

    fun generateSecretKey(): ByteArray {
        val random = SecureRandom()
        val secret = ByteArray(20)
        random.nextBytes(secret)
        return secret
    }

    fun generateQRCodeUri(secret: ByteArray, accountName: String, issuer: String = "SecureChat"): String {
        val base32Secret = encodeBase32(secret)
        return "otpauth://totp/$issuer:$accountName?secret=$base32Secret&issuer=$issuer"
    }

    fun generateTOTP(secret: ByteArray): String {
        val googleAuth = dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator(secret)
        return googleAuth.generate()
    }

    fun verifyTOTP(secret: ByteArray, userCode: String): Boolean {
        val expectedCode = generateTOTP(secret)
        return expectedCode == userCode
    }

    private fun encodeBase32(bytes: ByteArray): String {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        val sb = StringBuilder()
        var buffer = 0
        var bitsLeft = 0

        for (b in bytes) {
            buffer = (buffer shl 8) or (b.toInt() and 0xFF)
            bitsLeft += 8
            while (bitsLeft >= 5) {
                sb.append(alphabet[(buffer shr (bitsLeft - 5)) and 0x1F])
                bitsLeft -= 5
            }
        }

        if (bitsLeft > 0) {
            sb.append(alphabet[(buffer shl (5 - bitsLeft)) and 0x1F])
        }

        return sb.toString()
    }
}