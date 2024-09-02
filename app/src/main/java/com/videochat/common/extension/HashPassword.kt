package com.videochat.common.extension

import android.os.Build
import androidx.annotation.RequiresApi
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object HashPassword {
    fun generateSalt(): ByteArray {
        val sr = SecureRandom.getInstance("SHA1PRNG")
        val salt = ByteArray(16)
        sr.nextBytes(salt)
        return salt
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun hashPassword(password: String, salt: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val hashedPassword = md.digest(password.toByteArray())
        return Base64.getEncoder().encodeToString(hashedPassword)
    }
}
