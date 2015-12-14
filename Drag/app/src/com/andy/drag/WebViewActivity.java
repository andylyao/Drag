package com.andy.drag;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewActivity extends BaseActivity {

	private WebView mWebView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		mWebView = (WebView) findViewById(R.id.web_view);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setDefaultTextEncodingName("GBK");
		webSettings.setJavaScriptEnabled(true);
		webSettings.setNeedInitialFocus(false); // 第一次加载焦点问题

		//mWebView.setWebViewClient(new MyWebViewClient());
		//mWebView.setWebChromeClient(new MyWebChromeClient());
		mWebView.loadUrl("https://www.baidu.com/s?cl=3&tn=baidutop10&fr=top1000&wd=同时举办婚礼葬礼&rsv_idx=2");
	}
}
