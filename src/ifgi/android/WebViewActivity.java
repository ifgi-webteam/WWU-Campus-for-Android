package ifgi.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {

	WebView webview;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);

		if (this.webview == null)
		{
			this.webview = (WebView) this.findViewById(R.id.webView1);
			this.webview.setWebViewClient(new WebViewClient() {
				 public boolean shouldOverrideUrlLoading(WebView view, String url)
			        {
			            if(url.startsWith("mailto:"))
			            {
			                MailTo mt = MailTo.parse(url);
			                Intent i = newEmailIntent(WebViewActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
			                startActivity(i);
			                view.reload();
			                return true;
			            }

			            view.loadUrl(url);
			            return true;
			        }
			}); // Disables the adress
			this.webview.getSettings().setJavaScriptEnabled(true); // Enable JavaScript
		}
		
	    if (savedInstanceState == null)
	    {
			this.webview.loadUrl("http://app.uni-muenster.de"); // Loads the campus plan
	    }
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
		    if (webview.isFocused() && webview.canGoBack()) 
		    {
	            webview.goBack();       
		    }
		    else 
		    {
	            super.onBackPressed();
	            finish();
		    }
			
		    return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		// Save the state of the WebView
		this.webview.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);

		// Restore the state of the WebView
		this.webview.restoreState(savedInstanceState);
	}

	 public static Intent newEmailIntent(Context context, String address, String subject, String body, String cc) 
	 {
	        Intent intent = new Intent(Intent.ACTION_SEND);
	        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
	        intent.putExtra(Intent.EXTRA_TEXT, body);
	        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
	        intent.putExtra(Intent.EXTRA_CC, cc);
	        intent.setType("message/rfc822");
	        return intent;
	 }
}
