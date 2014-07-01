package scheduler.proxy.impl;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import scheduler.dto.*;
import scheduler.proxy.SchedulerService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static scheduler.util.Exceptions.catchOf;
import static scheduler.util.Exceptions.tryOf;

public class QuartzSchedulerService implements SchedulerService {

    private Scheduler scheduler;
    private Collection<Scheduler> schedulers = new ArrayList<>();
    private StdSchedulerFactory schedulerFactory;

    public QuartzSchedulerService() {
        this.schedulerFactory = new StdSchedulerFactory();
    }

    @Override
    public void connect(String host, Integer port) {
        try {
            schedulerFactory.initialize(getProperties(host, port));
            this.schedulers = schedulerFactory.getAllSchedulers();
            this.scheduler = schedulerFactory.getScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disconnect() {
        scheduler = null;
    }

    @Override
    public Collection<SchedulerDto> getSchedulers() {
        return schedulers.stream().map(SchedulerDto::new).collect(Collectors.toList());
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

    @Override
    public Collection<TimelineJobDto> getTimelineJobs() {
        Collection<JobDetailDto> jobs = getJobs(null);
        Collection<ExecutingJobDto> executingJobs = getExecutingJobs();

        List<TimelineJobDto> dtos = newArrayList();
        for (JobDetailDto job : jobs) {
            try {
                List<? extends Trigger> triggers = getScheduler().getTriggersOfJob(
                        new JobKey(job.getName(), job.getGroup()));
                for (Trigger trigger : triggers) {
                    dtos.add(new TimelineJobDto(job.getName(),
                            trigger.getKey().getName(),
                            trigger.getNextFireTime(), null, "box"));
                }
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }

        return dtos;
    }

    private Scheduler getScheduler() {
        if (scheduler == null) {
            throw new RuntimeException("Scheduler is not running. Try to reconnect the Scheduler with properties");
        }
        return scheduler;
    }

    private JobDetailDto newJobDetailDto(JobDetail jobDetail) {
        return new JobDetailDto(jobDetail.getKey().getGroup(),
                jobDetail.getKey().getName(),
                jobDetail.getDescription(),
                jobDetail.getJobClass().getName(),
                jobDetail.getJobDataMap().getWrappedMap());
    }

    private TriggerDto newTriggerDto(Trigger trigger) {
        return new TriggerDto(trigger.getKey().getGroup(),
                trigger.getKey().getName(),
                trigger.getDescription(),
                trigger.getPreviousFireTime(),
                trigger.getNextFireTime());
    }

    private Properties getProperties(String host, Integer port) {
        Properties properties = new Properties();
        properties.setProperty("org.quartz.scheduler.instanceName", "RmiQuartzScheduler");
        properties.setProperty("org.quartz.scheduler.instanceId", "NON_CLUSTERED");
        properties.setProperty("org.quartz.scheduler.rmi.proxy", "true");
        properties.setProperty("org.quartz.scheduler.rmi.registryHost", host);
        properties.setProperty("org.quartz.scheduler.rmi.registryPort", port.toString());
        return properties;
    }
}
