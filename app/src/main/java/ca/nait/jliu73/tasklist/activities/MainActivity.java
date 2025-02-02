package ca.nait.jliu73.tasklist.activities;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import ca.nait.jliu73.tasklist.DBHelper.DBManager;
import ca.nait.jliu73.tasklist.DBHelper.TaskItemDAO;
import ca.nait.jliu73.tasklist.DBHelper.TaskTagDAO;

import ca.nait.jliu73.tasklist.InputDialog;

import ca.nait.jliu73.tasklist.R;
import ca.nait.jliu73.tasklist.models.TaskItem;

public class MainActivity extends BaseActivity implements InputDialog.InputDialogListener, SharedPreferences.OnSharedPreferenceChangeListener
{
    private FloatingActionButton fab;
    private DBManager manager;
    private TaskTagDAO tagDao;
    private TaskItemDAO itemDAO;
    private int lastPosition = -1;
    private FloatingActionButton add_fab;
    private SQLiteDatabase db;
    private String groupTag = null;
    private ExpandableListAdapter adapter;
    private String mode = "";
    private long idToComplete;
    View mainView;
    SharedPreferences prefs;
    String size;
    float intSize;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable_list);
        if(Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        manager = new DBManager(this);
        manager.getWritableDatabase();
        tagDao = new TaskTagDAO(this);
        itemDAO = new TaskItemDAO(this);


        fillData();

        add_fab = (FloatingActionButton)findViewById(R.id.fab_child);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openDialog();

            }
        });


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        mainView = findViewById(R.id.main_screen);

        int color = prefs.getInt("pref_color",0xffff0000);

        size = prefs.getString("pref_font", "15");


        mainView.setBackgroundColor(color);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            ActionBar bar = this.getSupportActionBar();
            bar.setBackgroundDrawable(new ColorDrawable(color));
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);

        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        int bgColor = prefs.getInt("pref_color", 0xffff0000);
        mainView.setBackgroundColor(bgColor);

        intSize = Float.parseFloat(size.substring(0,2));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            ActionBar bar = this.getSupportActionBar();
            bar.setBackgroundDrawable(new ColorDrawable(bgColor));
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(bgColor);
            TextView tv = (TextView)this.findViewById(R.id.tv_group);
            tv.setTextSize(intSize);
        }

    }

    public void openDialog()
    {
        InputDialog input = new InputDialog();
        input.show(getSupportFragmentManager(), "Input Dialog");
    }

    @Override
    public void applyText(String tag)
    {
        if(mode.equals("edit"))
        {
            itemDAO.updateItemDesc(idToComplete, tag);
            mode = "";
        }
        else if(groupTag != null && !groupTag.isEmpty() && !tag.isEmpty())
        {
            itemDAO.createTaskItem(groupTag, tag);
            groupTag = null;
        }
        else if (!tag.isEmpty())
        {
            tagDao.createTag(tag);
        }
        fillData();
    }

    private AlertDialog AskComplete(long id)
    {
        idToComplete = id;

        AlertDialog confirmationDialong =new AlertDialog.Builder(this)
                .setTitle("Edit or Mark as Complete")
                .setMessage("Do you want edit this item or mark as complete?")
                .setIcon(R.drawable.check)
                .setPositiveButton("Mark as Complete", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {

                        itemDAO.updateItemComplete(idToComplete);
                        fillData();
                    }
                })
                .setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        TaskItem item = itemDAO.getTaskItemById(idToComplete);
                        mode = "edit";
                        openDialog();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {

                        dialog.dismiss();

                    }
                })
                .create();
        return confirmationDialong;
    }

    private AlertDialog AskOption(long id)
    {
        final long idToDelete = id;

        AlertDialog confirmationDialong =new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.delete)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        tagDao.deleteTask(tagDao.getTaskTagById(idToDelete));
                        fillData();
                        add_fab.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {

                        dialog.dismiss();
                    }
                })
                .create();

        return confirmationDialong;
    }

    private AlertDialog AskArchive(long id)
    {
        final long idToEdit = id;

        AlertDialog confirmationDialong =new AlertDialog.Builder(this)
                .setTitle("Archive or Delete")
                .setMessage("Do you want to Archive or Delete this time?")
                .setIcon(R.drawable.archive)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                            itemDAO.deleteTaskItemById(idToEdit);
                            fillData();
                    }
                })
                .setNeutralButton("Archive", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TaskItem item = itemDAO.getTaskItemById(idToEdit);
                        String completed = item.isCompleted() ? "Completed" : "Incomplete";
                        postToTask(item.getTitle(), item.getDescription(), completed, item.getDate());
                        itemDAO.deleteTaskItem(item);
                        fillData();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {

                        dialog.dismiss();
                    }
                })
                .create();

        return confirmationDialong;
    }

    private void fillData()
    {
        Cursor groupCursor = manager.fetchGroup();
        this.startManagingCursor(groupCursor);
        groupCursor.moveToFirst();
        groupCursor.getPosition();

        final ExpandableListView elv = (ExpandableListView)this.findViewById(R.id.el_all_tags);
        adapter = new ExpandableListAdapter(groupCursor,
                                                              this,
                                                              R.layout.group_layout,
                                                              R.layout.child_layout,
                                                              new String[] {"Title"},
                                                              new int[] {R.id.tv_group},
                                                              new String[] {"Description", "Completed"},
                                                              new int[] {R.id.tv_child, R.id.tv_child_complete});
        elv.setAdapter(adapter);

        elv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id)
            {
                if(ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
                {
                    int childPos = ExpandableListView.getPackedPositionChild(id);
                    AlertDialog diaBox = AskArchive(childPos);
                    diaBox.show();
                }
                else if(ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP)
                {
                    int groupPos = ExpandableListView.getPackedPositionGroup(id);
                    AlertDialog diaBox = AskOption(groupPos);
                    diaBox.show();
                }
                return false;
            }
        });

        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                TaskItem item = itemDAO.getTaskItemById(id);
                if(!item.isCompleted())
                {
                    AlertDialog dialog = AskComplete(id);
                    dialog.show();
                }
                return false;
            }
        });

        elv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition)
            {
                if (lastPosition != -1
                        && groupPosition != lastPosition)
                {
                    elv.collapseGroup(lastPosition);
                }
                lastPosition = groupPosition;
                add_fab.setVisibility(View.VISIBLE);
                add_fab.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        TextView tv = (TextView)(adapter.getGroupView(lastPosition, true, null, null)).findViewById(R.id.tv_group);
                        groupTag = tv.getText().toString();
                        openDialog();
                    }
                });
            }
        });
    }

    public class ExpandableListAdapter extends SimpleCursorTreeAdapter
    {
        public ExpandableListAdapter(Cursor cursor, Context context,int groupLayout,
                                     int childLayout, String[] groupFrom, int[] groupTo, String[] childrenFrom,
                                     int[] childrenTo) {

            super(context, cursor, groupLayout, groupFrom, groupTo,
                    childLayout, childrenFrom, childrenTo);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor)
        {
            Cursor childCursor = manager.fetchChildren(groupCursor.getString(groupCursor.getColumnIndex("Title")));
            MainActivity.this.startManagingCursor(childCursor);
            childCursor.moveToFirst();
            return childCursor;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
        {
            View view = super.getGroupView(groupPosition, isExpanded, convertView, parent);
            if (groupPosition % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#ffbf66"));
            } else {
                view.setBackgroundColor(Color.parseColor("#d46f4d"));
            }
            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
        {
            View view = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
            TextView tv = (TextView)view.findViewById(R.id.tv_child_complete);
            String complete = tv.getText().toString();

            if(complete.equals("1"))
            {
                view.setBackgroundColor(Color.RED);
            }

            return view;
        }
    }

    public void postToTask(String title, String content, String completed, String created_date)
    {
        String alias = prefs.getString("pref_login_name", "Hank");
        String password = prefs.getString("pref_password", "1234");
        try
        {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://www.youcode.ca/Lab02Post.jsp");
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("LIST_TITLE", title));
            postParameters.add(new BasicNameValuePair("CONTENT", content));
            postParameters.add(new BasicNameValuePair("COMPLETED_FLAG", completed));
            postParameters.add(new BasicNameValuePair("ALIAS", alias));
            postParameters.add(new BasicNameValuePair("PASSWORD", password));
            postParameters.add(new BasicNameValuePair("CREATED_DATE", created_date));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
            request.setEntity(formEntity);
            HttpResponse response = client.execute(request);
        }
        catch(Exception e)
        {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }


}
