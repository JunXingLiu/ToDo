package ca.nait.jliu73.tasklist.DBHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Hank on 2019-03-12.
 */

public class DBManager extends SQLiteOpenHelper
{
    static final String TAG = "DBManager";
    static final String DB_Name = "task.db";
    static final int DB_Version = 1;

    static final String taskItem = "TaskItem";
    static final String id = BaseColumns._ID;
    static final String title = "Title";
    static final String description = "Description";
    static final String date = "Date";
    static final String completed = "Completed";

    static final String taskTag = "TaskTag";
    static final String headerId = BaseColumns._ID;
    static final String headerTitle = "Title";

    static final String Create_Header = "CREATE TABLE " + taskTag
            + "(" + headerId + " INTEGER PRIMARY KEY," + headerTitle + " TEXT" + ")";

    static final String Create_Detail = String.format("CREATE TABLE %s(" +
            "%s INTEGER PRIMARY KEY, " +
            "%s TEXT REFERENCES %s(%s), " +
            "%s TEXT, " +
            "%s TEXT," +
            "%s INTEGER)", taskItem, id, title, taskTag, headerTitle, description, date, completed);


    public DBManager(Context context)
    {
        super(context, DB_Name, null, DB_Version);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(Create_Header);

        Log.d(TAG, "HERE");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + taskItem);
        db.execSQL("DROP TABLE IF EXISTS " + taskTag);

        onCreate(db);

    }
}
