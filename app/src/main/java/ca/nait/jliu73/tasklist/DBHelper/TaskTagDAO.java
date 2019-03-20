package ca.nait.jliu73.tasklist.DBHelper;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


import ca.nait.jliu73.tasklist.models.TaskItem;
import ca.nait.jliu73.tasklist.models.TaskTag;

/**
 * Created by Hank on 2019-03-12.
 */

public class TaskTagDAO
{
    private SQLiteDatabase db;
    private DBManager manager;
    private Context context;
    private String[] allCol = {DBManager.headerId,
                                DBManager.headerTitle};

    public TaskTagDAO(Context context)
    {
        this.context = context;
        manager = new DBManager(context);
        try
        {
            open();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void open() throws SQLException
    {
        db = manager.getWritableDatabase();
    }

    public void close()
    {
        manager.close();
    }

    public TaskTag createTag(String headerTitle)
    {
        ContentValues values = new ContentValues();
        values.put(DBManager.headerTitle, headerTitle);
        long insertID = db.insert(DBManager.taskTag, null, values);
        Cursor cursor = db.query(DBManager.taskTag, allCol, DBManager.headerId + " = " + insertID, null, null, null, null);

        cursor.moveToFirst();

        TaskTag newTaskTag = cursorToTaskTag(cursor);
        cursor.close();
        return newTaskTag;

    }

    public void deleteTask(TaskTag taskTag)
    {
            String title = taskTag.getTitle();
            long headerId = taskTag.getId();

            TaskItemDAO taskItemDAO = new TaskItemDAO(context);
            List<TaskItem> listOfTaskItems = taskItemDAO.getTaskItemOfTag(title);
            if(listOfTaskItems != null && !listOfTaskItems.isEmpty())
            {
                for(TaskItem item : listOfTaskItems)
                {
                    taskItemDAO.deleteTaskItem(item);
                }
            }

            db.delete(DBManager.taskTag, DBManager.headerId + " = " + headerId, null);
    }

    public List<TaskTag> getAllTag()
    {
        List<TaskTag> listOfTags = new ArrayList<TaskTag>();

        Cursor cursor = db.query(DBManager.taskTag, allCol, null, null, null, null, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                TaskTag taskTag = cursorToTaskTag(cursor);
                listOfTags.add(taskTag);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listOfTags;
    }

    public TaskTag getTaskTagById(long id)
    {
        Cursor cursor = db.query(DBManager.taskTag, allCol, DBManager.headerId + " = ?", new String[] {String.valueOf(id)}, null, null, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        TaskTag taskTag = cursorToTaskTag(cursor);
        return taskTag;
    }

    protected TaskTag cursorToTaskTag(Cursor cursor)
    {
        TaskTag taskTag = new TaskTag();
        taskTag.setId(cursor.getLong(0));
        taskTag.setTitle(cursor.getString(1));
        return taskTag;
    }

}
