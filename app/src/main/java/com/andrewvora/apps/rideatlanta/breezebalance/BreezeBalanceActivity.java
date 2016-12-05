package com.andrewvora.apps.rideatlanta.breezebalance;

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
 * Created by faytx on 11/16/2016.
 * @author Andrew Vorakrajangthiti
 */
public class BreezeBalanceActivity extends AppCompatActivity {

    @BindView(R.id.web_view_toolbar) Toolbar mToolbar;
    @BindView(R.id.web_view) WebView mWebView;
    @BindView(R.id.web_view_progress_bar) ProgressBar mPageProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breeze_balance);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.breeze_balance_url);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                view.scrollTo(0, 0);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if(error.getUrl().equals(getString(R.string.breeze_balance_url))) {
                    handler.proceed();
                }
                else super.onReceivedSslError(view, handler, error);
            }
        });
        mWebView.getSettings().setSupportZoom(true);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                int progressBarVisibility = newProgress >= 100 ?
                        View.INVISIBLE :
                        View.VISIBLE;

                mPageProgressBar.setProgress(newProgress);
                mPageProgressBar.setVisibility(progressBarVisibility);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mToolbar.setTitle(title);
            }
        });

        mWebView.loadUrl(getString(R.string.breeze_balance_url));
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
                mWebView.reload();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
