package com.wizzapps.android.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wizzapps.android.newsapp.adapter.NewsAdapter;
import com.wizzapps.android.newsapp.loader.NewsAsyncTaskLoader;
import com.wizzapps.android.newsapp.model.News;
import com.wizzapps.android.newsapp.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SwipeRefreshLayout.OnRefreshListener {

    public static final String LOG_TAG = MainActivity.class.getName();
    private final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";
    private static final int LOADER_ID_NEWS = 10000;
    LoaderManager loaderManager;
    RecyclerView recyclerView;
    NewsAdapter newsAdapter;
    ProgressBar progressBar;
    TextView noDataTxtView;
    List<News> mNewsData;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress_circular);
        noDataTxtView = findViewById(R.id.no_data_txt_view);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
        recyclerView = findViewById(R.id.list_of_items);
        mNewsData = new ArrayList<>();
        newsAdapter = new NewsAdapter(mNewsData);
        recyclerView.setAdapter(newsAdapter);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (isConnected) {
            this.loaderManager = LoaderManager.getInstance(this);
            this.loaderManager.initLoader(LOADER_ID_NEWS, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            noDataTxtView.setText(R.string.no_internet_connection);
        }
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.i(LOG_TAG, "Test: onCreateLoader");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String topic = sharedPreferences.getString(getString(R.string.settings_topic_key), getString(R.string.settings_topic_default_value));
        String apiKey = sharedPreferences.getString(getString(R.string.settings_api_key_key), getString(R.string.settings_api_key_default_value));
        String date = sharedPreferences.getString(getString(R.string.settings_date_key), getString(R.string.settings_date_default_value));
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        if (topic != null && !"".equals(topic)) {
            uriBuilder.appendQueryParameter("q", topic);
        }
        if (date != null && !"".equals(date)) {
            uriBuilder.appendQueryParameter("from-date", date);
        }
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", apiKey);
        if (id == LOADER_ID_NEWS) {
            return new NewsAsyncTaskLoader(this, uriBuilder.toString());
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> data) {
        Log.i(LOG_TAG, "TEST: onLoadFinished");
        if (loader.getId() == LOADER_ID_NEWS) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean isConnected = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if (data != null && !data.isEmpty()) {
                this.mNewsData.clear();
                this.mNewsData.addAll(data);
                newsAdapter.notifyDataSetChanged();
                noDataTxtView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else if (isConnected) {
                noDataTxtView.setText(getString(R.string.no_data_available));
                noDataTxtView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                noDataTxtView.setText(R.string.no_internet_connection);
                noDataTxtView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        this.mNewsData.clear();
        newsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_refresh) {
            refreshNews();
            progressBar.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        Log.i(LOG_TAG, "Test: onRefresh");
        refreshNews();
    }

    private void refreshNews() {
        Log.i(LOG_TAG, "Test: refreshNews");
        this.loaderManager.restartLoader(LOADER_ID_NEWS, null, this);
    }
}