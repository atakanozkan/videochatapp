package com.videochat.common.extension

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.util.Base64

class Converters {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromByteArray(value: ByteArray?): String? {
        return Base64.getEncoder().encodeToString(value)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toByteArray(value: String?): ByteArray? {
        return Base64.getDecoder().decode(value)
    }
}
