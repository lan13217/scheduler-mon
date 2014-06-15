package scheduler.proxy;

import scheduler.dto.ExecutingJobDto;
import scheduler.dto.JobDetailDto;
import scheduler.dto.TriggerDto;

import java.util.Collection;

public interface SchedulerService {

    void connect(String host, Integer port);

    void disconnect();

    Collection<TriggerDto> getTriggers(String triggerName);

    Collection<JobDetailDto> getJobs(String jobName);

    Collection<ExecutingJobDto> getExecutingJobs();
}
