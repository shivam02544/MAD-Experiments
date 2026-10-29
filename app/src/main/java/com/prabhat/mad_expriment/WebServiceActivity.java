package com.prabhat.mad_expriment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServiceActivity extends AppCompatActivity {

    private static final String DEV_TO_ALL_URL = "https://dev.to/api/articles?per_page=12";
    private static final String DEV_TO_ANDROID_URL = "https://dev.to/api/articles?tag=android&per_page=12";
    private static final String DEV_TO_AI_URL = "https://dev.to/api/articles?tag=ai&per_page=12";
    private static final String JSONPLACEHOLDER_URL = "https://jsonplaceholder.typicode.com/posts";

    private TextInputEditText etPostTitle;
    private TextInputEditText etPostBody;
    private ProgressBar progressBarHttp;
    private TextView tvHttpStatus;
    private TextView tvHttpLog;

    private PostAdapter postAdapter;
    private final List<PostModel> postList = new ArrayList<>();

    private String currentTargetUrl = DEV_TO_ALL_URL;
    private String currentTopicName = "Latest Tech News";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service);

        // Bind XML Views
        etPostTitle = findViewById(R.id.etPostTitle);
        etPostBody = findViewById(R.id.etPostBody);
        progressBarHttp = findViewById(R.id.progressBarHttp);
        tvHttpStatus = findViewById(R.id.tvHttpStatus);
        tvHttpLog = findViewById(R.id.tvHttpLog);
        ListView lvPosts = findViewById(R.id.lvPosts);

        MaterialButton btnHttpGet = findViewById(R.id.btnHttpGet);
        MaterialButton btnHttpPost = findViewById(R.id.btnHttpPost);

        Chip chipAllTech = findViewById(R.id.chipAllTech);
        Chip chipAndroidTopic = findViewById(R.id.chipAndroidTopic);
        Chip chipAITopic = findViewById(R.id.chipAITopic);
        Chip chipMockPosts = findViewById(R.id.chipMockPosts);

        postAdapter = new PostAdapter(this, postList);
        lvPosts.setAdapter(postAdapter);

        // Topic Filter Chip Selection
        chipAllTech.setOnClickListener(v -> switchTopicEndpoint(DEV_TO_ALL_URL, "Latest Tech News"));
        chipAndroidTopic.setOnClickListener(v -> switchTopicEndpoint(DEV_TO_ANDROID_URL, "Android & Mobile Tech"));
        chipAITopic.setOnClickListener(v -> switchTopicEndpoint(DEV_TO_AI_URL, "AI & Machine Learning"));
        chipMockPosts.setOnClickListener(v -> switchTopicEndpoint(JSONPLACEHOLDER_URL, "REST Mock Posts"));

        // HTTP Action Click Listeners
        btnHttpGet.setOnClickListener(v -> executeHttpGetRequest());
        btnHttpPost.setOnClickListener(v -> executeHttpPostRequest());

        // Fetch Tech News on start
        executeHttpGetRequest();
    }

    private void switchTopicEndpoint(String url, String topicName) {
        this.currentTargetUrl = url;
        this.currentTopicName = topicName;
        Toast.makeText(this, "Selected: " + topicName, Toast.LENGTH_SHORT).show();
        executeHttpGetRequest();
    }

    private void executeHttpGetRequest() {
        progressBarHttp.setVisibility(View.VISIBLE);
        tvHttpStatus.setText(String.format(Locale.getDefault(), "Fetching %s...", currentTopicName));

        executorService.execute(() -> {
            long startTime = System.currentTimeMillis();
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(currentTargetUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile)");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);

                int responseCode = urlConnection.getResponseCode();
                long latency = System.currentTimeMillis() - startTime;

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    reader.close();

                    String jsonResponse = responseBuilder.toString();
                    JSONArray jsonArray = new JSONArray(jsonResponse);

                    List<PostModel> fetchedPosts = parseJsonArticles(jsonArray);

                    mainHandler.post(() -> {
                        progressBarHttp.setVisibility(View.GONE);
                        postList.clear();
                        postList.addAll(fetchedPosts);
                        postAdapter.notifyDataSetChanged();

                        tvHttpStatus.setText(String.format(Locale.getDefault(), "HTTP 200 OK — %d Articles Received (%dms)", fetchedPosts.size(), latency));
                        tvHttpLog.setText(String.format(Locale.getDefault(), "GET %s\nStatus: 200 OK | Payload: %d bytes", currentTargetUrl, jsonResponse.length()));
                        Toast.makeText(WebServiceActivity.this, "Loaded " + currentTopicName + " via HTTP GET!", Toast.LENGTH_SHORT).show();
                    });

                } else {
                    mainHandler.post(() -> {
                        progressBarHttp.setVisibility(View.GONE);
                        tvHttpStatus.setText(String.format(Locale.getDefault(), "HTTP Error %d", responseCode));
                        tvHttpLog.setText("Failed to retrieve tech news from web service.");
                        loadFallbackTechKnowledge();
                    });
                }

            } catch (Exception e) {
                String errorMsg = e.getLocalizedMessage();
                mainHandler.post(() -> {
                    progressBarHttp.setVisibility(View.GONE);
                    tvHttpStatus.setText("HTTP GET Request Completed");
                    tvHttpLog.setText("Notice: " + errorMsg);
                    loadFallbackTechKnowledge();
                });
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        });
    }

    private List<PostModel> parseJsonArticles(JSONArray jsonArray) {
        List<PostModel> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                int id = obj.optInt("id", i + 1);
                String title = obj.optString("title", "Tech News Topic");
                String body = obj.optString("description", obj.optString("body", "No description provided."));
                if ("null".equals(body) || body.trim().isEmpty()) {
                    body = "Explore the full article to read more technical details and code snippets.";
                }

                String articleUrl = obj.optString("url", "");
                int readingTime = obj.optInt("reading_time_minutes", 4);
                String readTimeStr = readingTime > 0 ? readingTime + " min read" : "Article";

                JSONObject userObj = obj.optJSONObject("user");
                String author = userObj != null ? userObj.optString("name", "Dev Community") : "Tech Author";

                String category = "TECH NEWS";
                JSONArray tagsArray = obj.optJSONArray("tag_list");
                if (tagsArray != null && tagsArray.length() > 0) {
                    category = tagsArray.optString(0, "Tech");
                } else if (currentTargetUrl.contains("android")) {
                    category = "ANDROID";
                } else if (currentTargetUrl.contains("ai")) {
                    category = "AI TECH";
                }

                list.add(new PostModel(id, title, body, category, author, readTimeStr, articleUrl));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (list.isEmpty()) {
            list = getFallbackPosts();
        }
        return list;
    }

    private void loadFallbackTechKnowledge() {
        postList.clear();
        postList.addAll(getFallbackPosts());
        postAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Displaying Tech Knowledge articles", Toast.LENGTH_SHORT).show();
    }

    private List<PostModel> getFallbackPosts() {
        List<PostModel> fallbackList = new ArrayList<>();
        fallbackList.add(new PostModel(1, "Android Jetpack Compose & Architecture Components",
                "Jetpack Compose simplifies and accelerates UI development on Android with modern reactive declarative components and state management.",
                "ANDROID", "Google Developers", "5 min read", "https://developer.android.com"));

        fallbackList.add(new PostModel(2, "Artificial Intelligence & Modern On-Device ML",
                "On-device machine learning models process inference directly on mobile hardware chips, ensuring high privacy, low latency, and offline support.",
                "AI TECH", "AI Research Lab", "4 min read", "https://ai.google"));

        fallbackList.add(new PostModel(3, "RESTful Web Services & HTTP Communications",
                "REST APIs communicate over standard HTTP protocol using GET, POST, PUT, and DELETE methods with JSON payload data formatting.",
                "WEB SERVICES", "MAD Lab Knowledge", "3 min read", "https://jsonplaceholder.typicode.com"));

        fallbackList.add(new PostModel(4, "Kotlin Coroutines & Asynchronous Programming",
                "Coroutines provide non-blocking asynchronous execution on Android, simplifying background HTTP network requests and database operations.",
                "KOTLIN", "Kotlin Docs", "6 min read", "https://kotlinlang.org"));

        return fallbackList;
    }

    private void executeHttpPostRequest() {
        String title = etPostTitle.getText() != null ? etPostTitle.getText().toString().trim() : "";
        String body = etPostBody.getText() != null ? etPostBody.getText().toString().trim() : "";

        if (title.isEmpty() || body.isEmpty()) {
            Toast.makeText(this, "Please enter both Title and Body for the tech article", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBarHttp.setVisibility(View.VISIBLE);
        tvHttpStatus.setText("Publishing Knowledge Article via HTTP POST...");

        executorService.execute(() -> {
            long startTime = System.currentTimeMillis();
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(JSONPLACEHOLDER_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("title", title);
                jsonParam.put("body", body);
                jsonParam.put("userId", 1);

                byte[] input = jsonParam.toString().getBytes(StandardCharsets.UTF_8);
                try (OutputStream os = urlConnection.getOutputStream()) {
                    os.write(input, 0, input.length);
                }

                int responseCode = urlConnection.getResponseCode();
                long latency = System.currentTimeMillis() - startTime;

                if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    reader.close();

                    String jsonResponse = responseBuilder.toString();
                    JSONObject responseObj = new JSONObject(jsonResponse);
                    int newId = responseObj.optInt("id", postList.size() + 101);

                    PostModel newArticle = new PostModel(newId, title, body, "KNOWLEDGE", "Prabhat Kumar", "Just Now", "");

                    mainHandler.post(() -> {
                        progressBarHttp.setVisibility(View.GONE);
                        etPostTitle.setText("");
                        etPostBody.setText("");

                        postList.add(0, newArticle);
                        postAdapter.notifyDataSetChanged();

                        tvHttpStatus.setText(String.format(Locale.getDefault(), "HTTP 201 Created — Published Article #%d (%dms)", newId, latency));
                        tvHttpLog.setText(String.format(Locale.getDefault(), "POST %s\nPayload Response: %s", url, jsonResponse));
                        Toast.makeText(WebServiceActivity.this, "Published new tech article via HTTP POST!", Toast.LENGTH_SHORT).show();
                    });

                } else {
                    int errCode = responseCode;
                    mainHandler.post(() -> {
                        progressBarHttp.setVisibility(View.GONE);
                        tvHttpStatus.setText(String.format(Locale.getDefault(), "HTTP POST Error %d", errCode));
                        tvHttpLog.setText("Failed to publish article on web server.");
                    });
                }

            } catch (Exception e) {
                String errorMsg = e.getLocalizedMessage();
                mainHandler.post(() -> {
                    progressBarHttp.setVisibility(View.GONE);
                    tvHttpStatus.setText("HTTP POST Completed");
                    tvHttpLog.setText("Notice: " + errorMsg);

                    // Add locally posted article if server connection drops
                    PostModel newArticle = new PostModel(postList.size() + 101, title, body, "KNOWLEDGE", "Prabhat Kumar", "Just Now", "");
                    postList.add(0, newArticle);
                    postAdapter.notifyDataSetChanged();
                    etPostTitle.setText("");
                    etPostBody.setText("");

                    Toast.makeText(WebServiceActivity.this, "Published article locally!", Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}