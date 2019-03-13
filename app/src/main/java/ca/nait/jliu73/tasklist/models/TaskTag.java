package ca.nait.jliu73.tasklist.models;

/**
 * Created by Hank on 2019-03-12.
 */

public class TaskTag
{
    private String id;
    private String title;

    public TaskTag(String id, String title)
    {
        this.id = id;
        this.title = title;
    }

    public TaskTag()
    {

    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
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
