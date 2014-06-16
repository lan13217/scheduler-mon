package scheduler.dto;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.Date;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class ExecutingJobDto {
    private Optional<String> jobGroup;
    private String jobName;
    private Optional<String> triggerGroup;
    private String triggerName;
    private Date fireTime;
    private Date scheduledFireTime;
    private long jobRunTime;
    private JobDataMap mergedJobDataMap;

    public ExecutingJobDto(JobExecutionContext jobExecutionContext) {
        this.jobGroup = ofNullable(jobExecutionContext.getJobDetail().getKey().getGroup());
        this.jobName = jobExecutionContext.getJobDetail().getKey().getName();
        this.triggerGroup = ofNullable(jobExecutionContext.getTrigger().getKey().getGroup());
        this.triggerName = jobExecutionContext.getTrigger().getKey().getName();
        this.fireTime = jobExecutionContext.getFireTime();
        this.scheduledFireTime = jobExecutionContext.getScheduledFireTime();
        this.jobRunTime = jobExecutionContext.getJobRunTime();
        this.mergedJobDataMap = jobExecutionContext.getMergedJobDataMap();
    }

    public String getJobGroup() {
        return jobGroup.get();
    }

    public String getJobName() {
        return jobName;
    }

    public String getTriggerGroup() {
        return triggerGroup.get();
    }

    public String getTriggerName() {
        return triggerName;
    }

    public Date getFireTime() {
        return fireTime;
    }

    public Date getScheduledFireTime() {
        return scheduledFireTime;
    }

    public long getJobRunTime() {
        return jobRunTime;
    }

    public JobDataMap getMergedJobDataMap() {
        return mergedJobDataMap;
    }

    public boolean hasSameTrigger(TriggerDto trigger) {
        return triggerGroup.orElse("").equals(trigger.getGroup()) && triggerName.equals(trigger.getName());
    }

    public boolean isRunning() {
        return jobRunTime == -1;
    }
}
