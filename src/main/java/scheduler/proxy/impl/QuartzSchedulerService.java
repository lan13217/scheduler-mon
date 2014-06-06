package scheduler.proxy.impl;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import scheduler.dto.JobDetailDto;
import scheduler.dto.TriggerDto;
import scheduler.proxy.SchedulerService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class QuartzSchedulerService implements SchedulerService {

    private Scheduler scheduler;


    @Override
    public void connect(Properties properties) {

        properties.setProperty("org.quartz.scheduler.instanceName", "RmiQuartzScheduler");
        properties.setProperty("org.quartz.scheduler.instanceId", "NON_CLUSTERED");
        properties.setProperty("org.quartz.scheduler.rmi.proxy", "true");
        properties.setProperty("org.quartz.scheduler.rmi.registryHost", properties.getProperty("host"));
        properties.setProperty("org.quartz.scheduler.rmi.registryPort", properties.getProperty("port"));

        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory(properties);
            scheduler = schedulerFactory.getScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disconnect() {
        scheduler = null;
    }

    @Override
    public Collection<TriggerDto> getTriggers(String triggerName) {
        try {
            List<TriggerDto> triggerDtos = new ArrayList<>();
            if (triggerName != null) {
                triggerDtos.add(newTriggerDto(getScheduler().getTrigger(new TriggerKey(triggerName))));
            }

            getScheduler().getTriggerKeys(GroupMatcher.<TriggerKey>anyGroup()).forEach(triggerKey -> {
                // Have to have try catch block here because lamda actually uses annonymous instance.
                try {
                    triggerDtos.add(newTriggerDto(getScheduler().getTrigger(triggerKey)));
                } catch (SchedulerException e) {
                    throw new RuntimeException(e);
                }
            });
            return triggerDtos;
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<JobDetailDto> getJobs(String jobName) {
        try {
            List<JobDetailDto> jobDetailDtos = new ArrayList<>();
            if (jobName != null) {
                jobDetailDtos.add(newJobDetailDto(getScheduler().getJobDetail(new JobKey(jobName))));
            }

            getScheduler().getJobKeys(GroupMatcher.<JobKey>anyGroup()).forEach(jobKey -> {
                try {
                    jobDetailDtos.add(newJobDetailDto(getScheduler().getJobDetail(jobKey)));
                } catch (SchedulerException e) {
                    throw new RuntimeException(e);
                }
            });
            return jobDetailDtos;
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private Scheduler getScheduler() {
        if (scheduler == null) {
            throw new RuntimeException("Scheduler is not running. Try to reconnect the Scheduler with properties");
        }
        return scheduler;
    }

    private JobDetailDto newJobDetailDto(JobDetail jobDetail) {
        return new JobDetailDto(jobDetail.getKey().getGroup(), jobDetail.getKey().getName(),
                jobDetail.getDescription(), jobDetail.getJobClass().getName(),
                jobDetail.getJobDataMap().getWrappedMap());
    }

    private TriggerDto newTriggerDto(Trigger trigger) {
        return new TriggerDto(trigger.getKey().getGroup(), trigger.getKey().getName(), trigger.getDescription(),
                trigger.getPreviousFireTime(), trigger.getNextFireTime());
    }


}
