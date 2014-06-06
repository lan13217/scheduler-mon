package scheduler.dto;

import java.util.Date;

public class TriggerDto {
    private final String group;
    private final String name;
    private final String description;
    private final Date previousFireTime;
    private final Date nextFireTime;

    public TriggerDto(String group, String name, String description, Date previousFireTime, Date nextFireTime) {
        this.group = group;
        this.name = name;
        this.description = description;
        this.previousFireTime = previousFireTime;
        this.nextFireTime = nextFireTime;
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

    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }
}
