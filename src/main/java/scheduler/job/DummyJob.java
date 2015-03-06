package scheduler.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DummyJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println(context.getJobDetail().getJobDataMap().get("say")+"Executed dummy job...:"+context.getJobDetail().getKey());
    }
}
