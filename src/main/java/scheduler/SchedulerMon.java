package scheduler;

import com.google.gson.Gson;

import scheduler.dto.Success;
import scheduler.util.JsonTransformer;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static scheduler.util.SchedulerSupport.*;
import static spark.Spark.*;
import static spark.SparkBase.setPort;
import static spark.SparkBase.staticFileLocation;

public class SchedulerMon {

    public static void main(String[] args) {

        Gson gson = new Gson();
        JsonTransformer jsonTransformer = new JsonTransformer(gson);

        setPort(9090);
        staticFileLocation("/public");

        exception(RuntimeException.class, (e, req, res) -> {
            res.status(500);
            res.body("<b>" + e.getMessage() + "</b>");
        });

        post("/connect", "application/json", fromJson((params, req, res) -> {
            bind(req.session(), connect(valueOf(params.get("host")), parseInt(valueOf(params.get("port")))));
            return new Success();
        }), jsonTransformer);

        get("/schedulers", "application/json", (req, res) -> getScheduler(req.session())
                .getSchedulers(), jsonTransformer);

        get("/triggers/:triggerName", "application/json", (req, res) -> getScheduler(req.session())
                .getTriggers(req.params("triggerName")), jsonTransformer);

        get("/triggers", "application/json", (req, res) -> getScheduler(req.session())
                .getTriggers(null), jsonTransformer);

        get("/jobs", "application/json", (req, res) -> getScheduler(req.session())
                .getJobs(null), jsonTransformer);

        get("/executingJobs", "application/json", (req, res) -> getScheduler(req.session())
                .getExecutingJobs(), jsonTransformer);
    }
}
