package ca.nait.jliu73.tasklist.activities;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;

import ca.nait.jliu73.tasklist.DBHelper.DBManager;
import ca.nait.jliu73.tasklist.R;

/**
 * Created by jliu73 on 3/20/2019.
 */

public class ListViewAdapater extends SimpleCursorAdapter
{
    static final String[] columns = {DBManager.listTitle, DBManager.contents, DBManager.completedFlag, DBManager.post_date};
    static final int[] ids = {R.id.list_title, R.id.content, R.id.complete, R.id.date};
    public ListViewAdapater(Context context, Cursor cursor)
    {
        super(context, R.layout.list_view_row, cursor, columns, ids);
    }

    @Override
    public void bindView(View row, Context context, Cursor cursor)
    {
        super.bindView(row, context, cursor);
    }
}
