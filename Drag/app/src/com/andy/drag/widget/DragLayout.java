package com.andy.drag.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.andy.drag.R;

/**
 * Created by Administrator
 * Time on 2015/11/30.
 * Description
 */
public class DragLayout extends ViewGroup {

	private static final int VEL_THRESHOLD = 100; // 滑动速度的阈值，超过这个绝对值认为是左右
	private static final int MIN_LEFT = 16;  //滑动的最小设置值
	private View mBgView;
	private View mDragView;

	private boolean isAllowDrag = true;
	private boolean isLayouted;
	private int mDragWidth;
	private OnDragListener mOnDragListener;

	/* 拖拽工具类 */
	private final ViewDragHelper mDragHelper;
	private final GestureDetectorCompat mGestureDetector;

	public DragLayout(Context context) {
		this(context, null);
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mDragHelper = ViewDragHelper.create(this, 10f, new DragHelperCallback());
		mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_BOTTOM);
		mGestureDetector = new GestureDetectorCompat(getContext(), new YScrollDetector());
		setWillNotDraw(false);
	}

	class YScrollDetector extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
			// 垂直滑动时dy>dx，才被认定是上下拖动
			return Math.abs(dy) < Math.abs(dx);
		}
	}

	@Override
	public void computeScroll() {
		if (mDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}else if (mDragView.getLeft() >= mDragWidth) {
			if(mOnDragListener != null)
				mOnDragListener.close();
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mBgView = getChildAt(0);
		mDragView = getChildAt(1);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);

		int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
		int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
				resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
	}

	/**
	 * 这是View的方法，该方法不支持android低版本（2.2、2.3）的操作系统，所以手动复制过来以免强制退出
	 */
	public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
		int result = size;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		switch (specMode) {
			case MeasureSpec.UNSPECIFIED:
				result = size;
				break;
			case MeasureSpec.AT_MOST:
				if (specSize < size) {
					result = specSize | MEASURED_STATE_TOO_SMALL;
				} else {
					result = size;
				}
				break;
			case MeasureSpec.EXACTLY:
				result = specSize;
				break;
		}
		return result | (childMeasuredState & MEASURED_STATE_MASK);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!isLayouted) {
			// 只在初始化的时候调用
			// 一些参数作为全局变量保存起来
			mBgView.layout(l, 0, r, b - t);
			mDragView.layout(l, 0, r, b - t);
			mDragWidth = mDragView.getMeasuredWidth();
		} else {
			// 如果已被初始化，这次onLayout只需要将之前的状态存入即可
			mBgView.layout(l, mBgView.getTop(), r, mBgView.getBottom());
			mDragView.layout(l, mDragView.getTop(), r, mDragView.getBottom());
		}
	}

	/* touch事件的拦截与处理都交给mDraghelper来处理 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!isAllowDrag) {
			return false;
		}

		boolean xScroll = mGestureDetector.onTouchEvent(ev);
		boolean shouldIntercept = false;
		try {
			shouldIntercept = mDragHelper.shouldInterceptTouchEvent(ev);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int action = ev.getActionMasked();

		if (action == MotionEvent.ACTION_DOWN) {
			// action_down时就让mDragHelper开始工作，否则有时候导致异常 他大爷的
			isLayouted = true; // 触摸过一次之后，认为已经layout成功
			mDragHelper.processTouchEvent(ev);
		}

		return shouldIntercept && xScroll;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// 统一交给mDragHelper处理，由DragHelperCallback实现拖动效果
		mDragHelper.processTouchEvent(ev); // 该行代码可能会抛异常，正式发布时请将这行代码加上try catch
		return true;
	}

	/**
	 * 这是拖拽效果的主要逻辑
	 */
	private class DragHelperCallback extends ViewDragHelper.Callback {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			// 两个子View都需要跟踪，返回true
			return child == mDragView;
		}

		@Override
		public int getViewHorizontalDragRange(View child) {
			return 1;
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// 滑动松开后，需要向上或者乡下粘到特定的位置
			animLeftOrRight(releasedChild, xvel);
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if ((left + dx) < MIN_LEFT) {
				return 0;
			}
			return left;
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			invalidate();
		}
	}

	private void animLeftOrRight(View releasedChild, float xvel) {
		int finalLeft = 0; // 默认是滑到最左端
		if (xvel > VEL_THRESHOLD || ((mDragView.getLeft()) > mDragWidth / 3)) {
			finalLeft = mDragWidth;
		}
		if (mDragHelper.smoothSlideViewTo(mDragView, finalLeft, 0)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	@Override
	public void draw(Canvas c) {
		super.draw(c);

		Drawable shadowDrawable = getResources().getDrawable(R.drawable.sliding_back_shadow);
		if (mDragView == null || shadowDrawable == null || mDragView.getLeft() >= mDragWidth) {
			// No need to draw a shadow if we don't have one.
			return;
		}

		final int top = mDragView.getTop();
		final int bottom = mDragView.getBottom();

		final int shadowWidth = shadowDrawable.getIntrinsicWidth();
		final int right = mDragView.getLeft();
		final int left = right - shadowWidth;

		shadowDrawable.setBounds(left, top, right, bottom);
		shadowDrawable.draw(c);
	}

	public void setAllowDrag(boolean allowDrag) {
		this.isAllowDrag = allowDrag;
	}

	public interface OnDragListener {
		void close();
	}

	public void setOnDragListener(OnDragListener listener) {
		mOnDragListener = listener;
	}
}
