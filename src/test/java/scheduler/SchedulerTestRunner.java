package scheduler;

import com.google.common.collect.Sets;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import scheduler.job.DummyJob;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class SchedulerTestRunner {

    public static void main(String[] args) {

        Properties properties = getProperties();

        try {
            StdSchedulerFactory factory = new StdSchedulerFactory(properties);
            Scheduler scheduler = factory.getScheduler();
            scheduler.start();

            JobDetail job = JobBuilder.newJob()
                    .withIdentity("testJob")
                    .ofType(DummyJob.class)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("testTrigger")
                    .withSchedule(simpleSchedule()
                            .repeatForever()
                            .withIntervalInSeconds(5)).build();

            Trigger trigger10 = TriggerBuilder.newTrigger()
                    .withIdentity("testTrigger10")
                    .withSchedule(simpleSchedule()
                            .repeatForever()
                            .withIntervalInSeconds(60*5)).build();

            JobDetail job2 = JobBuilder.newJob()
                    .withIdentity("testJob2")
                    .ofType(DummyJob.class)
                    .build();

            Trigger trigger2 = TriggerBuilder.newTrigger()
                    .withIdentity("testTrigger2")
                    .withSchedule(cronSchedule("0 0/10 2-3 * * ?")).build();

            scheduler.scheduleJob(job, Sets.newHashSet(trigger, trigger10), true);
            scheduler.scheduleJob(job2, trigger2);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("quartz.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

}
