package scheduler.dto;

import java.util.Date;

public class TimelineJobDto {

    private final int id;
    private final String group;
    private final String content;
    private final Date start;
    private final Date end;
    private final String type;

    public TimelineJobDto(int id, String group, String content, Date start, Date end, String type) {
        this.id = id;
        this.group = group;
        this.content = content;
        this.start = start;
        this.end = end;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public String getContent() {
        return content;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String getType() {
        return type;
    }
}
