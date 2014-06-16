package scheduler.util;

import com.google.gson.Gson;
import scheduler.proxy.SchedulerService;
import scheduler.proxy.impl.QuartzSchedulerService;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.Map;
import java.util.function.BiFunction;

public class SchedulerSupport {

    private static final String SESSION_KEY_SCHEDULER = "_SCHEDULER";

    public static SchedulerService connect(String host, Integer port) {
        SchedulerService service = new QuartzSchedulerService();
        service.connect(host, port);
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
    
    public static Route fromJson(BiFunction<Map<String, String>, Response, Object> function) {
        return (req, res) -> {
            Map params = new Gson().fromJson(req.body(), Map.class);
            return function.apply(params, res);
        };
    }
}