package com.example.firebasefirst;

public class Notes {
String TimeStamp;
String content;
String title;

    public Notes() {

    }

    public Notes(String timeStamp, String content, String title) {
        TimeStamp = timeStamp;
        this.content = content;
        this.title = title;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
