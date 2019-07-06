package com.teamsamst.binarystorage.service

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths

class StorageService private constructor(private val rootDirectory: Path) {
    companion object {
        val CLEANUP_TIME = 1000 * 60 * 60 * 24 * 3

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

        if(!file.exists()) {
            throw FileNotFoundException(file.absolutePath)
        }

        return file
    }

    fun cleanupOldFiles(currentPath: Path = rootDirectory) {
        val currentFileOrDir = currentPath.toFile()
        if (currentFileOrDir.isFile) {
            if (System.currentTimeMillis() - currentFileOrDir.lastModified() > CLEANUP_TIME) {
                currentFileOrDir.delete()
            }
        } else {
            currentFileOrDir.listFiles()?.forEach {
                cleanupOldFiles(it.toPath())
            }
        }
    }
}