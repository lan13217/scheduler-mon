package scheduler.dto;

import java.util.Date;

public class TimelineJobDto {
    private Date start;
    private Date end;
    private boolean isDuration;
    private String title;
    private String image;
    private String link;

    public TimelineJobDto(Date start, Date end, boolean isDuration, String title, String image, String link) {
        this.start = start;
        this.end = end;
        this.isDuration = isDuration;
        this.title = title;
        this.image = image;
        this.link = link;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public boolean isDuration() {
        return isDuration;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getLink() {
        return link;
    }
}
