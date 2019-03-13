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

    public TaskItem createTaskItem(String title, String description) throws Exception
    {
        DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
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
        TaskItem newTaskItem = cursorToTaskItem(cursor);
        cursor.close();
        return  newTaskItem;

    }

    public void deleteTaskItem(TaskItem taskItem)
    {
        long id = taskItem.getId();
        db.delete(DBManager.taskItem, DBManager.id + " = " + id, null);
    }

    public List<TaskItem> getTaskItemOfTag(String title) throws Exception
    {
        List<TaskItem> listTaskItems = new ArrayList<TaskItem>();

        Cursor cursor = db.query(DBManager.taskItem, allCol, DBManager.title + " = " + title, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            TaskItem taskItem = cursorToTaskItem(cursor);
            listTaskItems.add(taskItem);
            cursor.moveToNext();
        }

        cursor.close();
        return listTaskItems;
    }

    private TaskItem cursorToTaskItem(Cursor cursor) throws Exception
    {
        Date date = new SimpleDateFormat("dd/mm/yyyy").parse(cursor.getString(3));

        TaskItem taskItem = new TaskItem();
        taskItem.setId(cursor.getLong(0));
        taskItem.setTitle(cursor.getString(1));
        taskItem.setDescription(cursor.getString(2));
        taskItem.setDate(date);
        taskItem.setCompleted(cursor.getInt(4) > 0 ? true : false);

        return taskItem;
    }
}
