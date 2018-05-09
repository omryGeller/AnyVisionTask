package geller.omry.anyvisiontask.View;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import geller.omry.anyvisiontask.Controller.RSSFeedRecyclerAdapter;
import geller.omry.anyvisiontask.Model.FeedItem;
import geller.omry.anyvisiontask.Model.FeedManager;
import geller.omry.anyvisiontask.R;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    private List<FeedItem>  feedItemList;
    private RSSFeedRecyclerAdapter rssFeedRecyclerAdapter;
    private  SwipeRefreshLayout swipeRefreshLayout;
    private FeedManager feedManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        feedManager=FeedManager.getInstance(this);//this object is responsible for the creation of the feed (feed xml parsing,feed items images extraction, etc..).

        feedItemList=new ArrayList<>();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_view);

        swipeRefreshLayout.setRefreshing(false);

        RecyclerView feedRecyclerView=(RecyclerView)findViewById(R.id.feed_items);

        feedRecyclerView.setNestedScrollingEnabled(false);
        feedRecyclerView.setHasFixedSize(false);
        feedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        rssFeedRecyclerAdapter=new RSSFeedRecyclerAdapter(this,feedItemList);

        feedRecyclerView.setAdapter(rssFeedRecyclerAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(haveNetworkConnection()) {
            new FeedAsyncTask().execute((Void) null);
        }
        else {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("No internet connection");
            builder.setMessage("Please connect to a WIFI or MOBILE network and reopen the app.");
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    System.exit(0);
                }
            });
            builder.create().show();
        }
    }

    /**
     * This method checks for network connection Mobile or Wifi.
     * @return - true if the device has any kind of internet connection and false if its disconnected.
     */
    private boolean haveNetworkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * FeedAsyncTask responsible for doing background work to extract feed content.
     */
    private class FeedAsyncTask extends AsyncTask<Void,Void,Boolean>{

        private final String urlLink="http://feeds.nationalgeographic.com/ng/photography/photo-of-the-day";
        private List<FeedItem> feedItems;

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setEnabled(false);
            if(feedItems != null){
               rssFeedRecyclerAdapter.setItemList(feedItems);
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL url=new URL(urlLink);
                InputStream inputStream = url.openConnection().getInputStream();
                feedItems=feedManager.parseFeedContent(inputStream);
                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;

        }
    }
}