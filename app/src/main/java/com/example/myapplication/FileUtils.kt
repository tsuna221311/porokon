package com.example.myapplication.utils

import android.content.Context
import java.io.File

/**
 * 写真を一時保存するためのヘルパー関数。
 * プロジェクト全体でこの関数を共有して使います。
 */
fun Context.createImageFile(): File {
    val timeStamp = System.currentTimeMillis()
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, ".jpg", externalCacheDir
    )
}
