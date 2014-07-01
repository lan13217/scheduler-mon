package scheduler.util;

import spark.Request;
import spark.Response;

import java.util.Map;

@FunctionalInterface
public interface RequestJsonProvider {

    Object apply(Map<String, String> params, Request req, Response res);
}
