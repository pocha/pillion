package com.getpillion;

import android.os.Bundle;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.appcovery.android.appcoveryapp.R;
import com.getpillion.common.Constant;
import com.bugsense.trace.BugSenseHandler;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class Like extends SherlockFragmentActivity {

	private SlidingMenu menu = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(getApplicationContext(),
				Constant.BUGSENSE_API_KEY);
		setContentView(R.layout.like);

		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setTitle("Like AppCovery");

		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.0f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		menu.setMenu(R.layout.menu);

		WebView webview;
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		// webview.loadUrl("http://www.facebook.com/Qples");

		String customHtml = "<html><body><iframe src='http://www.facebook.com/plugins/like.php?href=http%3A%2F%2Fdevelopers.facebook.com%2Fdocs%2Freference%2Fplugins%2Flike&amp;send=false&amp;layout=box_count&amp;width=450&amp;show_faces=true&amp;font&amp;colorscheme=light&amp;action=like&amp;height=65' scrolling='no' frameborder='0' style='border:none; overflow:hidden; width:450px; height:65px;' allowTransparency='true'></iframe></body></html>";
		webview.loadData(customHtml, "text/html", "UTF-8");

	}

}
