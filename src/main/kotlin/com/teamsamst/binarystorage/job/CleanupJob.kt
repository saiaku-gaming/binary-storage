package com.teamsamst.binarystorage.job

import com.teamsamst.binarystorage.service.StorageService
import org.quartz.Job
import org.quartz.JobExecutionContext

class CleanupJob : Job {
    private val storageService = StorageService.INSTANCE

    override fun execute(context: JobExecutionContext?) {
        storageService.cleanupOldFiles()
    }
}