package ifgi.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends Activity {

	public static String APP_URL = "http://app.uni-muenster.de/";

	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initWebView();
		if (savedInstanceState == null) {
			webView.loadUrl(APP_URL); // Loads the campus plan
		}
		this.setContentView(webView);
	}

	@Override
	protected void onPause() {
		super.onPause();
		webView.pauseTimers();
	}

	@Override
	protected void onResume() {
		super.onResume();
		webView.resumeTimers();
	}

	private void initWebView() {
		webView = new WebView(this);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.setWebViewClient(new mWebViewClient());

		webView.setWebChromeClient(new mWebChromeClient());
		webView.getSettings().setGeolocationDatabasePath(
				getFilesDir().getPath());
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setDatabaseEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView.isFocused() && webView.canGoBack()) {
				webView.goBack();
			} else {
				super.onBackPressed();
				finish();
			}

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the state of the WebView
		this.webView.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// Restore the state of the WebView
		this.webView.restoreState(savedInstanceState);
	}

	public static Intent newEmailIntent(Context context, String address,
			String subject, String body, String cc) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
		intent.putExtra(Intent.EXTRA_TEXT, body);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_CC, cc);
		intent.setType("message/rfc822");
		return intent;
	}

	private class mWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			if (url.startsWith("mailto:")) {
				MailTo mt = MailTo.parse(url);
				Intent i = newEmailIntent(WebViewActivity.this, mt.getTo(),
						mt.getSubject(), mt.getBody(), mt.getCc());
				startActivity(i);
				view.reload();
				return true;
			} else if (Uri.parse(url).getHost()
					.equals(Uri.parse(APP_URL).getHost())) {
				// This is my web site, so do not override; let my WebView load
				// the page
				return false;
			}
			// Otherwise, the link is not for a page on my site, so launch
			// another Activity that handles URLs
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
			return true;
		}
	}

	private class mWebChromeClient extends WebChromeClient {

		public void onGeolocationPermissionsShowPrompt(String origin,
				GeolocationPermissions.Callback callback) {
			callback.invoke(origin, true, false);
		}

	}
}
