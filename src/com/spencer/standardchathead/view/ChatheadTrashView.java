package com.spencer.standardchathead.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.spencer.standardchathead.R;

public class ChatheadTrashView extends RelativeLayout {
	private FrameLayout trashFrame;
	private ImageView trashImageView;
	
	public ChatheadTrashView(Context context, WindowManager windowManager, WindowManager.LayoutParams params) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_chathead_trash, this, true);

		trashImageView = (ImageView) findViewById(R.id.chathead_trash_image);
		trashFrame = (FrameLayout) findViewById(R.id.chathead_trash_frame);
		
	}
	
	public FrameLayout getTrashFrame() {
		if (trashFrame == null)
			trashFrame = (FrameLayout) findViewById(R.id.chathead_image_frame); 
		
		return trashFrame;
	}
	
	public ImageView getImageView() {
		if (trashImageView == null)
			trashImageView = (ImageView) findViewById(R.id.chathead_imageview);
		
		return trashImageView;
	}
	
	public void setFrameBackgroundColor(int color) {
		getTrashFrame().setBackgroundColor(color);
	}
}
