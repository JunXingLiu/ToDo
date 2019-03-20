package ca.nait.jliu73.tasklist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import ca.nait.jliu73.tasklist.R;
import ca.nait.jliu73.tasklist.models.TaskTag;

/**
 * Created by Hank on 2019-03-15.
 */

public class ListTagsAdapter extends BaseAdapter
{

    private List<TaskTag> tags;
    private LayoutInflater inflater;

    public ListTagsAdapter(Context context, List<TaskTag> listTags)
    {
        this.setItems(listTags);
        this.inflater = LayoutInflater.from(context);
    }

    public List<TaskTag> getTags()
    {
        return tags;
    }

    public void setItems(List<TaskTag> tags)
    {
        this.tags = tags;
    }

    @Override
    public int getCount()
    {
        return (getTags() != null && !getTags().isEmpty()) ? getTags().size() : 0 ;
    }

    @Override
    public TaskTag getItem(int position) {
        return (getTags() != null && !getTags().isEmpty()) ? getTags().get(position) : null ;
    }

    @Override
    public long getItemId(int position) {
        return (getTags() != null && !getTags().isEmpty()) ? getTags().get(position).getId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        ViewHolder holder;
        if(v == null)
        {
            v = inflater.inflate(R.layout.list_tags, parent, false);
            holder = new ViewHolder();
            holder.displayTagTitle = (TextView)v.findViewById(R.id.tv_title);
            v.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)v.getTag();
        }

        TaskTag currentTag = getItem(position);
        if(currentTag != null)
        {
            holder.displayTagTitle.setText(currentTag.getTitle());
        }
        return v;

    }

    class ViewHolder
    {
        TextView displayTagTitle;
    }
}
