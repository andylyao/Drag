package com.andy.drag.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator
 * Time on 2015/12/1.
 * Description
 */
public class DragViewPager extends ViewPager{

	private float mDownX = 0;
	private boolean mNeedConsumeTouch = true;
	/** 是否允许向右滑消失 */
	private boolean mAllowDrag = true;

	public DragViewPager(Context context) {
		super(context);
	}

	public DragViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			mDownX = ev.getRawX();
			mNeedConsumeTouch = true; // 默认情况下，scrollView内部的滚动优先，默认情况下由该ScrollView去消费touch事件

			if (getScrollX() <= 0) {
				// 允许向右拖动
				mAllowDrag = true;
			} else {
				// 不允许向右拖动
				mAllowDrag = false;
			}
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			if (!mNeedConsumeTouch) {
				// 在最顶端且向上拉了，则这个touch事件交给父类去处理
				getParent().requestDisallowInterceptTouchEvent(false);
				return false;
			} else if (mAllowDrag) {
				// needConsumeTouch尚未被定性，此处给其定性
				// 允许拖动到底部的下一页，而且又向上拖动了，就将touch事件交给父view
				if (ev.getRawX() - mDownX > 2) {
					// flag设置，由父类去消费
					mNeedConsumeTouch = false;
					getParent().requestDisallowInterceptTouchEvent(false);
					return false;
				}
			}
		}

		// 通知父view是否要处理touch事件
		getParent().requestDisallowInterceptTouchEvent(mNeedConsumeTouch);
		return super.dispatchTouchEvent(ev);
	}
}
