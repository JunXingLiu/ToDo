package ca.nait.jliu73.tasklist.models;

/**
 * Created by Hank on 2019-03-12.
 */

public class TaskTag
{
    private long id;
    private String title;

    public TaskTag(String title)
    {
        this.title = title;
    }

    public TaskTag()
    {

    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
