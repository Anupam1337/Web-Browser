package com.anupam.samdevil.webbrowser;

import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    TextView urlTV;
    Button forwardB;
    Button backB;
    Button reloadButton;
    SwipeRefreshLayout swipe;

    //URL validation
    public void Go(View view) {
        TextView urlTV =(TextView)findViewById(R.id.url);
        String url=urlTV.getText().toString();
        if((url.startsWith("https://www.") || url.startsWith("http://www."))) {

        } else if(!(url.startsWith("www.") || url.startsWith("www."))) {
            url="https://www."+url;
        } else if(!(url.startsWith("https://") || url.startsWith("http://"))) {
            url="https://"+url;
        }
        try {
            java.net.URLEncoder.encode(url, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(URLUtil.isValidUrl(url)) {
            urlTV.setText(url);
            webView.loadUrl(url);
        }
        else {
            webView.loadData("<html><body><h1>Wrong Url!</h1><p>Page not found.</p></body></html>", "text/html", "UTF-8");
        }
    }

    public void Back(View view) {
        if (webView.canGoBack()){
            forwardB.setEnabled(true);
            webView.goBack();
            if (!webView.canGoBack())
                backB.setEnabled(false);
        }
    }

    public void Reload(View view) {
        webView.reload();
    }

    public void Forward(View view) {
        if (webView.canGoForward()) {
            backB.setEnabled(true);
            webView.goForward();
            if (!webView.canGoForward())
                forwardB.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed(){

        if (webView.canGoBack()) {
            webView.goBack();
        }else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlTV = (TextView) findViewById(R.id.url);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        reloadButton = (Button)findViewById(R.id.reloadButton);
        forwardB = (Button) findViewById(R.id.forwardButton);
        backB = (Button) findViewById(R.id.backButton);
        forwardB.setEnabled(false);
        backB.setEnabled(false);

        urlTV.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Go(findViewById(R.id.GoButton));
                    return true;
                }
                return false;
            }
        });

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                WebAction();
            }
        });
        WebAction();
    }
    public void WebAction() {

        webView.getSettings().setAppCacheEnabled(true);
        webView.loadUrl("https://www.google.com");
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(webView.canGoBack())
                    backB.setEnabled(true);
                if(webView.canGoForward())
                    forwardB.setEnabled(true);
                urlTV.setText(url);
                if(reloadButton.isPressed()) {
                    swipe.setRefreshing(false);
                }

                Log.d("WebView", "your current url when webpage loading.." + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WebView", "your current url when webpage loading.. finish" + url);
                super.onPageFinished(view, url);
                urlTV.setText(url);
                swipe.setRefreshing(false);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                webView.loadUrl("file:///android_assets/error.html");

            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("when you click on any interlink on webview that time you got url :-" + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }
}
