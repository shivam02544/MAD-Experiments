package com.prabhat.mad_expriment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;

public class PostAdapter extends ArrayAdapter<PostModel> {

    public PostAdapter(@NonNull Context context, @NonNull List<PostModel> posts) {
        super(context, 0, posts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_post, parent, false);
        }

        PostModel post = getItem(position);

        if (post != null) {
            TextView tvCategoryBadge = convertView.findViewById(R.id.tvCategoryBadge);
            TextView tvPostIdBadge = convertView.findViewById(R.id.tvPostIdBadge);
            TextView tvPostTitle = convertView.findViewById(R.id.tvPostTitle);
            TextView tvPostBody = convertView.findViewById(R.id.tvPostBody);
            TextView tvAuthor = convertView.findViewById(R.id.tvAuthor);
            TextView tvReadTime = convertView.findViewById(R.id.tvReadTime);

            tvCategoryBadge.setText(post.getCategory().toUpperCase(Locale.getDefault()));
            tvPostIdBadge.setText(String.format(Locale.getDefault(), "#%d", post.getId()));
            tvPostTitle.setText(post.getTitle());
            tvPostBody.setText(post.getBody());
            tvAuthor.setText(post.getAuthor());
            tvReadTime.setText(post.getDateOrReadTime());

            convertView.setOnClickListener(v -> {
                String articleUrl = post.getArticleUrl();
                if (articleUrl != null && !articleUrl.isEmpty()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl));
                        getContext().startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Cannot open article link", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), post.getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        return convertView;
    }
}