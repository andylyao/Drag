package com.andy.drag;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.andy.drag.cache.BitmapLruCache;
import com.andy.drag.util.Utils;
import com.andy.drag.widget.DragLayout;

/**
 * Created by Administrator
 * Time on 2015/11/30.
 * Description 基类，实现了Drag功能
 */
public class BaseActivity extends AppCompatActivity implements DragLayout.OnDragListener, View.OnClickListener{

	public static final String KEY_BITMAP = "bitmap_key";
	private DragLayout mDragLayout;

	@Override
	public void setContentView(int layoutResID) {
		setMobileContentView(LayoutInflater.from(this).inflate(layoutResID, null));
	}

	@Override
	public void setContentView(View midLayout) {
		setMobileContentView(midLayout);
	}

	private void setMobileContentView(View layout) {
		if(isAllowDrag() && !TextUtils.isEmpty(getIntentBitmapKey())) {
			super.setContentView(R.layout.activity_base);
			mDragLayout = (DragLayout) findViewById(R.id.base_drag);
			mDragLayout.setOnDragListener(this);
			mDragLayout.setAllowDrag(true);
			FrameLayout frameLayout = (FrameLayout) findViewById(R.id.base_frame);
			frameLayout.addView(layout, new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			bindBackgroundData();
		} else {
			super.setContentView(layout);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(isAllowDrag())
			bindBackgroundData();
	}

	private void bindBackgroundData() {
		ImageView background = (ImageView) findViewById(R.id.base_bg);
		if(background == null) return;
		Bitmap bitmap = null;
		try{
			String bitmapKey = getIntentBitmapKey();
			if(!TextUtils.isEmpty(bitmapKey)) {
				bitmap = BitmapLruCache.getInstance().get(bitmapKey);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(bitmap != null) {
			background.setImageBitmap(bitmap);
			/*if (Build.VERSION.SDK_INT >= 16) {
				background.setBackground(new BitmapDrawable(bitmap));
			} else {
				background.setBackgroundDrawable(new BitmapDrawable(bitmap));
			}*/
		}
	}

	@Override
	public void startActivityForResult(final Intent intent, final int requestCode) {
		getWindow().getDecorView().postDelayed(new Runnable() {
			@Override
			public void run() {
				final String bitmapKey = getLocalClassName() + "_screen_short";
				Bitmap bitmap = Utils.takeScreenShort(BaseActivity.this);
				BitmapLruCache.getInstance().put(bitmapKey, bitmap);
				intent.putExtra(KEY_BITMAP, bitmapKey);
				BaseActivity.super.startActivityForResult(intent, requestCode);
			}
		}, 100);

	}

	private String getIntentBitmapKey() {
		try{
			return getIntent().getExtras().getString(KEY_BITMAP);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void close() {
		finish();
	}

	protected boolean isAllowDrag() {
		return true;
	}

	@Override
	public void onClick(View v) {

	}
}
