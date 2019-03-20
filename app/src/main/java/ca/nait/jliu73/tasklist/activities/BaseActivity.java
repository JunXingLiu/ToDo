package ca.nait.jliu73.tasklist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import ca.nait.jliu73.tasklist.R;

/**
 * Created by jliu73 on 3/20/2019.
 */

public class BaseActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.toggle_service:
            {
                if(GetterService.bRun == false)
                {
                    startService(new Intent(this, GetterService.class));
                }
                else
                {
                    stopService(new Intent(this, GetterService.class));
                }

                break;
            }
            case R.id.view_archive:
            {
                startActivity(new Intent(this, ArchiveListActivity.class));
                break;
            }
            case R.id.home:
            {
                startActivity(new Intent(this, MainActivity.class));
                break;
            }
            case R.id.pref:
            {
                startActivity(new Intent(this, Preference.class));
                break;
            }
        }
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if(menu != null)
        {
            MenuItem toggleItem = menu.findItem(R.id.toggle_service);
            if (GetterService.bRun == false)
            {
                toggleItem.setTitle("Go Online");
            }
            else
            {
                toggleItem.setTitle("Go Offline");
            }
        }
        return true;
    }

}
