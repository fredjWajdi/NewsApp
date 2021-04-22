package com.wizzapps.android.newsapp.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wizzapps.android.newsapp.R;
import com.wizzapps.android.newsapp.model.News;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<News> mListNews;
    private static final String LOG_TAG = NewsAdapter.class.getCanonicalName();

    public NewsAdapter(List<News> mListNews) {
        this.mListNews = mListNews;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.row_layout, parent, false);
        return new NewsViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = mListNews.get(position);
        holder.publicationDateTxtView.setText(extractDate(news.getPublicationDate()));
        holder.sectionNameTxtView.setText(news.getSectionName());
        holder.titleTxtView.setText(news.getTitle());
        int size = news.getAuthorName().size();
        if (size > 0) {
            StringBuilder stb = new StringBuilder(holder.context.getString(R.string.authors) + " : ");
            for (int i = 0; i < size; i++) {
                stb.append(news.getAuthorName().get(i));
                if (i < size - 1) {
                    stb.append(", ");
                }
            }
            holder.authorsNames.setText(stb.toString());
        }
    }

    public String extractDate(String publicationDate) {
        if (publicationDate == null || "".equals(publicationDate)) {
            return "";
        }
        String[] strgs = publicationDate.split("T");
        return strgs[0];
    }

    @Override
    public int getItemCount() {
        return mListNews.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView titleTxtView;
        private final TextView sectionNameTxtView;
        private final TextView publicationDateTxtView;
        private final TextView authorsNames;
        private final Context context;

        public NewsViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            titleTxtView = itemView.findViewById(R.id.title_txt_view);
            sectionNameTxtView = itemView.findViewById(R.id.section_name_txt_view);
            publicationDateTxtView = itemView.findViewById(R.id.publication_date_txt_view);
            authorsNames = itemView.findViewById(R.id.authors_names_txt_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            if (position != RecyclerView.NO_POSITION) {
                String url = mListNews.get(position).getWebUrl();
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e(LOG_TAG, "Test: " + context.getString(R.string.application_not_found));
                    Toast.makeText(context, context.getString(R.string.application_not_found), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
