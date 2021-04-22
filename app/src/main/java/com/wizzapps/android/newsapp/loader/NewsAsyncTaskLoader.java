package com.wizzapps.android.newsapp.loader;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.wizzapps.android.newsapp.model.News;
import com.wizzapps.android.newsapp.util.QueryUtils;

import java.util.List;

public class NewsAsyncTaskLoader extends AsyncTaskLoader<List<News>> {

    private static final String LOG_TAG = NewsAsyncTaskLoader.class.getCanonicalName();
    private final String url;

    public NewsAsyncTaskLoader(@NonNull Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST: onStartLoading");
        forceLoad();
    }

    @Nullable
    @Override
    public List<News> loadInBackground() {
        return QueryUtils.fetchNews(this.url);
    }

}
