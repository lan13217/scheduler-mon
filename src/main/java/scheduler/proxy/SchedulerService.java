package scheduler.proxy;

import scheduler.dto.JobDetailDto;
import scheduler.dto.TriggerDto;

import java.util.Collection;
import java.util.Properties;

public interface SchedulerService {

    void connect(Properties properties);

    void disconnect();

    Collection<TriggerDto> getTriggers(String triggerName);

    Collection<JobDetailDto> getJobs(String jobName);
}
