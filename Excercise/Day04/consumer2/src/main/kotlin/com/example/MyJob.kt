package com.example

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class MyJob : Job {
    private val logger = LoggerFactory.getLogger(MyJob::class.java)

    override fun execute(context: JobExecutionContext) {
        val jobName = context.jobDetail.key.name
        logger.info("Job '$jobName' executed at ${LocalDateTime.now()}")
        // Add your job logic here
        try {
            // Simulate some work that might throw an exception
            if (jobName == "job2") {
                //throw RuntimeException("Simulated error in job2")
            }
        } catch (e: Exception) {
            logger.error("Error in job '$jobName': ${e.message}", e)
        }
    }
}