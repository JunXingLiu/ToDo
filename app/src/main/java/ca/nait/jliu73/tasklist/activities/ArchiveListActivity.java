package ca.nait.jliu73.tasklist.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import ca.nait.jliu73.tasklist.DBHelper.DBManager;
import ca.nait.jliu73.tasklist.R;

/**
 * Created by jliu73 on 3/20/2019.
 */

public class ArchiveListActivity extends BaseActivity
{
    ListViewAdapater adapter;
    ListView listView;
    Cursor cursor;
    SQLiteDatabase database;
    DBManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_archive);
        listView = (ListView)findViewById(R.id.lv_archive);
        manager = new DBManager(this);
        database = manager.getReadableDatabase();
    }

    @Override
    protected void onResume()
    {
        cursor = database.query(DBManager.taskArchive, null, null, null, null, null, null);
        startManagingCursor(cursor);
        adapter = new ListViewAdapater(this, cursor);
        listView.setAdapter(adapter);
        super.onResume();
    }
}
