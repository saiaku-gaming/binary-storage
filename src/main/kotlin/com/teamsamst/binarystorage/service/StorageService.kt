package com.teamsamst.binarystorage.service

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths

class StorageService private constructor(private val rootDirectory: Path) {
    companion object {
        const val CLEANUP_TIME = 1000 * 60 * 60 * 24 * 3
        const val MAX_SAVED_COPIES = 20
        lateinit var INSTANCE: StorageService
            private set

        fun init(rootDirectory: String) {
            INSTANCE = StorageService(Paths.get(rootDirectory))
        }
    }

    fun storeFile(inputStream: InputStream, path: String, name: String) {
        val file = rootDirectory.resolve(path).toFile().apply {
            mkdirs()
        }.toPath().resolve(name).toFile().apply {
            createNewFile()
        }

        val outputStream = FileOutputStream(file)

        var read = 0
        val bytes = ByteArray(1024)
        outputStream.use {
            while(read != -1) {
                read = inputStream.read(bytes)
                if(read == -1) {
                    break
                }
                it.write(bytes, 0, read)
            }
        }
    }

    fun getFile(path: String, name: String): File {
        val file = rootDirectory.resolve(path).resolve(name).toFile()

        if (!file.exists()) {
            throw FileNotFoundException(file.absolutePath)
        }

        return file
    }

    fun cleanupOldFiles(path: Path = rootDirectory) {
        val allFiles = File(path.toString())
                .walkTopDown()
                .filter { it.isFile }

        val oldFiles = allFiles.filter { (System.currentTimeMillis() - it.lastModified()) > CLEANUP_TIME }

        deleteOverMax(oldFiles.filter { it.name.contains("Develop") })
        deleteOverMax(oldFiles.filter { it.name.contains("DebugGame") })
        deleteOverMax(oldFiles.filter { !(it.name.contains("Develop") || it.name.contains("DebugGame")) })
    }

    private fun deleteOverMax(files: Sequence<File>) {
        val sorted = files.sorted().toList().reversed()
        if (sorted.size > MAX_SAVED_COPIES) {
            sorted.subList(MAX_SAVED_COPIES, sorted.size - 1).forEach { it.delete() }
        }
    }
}