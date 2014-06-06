package scheduler;

import com.google.gson.Gson;
import scheduler.dto.Success;
import scheduler.util.JsonTransformer;

import java.util.Properties;

import static scheduler.util.SchedulerSupport.bind;
import static scheduler.util.SchedulerSupport.connect;
import static scheduler.util.SchedulerSupport.getScheduler;
import static spark.Spark.*;
import static spark.SparkBase.staticFileLocation;

public class SchedulerMon {

    public static void main(String[] args) {

        Gson gson = new Gson();
        JsonTransformer jsonTransformer = new JsonTransformer(gson);

        staticFileLocation("/public");

        exception(RuntimeException.class, (e, req, res) -> {
            res.status(500);
            res.body("<b>" + e.getMessage() + "</b>");
        });

        post("/connect", "application/json", (req, res) -> {
            Properties properties = gson.fromJson(req.body(), Properties.class);
            bind(req.session(), connect(properties));
            return new Success();
        }, jsonTransformer);

        get("/triggers", "application/json", (req, res) -> {
            return getScheduler(req.session()).getTriggers(req.params("triggerName"));
        }, jsonTransformer);

        get("/jobs", "application/json", (req, res) -> {
            return getScheduler(req.session()).getJobs(req.params("jobName"));
        }, jsonTransformer);
    }


}
