package com.carlisle.ben.newtoniangravitysimulator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "MainActivity";
	private DrawView draw;
	private int status = 0;
	private boolean toasting = false;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		draw = findViewById(R.id.space);
		draw.requestFocus();
		new Thread(draw).start();
		draw.setMain(this);
		AdView mAdView = findViewById(R.id.adView);
		MobileAds.initialize(this, "ca-app-pub-9314925334599539~5032929283");
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
	}

	public void notifyMessage(String s) {
		final String str = s;
		if (toasting) {
			toast.cancel();
		}
		runOnUiThread(new Runnable() {
			public void run() {
				toast = Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT);
				toasting = true;
				toast.show();
				Handler h = new Handler();
				h.postDelayed(new Runnable() {
					public void run() {
						toast.cancel();
						toasting = false;
					}
				}, 670);
			}
		});
	}

	@SuppressWarnings("UnusedParameters")
	public void restart(View v) {
		draw.restart();
	}

	@SuppressWarnings("UnusedParameters")
	public void increase(View v) {
		draw.increase();
	}

	@SuppressWarnings("UnusedParameters")
	public void decrease(View v) {
		draw.decrease();
	}

	@SuppressWarnings("UnusedParameters")
	public void change(View v) {
		draw.change();
	}

	public void onStop() {
		super.onStop();
		draw.onStop();
	}

	public void onResume() {
		super.onResume();
		draw.onResume();
	}

	@SuppressLint("InlinedApi")
	private void ads(final MenuItem i) {
		if (status == 0) {
			AlertDialog.Builder builder;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
			} else {
				builder = new AlertDialog.Builder(this);
			}
			builder.setTitle("Do you need to remove ads?")
					.setMessage("I'm trying to pay for college so please keep ads on if possible thanks.")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							status = 1;
							i.setTitle(R.string.on);
							findViewById(R.id.adView).setVisibility(View.INVISIBLE);
							LinearLayout l = findViewById(R.id.linearLayout);
							RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) l.getLayoutParams();
							params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
							l.setLayoutParams(params);
						}
					})
					.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.setIcon(android.R.drawable.ic_dialog_alert)
					.show();
		} else if (status == 1) {
			findViewById(R.id.adView).setVisibility(View.VISIBLE);
			i.setTitle(R.string.off);
			LinearLayout l = findViewById(R.id.linearLayout);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) l.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
			l.setLayoutParams(params);
			status = 0;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.layout, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.ads:
				ads(item);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
