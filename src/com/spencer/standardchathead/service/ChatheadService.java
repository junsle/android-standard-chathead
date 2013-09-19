package com.spencer.standardchathead.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;

import com.spencer.standardchathead.R;
import com.spencer.standardchathead.view.ChatheadTrashView;
import com.spencer.standardchathead.view.ChatheadView;

public class ChatheadService extends Service implements
		ChatheadView.ChatheadListener {

	private Context mContext;
	private WindowManager windowManager;

	private ChatheadView chatheadView;
	private ChatheadTrashView chatheadTrashView;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		chatheadTrashView = new ChatheadTrashView(mContext, windowManager, params);

		chatheadView = new ChatheadView(mContext, chatheadTrashView, windowManager, params);
		chatheadView.setOnChatheadClickListener(this);

		params.gravity = Gravity.TOP | Gravity.CENTER;
		windowManager.addView(chatheadTrashView, params);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;
		windowManager.addView(chatheadView, params);

		chatheadView.getChatheadImageView().setImageDrawable(mContext.getResources().getDrawable(R.drawable.pic));
		
	}

	@Override
	public void onClick(ChatheadView v) {
		
	}

	@Override
	public void onTouch(ChatheadView v) {
	}

	@Override
	public void onDragging(ChatheadView v) {
	}

	@Override
	public void onStopDrag(ChatheadView v) {

	}

	@Override
	public void onTrashMeets(ChatheadView v) {
		chatheadTrashView.getTrashFrame().setBackgroundColor(Color.WHITE);
	}

	@Override
	public void onTrashNotMeets(ChatheadView v) {
		chatheadTrashView.getTrashFrame().setBackgroundColor(Color.TRANSPARENT);
	}

	@Override
	public void onDestoryView(ChatheadView v) {
		windowManager.removeViewImmediate(chatheadTrashView);
		windowManager.removeViewImmediate(chatheadView);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (chatheadView != null)
			windowManager.removeView(chatheadView);
	}
}
