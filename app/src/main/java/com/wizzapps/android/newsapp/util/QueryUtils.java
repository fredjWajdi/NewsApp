package com.wizzapps.android.newsapp.util;

import android.text.TextUtils;
import android.util.Log;

import com.wizzapps.android.newsapp.model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getCanonicalName();

    public static List<News> fetchNews(String urlString) {
        Log.i(LOG_TAG, "TEST: fetchNews");
        URL url = createUrl(urlString);
        List<News> news = new ArrayList<>();
        try {
            String jsonResponse = makeHttpRequest(url);
            news = extractNewsFromJson(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Return the list of news
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the InputStream into a String which contains the whole JSON response from the server.
     *
     * @param inputStream
     * @return JSON response as String
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * @param newsJSON
     * @return an {@link News} object by parsing out information
     * about the first news from the input newsJSON string.
     */
    private static List<News> extractNewsFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        List<News> newsList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject response = baseJsonResponse.getJSONObject("response");
            JSONArray newsArray = response.getJSONArray("results");
            List<String> authorNames;
            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject news = newsArray.getJSONObject(i);
                // Extract out the title, sectionName, webUrl and publicationDate values
                String title = news.optString("webTitle");
                String section = news.optString("sectionName");
                String publicationDate = news.optString("webPublicationDate");
                String webUrl = news.optString("webUrl");
                JSONArray contributors = news.getJSONArray("tags");
                authorNames = new ArrayList<>();
                for (int j = 0; j < contributors.length(); j++) {
                    JSONObject author = contributors.getJSONObject(j);
                    String authorName = author.optString("firstName") + (author.optString("lastName").equals("") ? "" : " " + author.optString("lastName"));
                    authorNames.add(authorName);
                }
                newsList.add(new News(section, title, webUrl, publicationDate, authorNames));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the News JSON results", e);
        }
        return newsList;
    }
}
