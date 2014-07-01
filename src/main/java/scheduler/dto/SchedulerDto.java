package scheduler.dto;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public class SchedulerDto {
    private final String name;
    private final String instanceId;
    private final int executingJobs;

    public SchedulerDto(Scheduler scheduler) {
        try {
            name = scheduler.getSchedulerName();
            instanceId = scheduler.getSchedulerInstanceId();
            executingJobs = scheduler.getCurrentlyExecutingJobs().size();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public int getExecutingJobs() {
        return executingJobs;
    }
}
