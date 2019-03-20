package ca.nait.jliu73.tasklist.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;


import ca.nait.jliu73.tasklist.DBHelper.DBManager;
import ca.nait.jliu73.tasklist.DBHelper.TaskItemDAO;
import ca.nait.jliu73.tasklist.DBHelper.TaskTagDAO;

import ca.nait.jliu73.tasklist.InputDialog;

import ca.nait.jliu73.tasklist.R;
import ca.nait.jliu73.tasklist.models.TaskItem;

public class MainActivity extends AppCompatActivity implements InputDialog.InputDialogListener
{
    private FloatingActionButton fab;
    private DBManager manager;
    private TaskTagDAO tagDao;
    private TaskItemDAO itemDAO;
    private int lastPosition = -1;
    private FloatingActionButton add_fab;
    SQLiteDatabase db;
    String groupTag = null;
    private ExpandableListAdapter adapter;
    private View childView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable_list);
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


    }

    public void openDialog()
    {
        InputDialog input = new InputDialog();
        input.show(getSupportFragmentManager(), "Input Dialog");
    }

    @Override
    public void applyText(String tag)
    {
        if(groupTag != null && !groupTag.isEmpty() && !tag.isEmpty())
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
        final long idToComplete = id;

        AlertDialog confirmationDialong =new AlertDialog.Builder(this)
                .setTitle("Complete?")
                .setMessage("Do you want to set this item as complete?")
                .setIcon(R.drawable.check)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {

                        itemDAO.updateItemComplete(idToComplete);
                        fillData();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {

                        dialog.dismiss();

                    }
                })
                .create();
        return confirmationDialong;
    }

    private AlertDialog AskOption(long id, String type)
    {
        final String deleteType = type;
        final long idToDelete = id;

        AlertDialog confirmationDialong =new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.delete)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        if(deleteType == "Child")
                        {
                            itemDAO.deleteTaskItemById(idToDelete);
                            fillData();
                        }
                        else if(deleteType == "Group")
                        {
                            tagDao.deleteTask(tagDao.getTaskTagById(idToDelete));
                            fillData();
                            add_fab.setVisibility(View.GONE);
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
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
                    AlertDialog diaBox = AskOption(childPos, "Child");
                    diaBox.show();


                }
                else if(ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP)
                {
                    int groupPos = ExpandableListView.getPackedPositionGroup(id);
                    AlertDialog diaBox = AskOption(groupPos, "Group");
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


}
