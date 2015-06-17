package com.rokuan.calliope;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.rokuan.calliope.db.CalliopeSQLiteOpenHelper;
import com.rokuan.calliope.db.TableEvent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;


public class LoadingActivity extends FragmentActivity {
    private CalliopeSQLiteOpenHelper db;

    private EventBus bus = EventBus.getDefault();

    @InjectView(R.id.progress_text) protected TextView progressTextView;

    class DatabaseLoadingAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        public void onPreExecute(){
            //onLoadingStarted();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }

            loadDatabase();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }
            return null;
        }

        @Override
        public void onPostExecute(Void result){
            onLoadingEnded();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ButterKnife.inject(this);

        db = new CalliopeSQLiteOpenHelper(this);
    }


    @Override
    protected void onPause(){
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        bus.register(this);

        //loadDatabase();
        new DatabaseLoadingAsyncTask().execute();
    }

    private void onLoadingEnded(){
        Intent intent = new Intent(this, HomeActivity.class);
        this.startActivity(intent);
    }

    public void onEvent(TableEvent event){
        final String message = event.getMessage();
        Log.i("DatabaseLoading", event.getMessage());
        progressTextView.post(new Runnable() {
            @Override
            public void run() {
                progressTextView.setText(message + "...");
            }
        });
    }

    private void loadDatabase(){
        SQLiteDatabase database;
        database = db.getReadableDatabase();
        database.close();

        /*Intent intent = new Intent(this, HomeActivity.class);
        this.startActivity(intent);*/
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
