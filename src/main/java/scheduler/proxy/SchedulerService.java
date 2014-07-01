package scheduler.proxy;

import scheduler.dto.*;

import java.util.Collection;

public interface SchedulerService {

    void connect(String host, Integer port);

    void disconnect();

    Collection<SchedulerDto> getSchedulers();

    Collection<TriggerDto> getTriggers(String triggerName);

    Collection<JobDetailDto> getJobs(String jobName);

    Collection<ExecutingJobDto> getExecutingJobs();

    Collection<TimelineJobDto> getTimelineJobs();
}
