package scheduler.proxy.impl;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import scheduler.dto.ExecutingJobDto;
import scheduler.dto.JobDetailDto;
import scheduler.dto.TriggerDto;
import scheduler.proxy.SchedulerService;

import java.util.Collection;
import java.util.Properties;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static scheduler.util.Exceptions.catchOf;
import static scheduler.util.Exceptions.tryOf;

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
            if (triggerName != null) {
                return newArrayList(newTriggerDto(getScheduler().getTrigger(new TriggerKey(triggerName))));
            } else {
                return getScheduler().getTriggerKeys(GroupMatcher.<TriggerKey>anyGroup())
                        .stream()
                        .map(tryOf(triggerKey -> newTriggerDto(getScheduler().getTrigger(triggerKey))))
                        .filter(tried -> tried.isSuccess())
                        .map(tried -> tried.get())
                        .collect(toList());

            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<JobDetailDto> getJobs(String jobName) {
        try {
            if (jobName != null) {
                return newArrayList(newJobDetailDto(getScheduler().getJobDetail(new JobKey(jobName))));
            } else {
                return getScheduler().getJobKeys(GroupMatcher.<JobKey>anyGroup())
                        .stream()
                        .map(catchOf(
                                jobKey -> newJobDetailDto(getScheduler().getJobDetail(jobKey)),
                                exception -> {throw new RuntimeException(exception);}))
                        .collect(toList());
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<ExecutingJobDto> getExecutingJobs() {
        try {
            return getScheduler().getCurrentlyExecutingJobs()
                    .stream()
                    .map(ExecutingJobDto::new)
                    .collect(toList());
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
