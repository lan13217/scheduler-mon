package scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import scheduler.job.DummyJob;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class SchedulerTestRunner {

    public static void main(String[] args) {

        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("quartz.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            StdSchedulerFactory factory = new StdSchedulerFactory(properties);
            Scheduler scheduler = factory.getScheduler();
//            scheduler.standby();
            scheduler.start();

            JobDetail job = JobBuilder.newJob()
                    .withIdentity("testJob")
                    .ofType(DummyJob.class)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("testTrigger")
                    .withSchedule(simpleSchedule()
                            .repeatForever()
                            .withIntervalInSeconds(10)
                            .withMisfireHandlingInstructionIgnoreMisfires()).build();

            scheduler.scheduleJob(job, trigger);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }

}
