package com.rokuan.calliope;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.rokuan.calliope.db.CalliopeSQLiteOpenHelper;
import com.rokuan.calliope.db.DatabaseLoadingListener;


public class LoadingActivity extends FragmentActivity implements DatabaseLoadingListener {
    private CalliopeSQLiteOpenHelper db;
    private SQLiteDatabase database;
    /*private TextView maxProgressView;
    private TextView currentProgressView;
    private TextView progressMessageView;*/

    //private Handler handler = new Handler();

    class DatabaseLoadingAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        public void onPreExecute(){
            onLoadingStarted();
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

        db = new CalliopeSQLiteOpenHelper(this);
        //db.setDatabaseLoadingListener(this);

        /*maxProgressView = (TextView)findViewById(R.id.loading_max_progress);
        currentProgressView = (TextView)findViewById(R.id.loading_progress);
        progressMessageView = (TextView)findViewById(R.id.progress_message);*/

        new DatabaseLoadingAsyncTask().execute();
    }

    /*@Override
    public void onStart(){
        super.onStart();
        //database = db.getReadableDatabase();
    }*/


    private void loadDatabase(){
        database = db.getReadableDatabase();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        database.close();
        database = null;
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

    @Override
    public void setOperationsCount(int count) {
        //maxProgressView.setText(String.valueOf(count));
    }

    //@Override
    public void onLoadingStarted() {

    }

    @Override
    public void onOperationStarted(int operationIndex, String message) {
            /*currentProgressView.setText(String.valueOf(operationIndex+1));
            progressMessageView.setText(message);*/
    }

    @Override
    public void onOperationEnded(int operationIndex) {

    }

    //@Override
    public void onLoadingEnded() {
        Intent intent = new Intent(this, HomeActivity.class);
        this.startActivity(intent);
    }
}
