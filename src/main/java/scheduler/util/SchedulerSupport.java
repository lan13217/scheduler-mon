package scheduler.util;

import scheduler.proxy.SchedulerService;
import scheduler.proxy.impl.QuartzSchedulerService;
import spark.Session;

import java.util.Properties;

public class SchedulerSupport {

    private static final String SESSION_KEY_SCHEDULER = "_SCHEDULER";

    public static SchedulerService connect(Properties properties) {
        SchedulerService service = new QuartzSchedulerService();
        service.connect(properties);
        return service;
    }

    public static void bind(Session session, SchedulerService schedulerService) {
        session.attribute(SESSION_KEY_SCHEDULER, schedulerService);
    }

    public static SchedulerService getScheduler(Session session) {
        SchedulerService scheduler = session.attribute(SESSION_KEY_SCHEDULER);
        if (scheduler == null) {
            throw new RuntimeException("Scheduler is not ready to use");
        }
        return scheduler;
    }
}
