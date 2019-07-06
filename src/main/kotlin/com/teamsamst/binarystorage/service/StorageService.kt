package com.teamsamst.binarystorage.service

import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.ws.rs.core.StreamingOutput

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

    fun getFile(path: String, name: String): StreamingOutput {
        val file = rootDirectory.resolve(path).resolve(name).toFile()

        if(!file.exists()) {
            throw Exception()
        }

        return StreamingOutput {
            val bytes = Files.readAllBytes(file.toPath())
            it.write(bytes)
            it.flush()
        }
    }

    fun cleanupOldFiles(currentPath: Path = rootDirectory) {
        currentPath.toFile().run {
            if(isFile) {
                if(System.currentTimeMillis() - lastModified() > CLEANUP_TIME) {
                    delete()
                }
            } else {
                listFiles().forEach {
                    cleanupOldFiles(it.toPath())
                }
            }
        }
    }
}