package cn.ac.lz233.tarnhelm.util.ktx

import cn.ac.lz233.tarnhelm.util.LogUtil
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

fun File.ensure(): File = if (this.exists()) {
    this
} else if (this.isDirectory) {
    this.mkdirs()
    this
} else {
    this.parentFile?.mkdirs()
    this.createNewFile()
    this
}

fun File.listFilesRecursively(): List<File> {
    val result = mutableListOf<File>()
    this.listFiles()?.forEach {
        if (it.isDirectory) {
            result += it.listFilesRecursively()
        } else {
            result += it
        }
    }
    return result
}

fun List<File>.listFilesRecursively(): List<File> {
    val result = mutableListOf<File>()
    this.forEach {
        result += it.listFilesRecursively()
    }
    return result
}

fun List<File>.zip(baseDir: File, output: File) {
    val zipOutputStream = ZipOutputStream(BufferedOutputStream(FileOutputStream(output.ensure())))
    this.forEach {
        val inputStream = BufferedInputStream(FileInputStream(it))
        // baseDir: /data/user/0/cn.ac.lz233.tarnhelm
        // it: /data/user/0/cn.ac.lz233.tarnhelm/shared_prefs/test.xml
        // it.toRelativeString(baseDir): shared_prefs/test.xml
        val zipEntry = ZipEntry(it.toRelativeString(baseDir))
        zipOutputStream.putNextEntry(zipEntry)
        inputStream.copyTo(zipOutputStream)
        inputStream.close()
        zipOutputStream.closeEntry()
    }
    zipOutputStream.close()
}

fun InputStream.unzip(outputDir: File) {
    val zipInputStream = ZipInputStream(BufferedInputStream(this))
    while (true) {
        val zipEntry = zipInputStream.nextEntry ?: break
        LogUtil._d(zipEntry.name)
        val outputStream = BufferedOutputStream(FileOutputStream(File(outputDir, zipEntry.name).ensure()))
        zipInputStream.copyTo(outputStream)
        zipInputStream.closeEntry()
        outputStream.close()
    }
}

fun File.unzip(outputDir: File) {
    FileInputStream(this).unzip(outputDir)
}