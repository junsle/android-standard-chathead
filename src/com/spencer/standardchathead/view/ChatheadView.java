package com.spencer.standardchathead.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spencer.standardchathead.R;

public class ChatheadView extends RelativeLayout {
	private static final String TAG = "ChatheadView";
	private DisplayMetrics displayMetrics;

	private Context mContext;
	private ChatheadView chatheadView;
	private ImageView chatheadImageView;
	private FrameLayout chatheadImageFrame;
	private TextView chatheadTextView;
	private LinearLayout chatheadTextLayout;
	private View marginView;

	private ChatheadTrashView trashView;

	private final int SENSITIVITY = 1000;
	private boolean willViewRemove = false;

	private enum MarginPostion {
		LEFT, RIGHT,
	}

	/** default coordinates for chathead at startup. */
	private WindowManager.LayoutParams params;

	private WindowManager windowManager;

	private ChatheadListener onChatheadListener;

	public interface ChatheadListener {
		public void onClick(ChatheadView v);

		public void onTouch(ChatheadView v);

		public void onDragging(ChatheadView v);

		public void onStopDrag(ChatheadView v);

		public void onTrashMeets(ChatheadView v);

		public void onTrashNotMeets(ChatheadView v);

		public void onDestoryView(ChatheadView v);
	}

	public ChatheadView(Context context, ChatheadTrashView trashView,
			WindowManager windowManager, WindowManager.LayoutParams params) {
		super(context);
		this.mContext = context;
		this.chatheadView = this;

		this.windowManager = windowManager;
		this.params = params;

		this.trashView = trashView;

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_chathead, this, true);

		// capture views
		this.marginView = (View) findViewById(R.id.chathead_margin_view);
		this.chatheadImageView = (ImageView) findViewById(R.id.chathead_imageview);
		this.chatheadImageFrame = (FrameLayout) findViewById(R.id.chathead_image_frame);
		this.chatheadTextView = (TextView) findViewById(R.id.chathead_textview);
		this.chatheadTextLayout = (LinearLayout) findViewById(R.id.chathead_buble_layer);

		this.setListeners();

		this.removeBubbleView();

		this.removeTrash();

	}

	private int getDisplayWidth() {
		if (displayMetrics == null)
			displayMetrics = new DisplayMetrics();

		windowManager.getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.widthPixels;
	}

	private int getDisplayHeight() {
		if (displayMetrics == null)
			displayMetrics = new DisplayMetrics();

		windowManager.getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}

	public void setOnChatheadClickListener(ChatheadListener listener) {
		if (listener != null)
			onChatheadListener = listener;
	}

	private void setListeners() {
		chatheadImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onChatheadListener != null) {
					onChatheadListener.onClick(chatheadView);
				}
			}
		});

		chatheadImageView.setOnTouchListener(new View.OnTouchListener() {
			private int initialX, initialY;
			private float initialTouchX, initialTouchY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					removeMarginView();
					addTrash();

					chatheadTextLayout.setVisibility(View.GONE);

					// set up coordinates.
					initialX = params.x;
					initialY = params.y;
					initialTouchX = event.getRawX();
					initialTouchY = event.getRawY();

					// filter.
					chatheadImageView.getDrawable().setColorFilter(0x5F000000,
							PorterDuff.Mode.SRC_ATOP);
					invalidate();

					// callback for onTouch listener.
					if (onChatheadListener != null) {
						onChatheadListener.onTouch(chatheadView);
					}

					if (initialX > (getDisplayWidth() / 2)) {
						initialX -= getChatheadImageView().getWidth();
					}

					return true;

				case MotionEvent.ACTION_UP:
					removeTrash();
					chatheadTextLayout.setVisibility(View.VISIBLE);
					// filter.
					chatheadImageView.getDrawable().clearColorFilter();
					invalidate();

					// make onClick insensitive
					int width = (chatheadImageView.getWidth());
					int height = (chatheadImageView.getHeight());
					int widthUp = width
							+ (int) (event.getRawX() - initialTouchX);
					int heightUp = height
							+ (int) (event.getRawY() - initialTouchY);

					int initialArea = width * height;
					int endArea = widthUp * heightUp;
					int range = Math.abs(endArea - initialArea);

					boolean isClick = range <= SENSITIVITY;

					if (isClick) {
						// we regard as onClick.
						v.performClick();
						break;
					}

					// callback for onStopDrag listener.
					if (onChatheadListener != null) {
						onChatheadListener.onStopDrag(chatheadView);
					}

					if (isDestroyView()) {
						onChatheadListener.onDestoryView(chatheadView);
						return true;
					}

					setPositionToEdge();

					return true;

				case MotionEvent.ACTION_MOVE:
					// callback for onDragging listener.
					params.x = initialX
							+ (int) (event.getRawX() - initialTouchX);
					params.y = initialY
							+ (int) (event.getRawY() - initialTouchY);

					windowManager.updateViewLayout(chatheadView, params);

					if (onChatheadListener != null) {
						onChatheadListener.onDragging(chatheadView);
					}

					if (isTrashMeets(params.x, params.y)) {
						willViewRemove = true;
						onChatheadListener.onTrashMeets(chatheadView);

					} else {
						willViewRemove = false;
						onChatheadListener.onTrashNotMeets(chatheadView);

					}

					return true;

				}

				return false;
			}
		});
	}

	public ImageView getChatheadImageView() {
		return chatheadImageView;
	}

	public boolean isDestroyView() {
		return this.willViewRemove;
	}

	public void addBubbleView() {
		this.chatheadTextLayout.setVisibility(View.VISIBLE);
		this.chatheadTextView.setVisibility(View.VISIBLE);
	}

	public void removeBubbleView() {
		this.chatheadTextLayout.setVisibility(View.GONE);
		this.chatheadTextView.setVisibility(View.GONE);
	}

	public void addMarginView(MarginPostion position) {
		int whichSide = -1;
		RelativeLayout.LayoutParams marginParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, 0);

		switch (position) {
		case LEFT:
			whichSide = RelativeLayout.LEFT_OF;
			break;

		case RIGHT:
			whichSide = RelativeLayout.RIGHT_OF;
			break;
		}

		marginParams.addRule(whichSide, chatheadImageFrame.getId());
		this.marginView.setLayoutParams(marginParams);
		this.marginView.setVisibility(View.VISIBLE);
	}

	public void removeMarginView() {
		this.marginView.setVisibility(View.GONE);
	}

	public void addTrash() {
		trashView.setVisibility(View.VISIBLE);
	}

	public void removeTrash() {
		trashView.setVisibility(View.GONE);
	}

	private void setPositionToEdge() {
		if (params.x > 0 && params.x < (getDisplayWidth() / 2)) {
			params.x = 0;
			this.addMarginView(MarginPostion.RIGHT);
			windowManager.updateViewLayout(chatheadView, params);

		} else if (params.x > 0 && params.x >= (getDisplayWidth() / 2)) {
			this.addMarginView(MarginPostion.LEFT);
			params.x = getDisplayWidth();
			windowManager.updateViewLayout(chatheadView, params);

		}
	}

	private boolean isTrashMeets(int positionX, int positionY) {
		int trashX = trashView.getTrashFrame().getLeft();
		int trashY = trashView.getTrashFrame().getTop();
		int trashPosX = getDisplayWidth() - trashX;
		int trashPosY = getDisplayHeight() - trashY;

		final int trashArea = trashPosX * trashPosY;

		int viewPosX = getDisplayWidth() - positionX;
		int viewPosY = getDisplayHeight() - positionY;
		final int viewArea = viewPosX * viewPosY;

		final int value = Math.abs(trashArea - viewArea);

		return value < 14000;
	}
}
