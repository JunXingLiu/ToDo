package ca.nait.jliu73.tasklist.models;

import android.provider.SyncStateContract;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hank on 2019-03-12.
 */

public class TaskItem
{
    private long id;
    private String title;
    private String description;
    private String date;
    private boolean completed;

    public TaskItem(String title, String description) throws Exception
    {

        this.title = title;
        this.description = description;
        this.date = "\"01/01/1991\"";
        this.completed = false;


    }

    public TaskItem()
    {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
