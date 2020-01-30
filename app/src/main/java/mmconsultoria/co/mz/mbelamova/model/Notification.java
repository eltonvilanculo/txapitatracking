package mmconsultoria.co.mz.mbelamova.model;

import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("to")
    public String to;
    @SerializedName("title")
    public String title;
    @SerializedName("message")
    public String subject;
    @SerializedName("project_id")
    private String projectId;
    private String error;
    @SerializedName("message_id")
    private String messageId;

    @SerializedName("data")
    public SubData data;

    public Notification() {
    }

    public Notification(String to, String title, String subject, SubData data) {
        this.to = to;
        this.title = title;
        this.subject = subject;
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = "/topics/" + to;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public SubData getData() {
        return data;
    }

    public void setData(SubData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
                "to:'" + to + '\'' +
                ", title:'" + title + '\'' +
                ", subtitle:'" + subject + '\'' +
                ", data:" + data +
                '}';
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}