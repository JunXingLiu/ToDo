package ca.nait.jliu73.tasklist.models;

import java.util.Date;

/**
 * Created by Hank on 2019-03-12.
 */

public class TaskItem
{
    private String id;
    private String title;
    private String description;
    private Date date;
    private boolean completed;

    public TaskItem(String id, String title, String description, Date date, boolean completed)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.completed = completed;
    }

    public TaskItem()
    {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
