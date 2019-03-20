package ca.nait.jliu73.tasklist.DBHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Calendar;

import ca.nait.jliu73.tasklist.models.TaskItem;

/**
 * Created by Hank on 2019-03-12.
 */


public class TaskItemDAO
{
    private Context context;

    private SQLiteDatabase db;
    private DBManager manager;
    private String[] allCol = { DBManager.id,
                                DBManager.title,
                                DBManager.description,
                                DBManager.date,
                                DBManager.completed};


    public TaskItemDAO(Context context)
    {
        manager = new DBManager(context);

        try
        {
            open();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void open()
    {
        db = manager.getWritableDatabase();
    }

    public void close()
    {
        manager.close();
    }

    public TaskItem createTaskItem(String title, String description)
    {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentTime =  Calendar.getInstance().getTime();
        String strDate = dateFormat.format(currentTime);
        ContentValues values = new ContentValues();
        values.put(DBManager.title, title);
        values.put(DBManager.description, description);
        values.put(DBManager.date, strDate);
        values.put(DBManager.completed, 0);

        long insertId = db.insert(DBManager.taskItem, null, values);

        Cursor cursor = db.query(DBManager.taskItem, allCol, DBManager.id + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        TaskItem newTaskItem = null;
        try {
            newTaskItem = cursorToTaskItem(cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return  newTaskItem;

    }

    public void deleteTaskItem(TaskItem taskItem)
    {
        long id = taskItem.getId();
        db.delete(DBManager.taskItem, DBManager.id + " = " + id, null);
    }


    public void deleteTaskItemById(long id)
    {
        db.delete(DBManager.taskItem, DBManager.id + " = " + id, null);
    }

    public TaskItem getTaskItemById(long id)
    {
        TaskItem taskItem = null;
        Cursor cursor = db.query(DBManager.taskItem, allCol, DBManager.id + " = " + id, null, null, null, null);
        try {
            cursor.moveToFirst();
            taskItem = cursorToTaskItem(cursor);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return  taskItem;
    }

    public List<TaskItem> getTaskItemOfTag(String title)
    {
        List<TaskItem> listTaskItems = new ArrayList<TaskItem>();


        Cursor cursor = null;
        try {
            cursor = db.query(DBManager.taskItem, allCol, DBManager.title + " = ?", new String[] { String.valueOf(title)}, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            TaskItem taskItem = null;
            try {
                taskItem = cursorToTaskItem(cursor);
            } catch (Exception e) {
                e.printStackTrace();
            }
            listTaskItems.add(taskItem);
            cursor.moveToNext();
        }

        cursor.close();
        return listTaskItems;
    }

    private TaskItem cursorToTaskItem(Cursor cursor) throws Exception
    {
        TaskItem taskItem = new TaskItem();
        taskItem.setId(cursor.getLong(0));
        taskItem.setTitle(cursor.getString(1));
        taskItem.setDescription(cursor.getString(2));
        taskItem.setDate(cursor.getString(3));
        taskItem.setCompleted(cursor.getInt(4) > 0);

        return taskItem;
    }

    public void updateItemComplete(long id)
    {
        ContentValues values = new ContentValues();
        values.put(DBManager.completed, 1);
        db.update(DBManager.taskItem, values, "_id=" + id, null);
    }
}
