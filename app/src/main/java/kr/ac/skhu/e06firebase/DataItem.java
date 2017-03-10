package kr.ac.skhu.e06firebase;

import java.util.Date;
import java.text.SimpleDateFormat;

public class DataItem {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String title;
    private String body;
    private Date modifiedTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getModifiedTimeString() {
        return dateFormat.format(modifiedTime);
    }

    @Override
    public String toString() {
        return getTitle() + " " + getModifiedTimeString();
    }
}

