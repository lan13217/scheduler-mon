package scheduler.dto;

import java.util.Map;

public class JobDetailDto {
    private final String group;
    private final String name;
    private final String description;
    private String jobClassName;
    private final Map<String, Object> jobDataMap;

    public JobDetailDto(String group, String name, String description,
                        String jobClassName, Map<String, Object> jobDataMap) {
        this.group = group;
        this.name = name;
        this.description = description;
        this.jobClassName = jobClassName;
        this.jobDataMap = jobDataMap;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getJobClassName() {
        return jobClassName;
    }

    public Map<String, Object> getJobDataMap() {
        return jobDataMap;
    }
}
