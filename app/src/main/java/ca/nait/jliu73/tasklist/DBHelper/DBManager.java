package ca.nait.jliu73.tasklist.DBHelper;

import android.content.Context;
import android.database.Cursor;
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
    static final int DB_Version = 3;

    public static final String taskItem = "TaskItem";
    public static final String id = BaseColumns._ID;
    public static final String title = "Title";
    public static final String description = "Description";
    public static final String date = "Date";
    public static final String completed = "Completed";

    public static final String taskTag = "TaskTag";
    public static final String headerId = BaseColumns._ID;
    public static final String headerTitle = "Title";

    public static final String taskArchive = "TaskArchive";
    public static final String archiveId = BaseColumns._ID;
    public static final String post_date = "PostedDate";
    public static final String listTitle = "List_Title";
    public static final String contents = "Content";
    public static final String completedFlag = "Completed_Flag";

    static final String Create_Header = "CREATE TABLE " + taskTag + "("
                                        + headerId + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + headerTitle + " TEXT "
                                        + ");";

    static final String Create_Detail = "Create Table " + taskItem + "("
                                        + id + " Integer Primary Key AUTOINCREMENT, "
                                        + title + " Text, "
                                        + description + " Text, "
                                        + date  + " Text, "
                                        + completed + " Integer "
                                        + ");";

    static final String Detail_Archive = "Create Table " + taskArchive + "("
            + archiveId + " Integer Primary Key AUTOINCREMENT, "
            + post_date + " Text, "
            + listTitle + " Text, "
            + contents  + " Text, "
            + completedFlag + " Text "
            + ");";


    public DBManager(Context context)
    {
        super(context, DB_Name, null, DB_Version);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {

        db.execSQL(Create_Header);
        db.execSQL(Create_Detail);
        db.execSQL(Detail_Archive);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + taskItem);
        db.execSQL("DROP TABLE IF EXISTS " + taskTag);
        db.execSQL("DROP TABLE IF EXISTS " + taskArchive);

        onCreate(db);
    }

    public Cursor fetchGroup()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + taskTag;
        return db.rawQuery(query, null);
    }

    public Cursor fetchChildren(String title)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM TaskItem WHERE Title = '" + title + "'";
        return  db.rawQuery(query, null);
    }

}
