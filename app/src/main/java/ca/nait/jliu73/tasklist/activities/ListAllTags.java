package ca.nait.jliu73.tasklist.activities;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ca.nait.jliu73.tasklist.DBHelper.DBManager;
import ca.nait.jliu73.tasklist.DBHelper.TaskItemDAO;
import ca.nait.jliu73.tasklist.DBHelper.TaskTagDAO;
import ca.nait.jliu73.tasklist.InputDialog;
import ca.nait.jliu73.tasklist.R;
import ca.nait.jliu73.tasklist.adapter.ListTagsAdapter;
import ca.nait.jliu73.tasklist.models.TaskTag;

import static android.support.v7.widget.AppCompatDrawableManager.get;

/**
 * Created by Hank on 2019-03-15.
 */

public class ListAllTags extends AppCompatActivity implements  OnItemLongClickListener, OnItemClickListener, OnClickListener, InputDialog.InputDialogListener
{

    private FloatingActionButton fab;
    private String tagTitle;
    private DBManager manager;
    private TaskTagDAO tagDao;
    private TaskItemDAO itemDAO;
    private ListView listOfTags;
    private List<TaskTag> allTags;
    private ListTagsAdapter tagsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        manager = new DBManager(this);
        tagDao = new TaskTagDAO(this);
        itemDAO = new TaskItemDAO(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openDialog();
            }
        });
        refreshTags();


    }

    public void refreshTags()
    {
        allTags = tagDao.getAllTag();
        if(allTags != null && !allTags.isEmpty())
        {
            tagsAdapter = new ListTagsAdapter(this, allTags);
            listOfTags.setAdapter(tagsAdapter);

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
        tagDao.createTag(tag);
        refreshTags();

    }

    public void initViews()
    {
        this.listOfTags = (ListView)findViewById(R.id.lv_list_companies);
    }


    @Override
    public void onClick(View v)
    {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

}

