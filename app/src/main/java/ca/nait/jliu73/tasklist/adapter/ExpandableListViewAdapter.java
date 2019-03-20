package ca.nait.jliu73.tasklist.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.nait.jliu73.tasklist.DBHelper.TaskItemDAO;
import ca.nait.jliu73.tasklist.DBHelper.TaskTagDAO;
import ca.nait.jliu73.tasklist.models.TaskItem;
import ca.nait.jliu73.tasklist.models.TaskTag;

/**
 * Created by Hank on 2019-03-16.
 */

public class ExpandableListViewAdapter extends BaseExpandableListAdapter
{

    Context context;
    TaskTagDAO tagDao;
    TaskItemDAO itemDAO;

    List<TaskTag> listOfTags;
    List<TaskItem> tempList;
    List<TaskItem> listOfItems = new ArrayList<TaskItem>();




    public ExpandableListViewAdapter(Context context)
    {
        this.context = context;
        tagDao = new TaskTagDAO(context);
        itemDAO = new TaskItemDAO(context);
        listOfTags = tagDao.getAllTag();
        for( TaskTag i : listOfTags)
        {
            tempList = itemDAO.getTaskItemOfTag(i.getTitle());
            for(TaskItem j : tempList)
            {
                listOfItems.add(j);
            }
        }
    }

    @Override
    public int getGroupCount() {
        return listOfTags.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listOfTags.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return itemDAO.getTaskItemOfTag(listOfTags.get(groupPosition).getTitle());
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent)
    {
        TextView txtView = new TextView(context);
        txtView.setText(listOfTags.get(groupPosition).getTitle());
        txtView.setPadding(100,0,0,0);
        txtView.setTextColor(Color.BLACK);

        return txtView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent)
    {
        final TextView txtView = new TextView(context);
        txtView.setText(listOfItems.get(childPosition).getDescription());
        txtView.setPadding(100,0,0,0);

        txtView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(context, txtView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return txtView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
