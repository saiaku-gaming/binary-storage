package com.teamsamst.binarystorage

import com.teamsamst.binarystorage.filter.AuthFilter
import com.teamsamst.binarystorage.job.CleanupJob
import com.teamsamst.binarystorage.server.WebServer
import com.teamsamst.binarystorage.service.StorageService
import com.teamsamst.binarystorage.servlet.RootServlet
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory

object BinaryStorage {
    @JvmStatic
    fun main(args: Array<String>) {
        initServices()
        initJobs()
        startServer()
    }

    private fun initServices() {
        StorageService.init(System.getenv("BINARY_STORAGE_HOME"))
    }

    private fun initJobs() {
        StdSchedulerFactory().scheduler.apply {
            scheduleJob(
                    JobBuilder.newJob(CleanupJob::class.java)
                        .withIdentity("cleanup-job", "cleanup")
                        .build(),
                    TriggerBuilder.newTrigger()
                        .withIdentity("cron-trigger", "cleanup")
                        .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?"))
                        .forJob("cleanup-job", "cleanup")
                        .build()
            )
        }.start()
    }

    private fun startServer() {
        WebServer.init()
        WebServer.addJerseySpec(RootServlet::class.java)
        WebServer.addFilter(AuthFilter::class.java, "/*")
        WebServer.start(8899)
    }
}