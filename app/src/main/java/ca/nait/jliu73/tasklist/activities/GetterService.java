package ca.nait.jliu73.tasklist.activities;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import ca.nait.jliu73.tasklist.DBHelper.DBManager;

/**
 * Created by jliu73 on 3/20/2019.
 */

public class GetterService extends Service
{
    public static boolean bRun = false;
    private static int DELAY = 60000;
    private MyThread theThread = null;
    DBManager manager;
    SQLiteDatabase database;
    public static final String NEW_MESSAGE = "New Message";

    public GetterService()
    {

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        manager = new DBManager(this);
        theThread = new MyThread();
        bRun = true;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        bRun = true;
        theThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        bRun = false;
        theThread.interrupt();
        theThread = null;
    }


    public class MyThread extends Thread
    {
        //Posted_Date, List_Title, Content, and the Completed_Flag as a 1 or 0
        public MyThread()
        {
            super("MainActivity");
        }

        private boolean getRecords() throws Exception
        {
            BufferedReader in = null;
            boolean bNew = false;
            try {
                bNew = false;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI("http://www.youcode.ca/Lab02Get.jsp?ALIAS=Hank&PASSWORD=1234"));
                HttpResponse response = client.execute(request);
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line = "";

                database = manager.getWritableDatabase();
                ContentValues values = new ContentValues();
                while ((line = in.readLine()) != null) {
                    values.clear();
                    values.put(DBManager.post_date, line);

                    if ((line = in.readLine()) != null) {
                        values.put(DBManager.listTitle, line);
                    }
                    if ((line = in.readLine()) != null) {
                        values.put(DBManager.contents, line);
                    }
                    if ((line = in.readLine()) != null) {
                        values.put(DBManager.completedFlag, (line.toString().equals("0") ? "Incomplete" : "Completed"));
                    }
                    try
                    {
                        database.insertOrThrow(DBManager.taskArchive, null, values);
                        bNew = true;

                    } catch (SQLException e) {
                        // ignore exception
                        e.printStackTrace();

                    }
                }
                database.close();
            } catch (Exception e) {

            }
            return bNew;
        }

        @Override
        public void run()
        {
            GetterService parent = GetterService.this;
            while(parent.bRun == true)
                try
                {
                    try {
                        if(getRecords())
                        {
                            Intent broadcast = new Intent(NEW_MESSAGE);
                            sendBroadcast(broadcast);

                        }
                        Thread.sleep(DELAY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                catch (Exception e)
                {
                    parent.bRun = false;
                }
        }


    }
}

