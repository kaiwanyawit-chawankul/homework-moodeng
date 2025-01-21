package com.example

import com.typesafe.config.ConfigFactory
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import org.slf4j.LoggerFactory

//calculate protobuff and save into cassandra
fun main() {
    val logger = LoggerFactory.getLogger("Main")
    val config = ConfigFactory.load()
    val schedulerFactory = StdSchedulerFactory()
    val scheduler = schedulerFactory.scheduler
    scheduler.start()

    val jobsConfig = config.getConfig("jobs")
    println("Jobs config: $jobsConfig") // Print the entire jobs config

    jobsConfig.root().forEach { (jobName, configValue) -> // Correct way to iterate over job names
    //jobsConfig.entrySet().forEach { (jobName, configValue) -> // Corrected line: We don't need the ConfigValue
        try {
            //println("Job Name: $jobName, Config Value: $configValue")
            // Correct way to get the cron expression: Access it directly as a string
            val cronExpression = jobsConfig.getString("$jobName.cronExpression")

            val job = JobBuilder.newJob(MyJob::class.java)
                .withIdentity(jobName, "group1")
                .build()

            val trigger = TriggerBuilder.newTrigger()
                .withIdentity("${jobName}Trigger", "group1")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build()

            scheduler.scheduleJob(job, trigger)
            logger.info("Scheduled job '$jobName' with cron expression: $cronExpression")
        } catch (e: Exception) {
            logger.error("Error scheduling job '$jobName': ${e.message}", e)
        }
    }

    // Keep the main thread alive (in a real application, you'd have a different mechanism)
    Thread.sleep(60000) // Keep running for 1 minute for demonstration
    scheduler.shutdown()
}