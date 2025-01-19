import akka.actor.{Actor, ActorSystem, Props}
import org.quartz._
import org.quartz.impl.StdSchedulerFactory
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorSystem

// Define the configuration case class
case class CronJobConfig(cronExpression: String)

// Create your Akka Actor
class CronJobActor extends Actor {
  override def receive: Receive = {
    case "run" =>
      // Simulate task execution
      println(s"Running cron job at: ${System.currentTimeMillis()}")
  }
}

// Create a Quartz job that will trigger the Akka actor
class AkkaJobWrapper(actorSystem: ActorSystem, actorProps: Props) extends Job {
  override def execute(context: JobExecutionContext): Unit = {
    // Create the actor
    val cronJobActor = actorSystem.actorOf(actorProps)
    cronJobActor ! "run"
  }
}

object Main extends App {
  // Load configuration from application.conf
  val cronConfig = ConfigFactory.load().getString("akka.quartz.cron-expression")

  // Create ActorSystem
  val system: ActorSystem = ActorSystem("CronJobSystem")

  // Create Props for your CronJobActor
  val cronJobActorProps = Props[CronJobActor]

  // Define Quartz job and trigger using the cron expression
  val job = JobBuilder.newJob(classOf[AkkaJobWrapper])
    .usingJobData("actorSystem", system.toString) // Optionally pass any parameters to the job
    .usingJobData("actorProps", cronJobActorProps.toString) // Pass the Props for the actor
    .build()

  val trigger = TriggerBuilder.newTrigger()
    .withIdentity("cronTrigger")
    .withSchedule(CronScheduleBuilder.cronSchedule(cronConfig))
    .build()

  // Create a scheduler
  val scheduler = new StdSchedulerFactory().getScheduler

  // Start the scheduler
  scheduler.start()

  // Schedule the job
  scheduler.scheduleJob(job, trigger)

  println(s"Scheduled cron job with expression: $cronConfig")

  // Keep the application running
  system.whenTerminated
}
