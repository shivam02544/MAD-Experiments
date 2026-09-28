package com.prabhat.mad_expriment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class WebViewActivity extends AppCompatActivity {

    private static final String DEFAULT_URL = "https://www.google.com";

    private TextInputEditText etUrl;
    private ProgressBar progressBar;
    private WebView webView;

    private ImageButton btnWebBack;
    private ImageButton btnWebForward;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // Bind XML views
        etUrl = findViewById(R.id.etUrl);
        MaterialButton btnGo = findViewById(R.id.btnGo);
        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);

        btnWebBack = findViewById(R.id.btnWebBack);
        btnWebForward = findViewById(R.id.btnWebForward);
        ImageButton btnWebReload = findViewById(R.id.btnWebReload);
        ImageButton btnWebHome = findViewById(R.id.btnWebHome);
        ImageButton btnMenuMore = findViewById(R.id.btnMenuMore);

        // Configure WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // Set WebViewClient to handle page navigation in app
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                etUrl.setText(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                updateNavigationButtons();
            }
        });

        // Set WebChromeClient for page load progress
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });

        // Load default URL
        loadUrl(DEFAULT_URL);

        // Address Bar Go button
        btnGo.setOnClickListener(v -> handleUrlInput());

        // Keyboard "Go" action
        etUrl.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                handleUrlInput();
                return true;
            }
            return false;
        });

        // Navigation controls
        btnWebBack.setOnClickListener(v -> {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        });

        btnWebForward.setOnClickListener(v -> {
            if (webView.canGoForward()) {
                webView.goForward();
            }
        });

        btnWebReload.setOnClickListener(v -> webView.reload());

        btnWebHome.setOnClickListener(v -> loadUrl(DEFAULT_URL));

        // Overflow Popup / Context Menu on top button
        btnMenuMore.setOnClickListener(this::showPopupMenu);

        // Register hardware / system back press handler
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void handleUrlInput() {
        String inputUrl = etUrl.getText() != null ? etUrl.getText().toString().trim() : "";
        if (!inputUrl.isEmpty()) {
            String targetUrl = (!inputUrl.startsWith("http://") && !inputUrl.startsWith("https://"))
                    ? "https://" + inputUrl
                    : inputUrl;
            loadUrl(targetUrl);
        } else {
            Toast.makeText(this, "Please enter a valid URL", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUrl(String url) {
        etUrl.setText(url);
        webView.loadUrl(url);
    }

    private void updateNavigationButtons() {
        btnWebBack.setAlpha(webView.canGoBack() ? 1.0f : 0.4f);
        btnWebForward.setAlpha(webView.canGoForward() ? 1.0f : 0.4f);
    }

    private void showPopupMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_web_view, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::handleMenuAction);
        popupMenu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return handleMenuAction(item) || super.onOptionsItemSelected(item);
    }

    private boolean handleMenuAction(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_refresh) {
            webView.reload();
            Toast.makeText(this, "Refreshing page...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_share) {
            String currentUrl = webView.getUrl();
            if (currentUrl != null && !currentUrl.isEmpty()) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, currentUrl);
                startActivity(Intent.createChooser(shareIntent, "Share URL via"));
            } else {
                Toast.makeText(this, "No URL to share", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_link_google) {
            loadUrl("https://www.google.com");
            return true;
        } else if (itemId == R.id.action_link_wikipedia) {
            loadUrl("https://www.wikipedia.org");
            return true;
        } else if (itemId == R.id.action_link_github) {
            loadUrl("https://www.github.com");
            return true;
        } else if (itemId == R.id.action_clear_cache) {
            webView.clearCache(true);
            webView.clearHistory();
            Toast.makeText(this, "Web cache and history cleared", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}