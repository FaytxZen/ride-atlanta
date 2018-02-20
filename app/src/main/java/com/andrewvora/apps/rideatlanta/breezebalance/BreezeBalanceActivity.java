package com.andrewvora.apps.rideatlanta.breezebalance;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.andrewvora.apps.rideatlanta.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays the Breeze Balance site in a {@link WebView}.
 *
 * Created by faytx on 11/16/2016.
 * @author Andrew Vorakrajangthiti
 */
public class BreezeBalanceActivity extends AppCompatActivity {

    @BindView(R.id.web_view_toolbar) Toolbar toolbar;
    @BindView(R.id.web_view) WebView webView;
    @BindView(R.id.web_view_progress_bar) ProgressBar pageProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breeze_balance);
        ButterKnife.bind(this);

        configureToolbar();
        configureWebView();

        webView.loadUrl(getString(R.string.breeze_balance_url));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_refresh:
                webView.reload();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebView() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // fix to prevent page from starting in the center
                // or some other location
                view.scrollTo(0, 0);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // ignore incorrectly signed SSL certificates
                // currently, this is an issue with the official site
                if(error.getUrl().equals(getString(R.string.breeze_balance_url))) {
                    handler.proceed();
                }
                else super.onReceivedSslError(view, handler, error);
            }
        });

        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                int progressBarVisibility = newProgress >= 100 ?
                        View.INVISIBLE :
                        View.VISIBLE;

                pageProgressBar.setProgress(newProgress);
                pageProgressBar.setVisibility(progressBarVisibility);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                toolbar.setTitle(title);
            }
        });
    }

    private void configureToolbar() {
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.breeze_balance_url);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
