package com.keyboardr.elevatorlistview;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.WrapperListAdapter;

public class ElevatorListView extends ListView {

	private final class ResizingScrollListener implements OnScrollListener {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mScrollListener != null) {
				mScrollListener.onScrollStateChanged(view, scrollState);
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (visibleItemCount > 0) {
				int firstTop = view.getChildAt(0).getTop();
				ListAdapter adapter = getAdapter();
				while (adapter instanceof WrapperListAdapter) {
					adapter = ((WrapperListAdapter) adapter)
							.getWrappedAdapter();
				}
				for (int i = 0; i < visibleItemCount; i++) {
					// If the view is not a footer
					if (firstVisibleItem + i < adapter.getCount()) {
						switch (i) {
							case 0:
								setFirstItemHeight(view.getChildAt(i));
								break;
							case 1:
								setSecondItemHeight(view.getChildAt(i),
										-firstTop);
								break;
							default:
								setOtherItemHeight(view.getChildAt(i));
								break;
						}
					}
				}
			}
			if (mScrollListener != null) {
				mScrollListener.onScroll(view, firstVisibleItem,
						visibleItemCount, totalItemCount);
			}
		}

		private void setFirstItemHeight(View item) {
			ViewGroup.LayoutParams layoutParams = item.getLayoutParams();
			layoutParams.height = mExpandedItemHeight;
			item.setLayoutParams(layoutParams);
		}

		private void setSecondItemHeight(final View item, int progress) {
			int minHeight = mCollapsedItemHeight;
			int maxHeight = mExpandedItemHeight;
			int maxProgress = mExpandedItemHeight;
			int height = minHeight
					+ (int) (mInterpolator.getInterpolation(((float) progress)
							/ ((float) maxProgress)) * (maxHeight - minHeight));
			ViewGroup.LayoutParams layoutParams = item.getLayoutParams();
			layoutParams.height = height;
			item.setLayoutParams(layoutParams);
		}

		private void setOtherItemHeight(View item) {
			ViewGroup.LayoutParams layoutParams = item.getLayoutParams();
			layoutParams.height = mCollapsedItemHeight;
			item.setLayoutParams(layoutParams);
		}
	}

	private static final class AdapterWrapper implements WrapperListAdapter {

		private final ListAdapter mAdapter;
		private final int mExpandedItemHeight;

		public AdapterWrapper(ListAdapter adapter, int expandedItemHeight) {
			mAdapter = adapter;
			mExpandedItemHeight = expandedItemHeight;
		}

		@Override
		public int getCount() {
			return mAdapter.getCount();
		}

		@Override
		public Object getItem(int position) {
			return mAdapter.getItem(position);
		}

		@Override
		public long getItemId(int position) {
			return mAdapter.getItemId(position);
		}

		@Override
		public int getItemViewType(int position) {
			return mAdapter.getItemViewType(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mAdapter.getView(position, convertView, parent);
			// Reset image to full size. Recycling may have issues when
			// scrolling up
			// otherwise
			ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
			layoutParams.height = mExpandedItemHeight;
			convertView.setLayoutParams(layoutParams);
			return convertView;
		}

		@Override
		public int getViewTypeCount() {
			return mAdapter.getViewTypeCount();
		}

		@Override
		public boolean hasStableIds() {
			return mAdapter.hasStableIds();
		}

		@Override
		public boolean isEmpty() {
			return mAdapter.isEmpty();
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			mAdapter.registerDataSetObserver(observer);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			mAdapter.unregisterDataSetObserver(observer);
		}

		@Override
		public boolean areAllItemsEnabled() {
			return mAdapter.areAllItemsEnabled();
		}

		@Override
		public boolean isEnabled(int position) {
			return mAdapter.isEnabled(position);
		}

		@Override
		public ListAdapter getWrappedAdapter() {
			return mAdapter;
		}

	}

	private OnScrollListener mScrollListener;
	private boolean hasAddedSpacer;
	private View mSpacer;
	private LayoutParams mSpacerParams;
	private ResizingScrollListener mResizingScrollListener = new ResizingScrollListener();

	private int mCollapsedItemHeight;
	private int mExpandedItemHeight;

	private static final int DEFAULT_COLLAPSED_HEIGHT_DP = 100;
	private static final int DEFAULT_EXPANDED_HEIGHT_DP = 300;

	public ElevatorListView(Context context) {
		super(context);
		init();
		float density = getResources().getDisplayMetrics().density;
		mCollapsedItemHeight = (int) (DEFAULT_COLLAPSED_HEIGHT_DP * density);
		mExpandedItemHeight = (int) (DEFAULT_EXPANDED_HEIGHT_DP * density);
	}

	public ElevatorListView(Context context, int collapsedItemHeight,
			int expandedItemHeight) {
		super(context);
		init();
		mCollapsedItemHeight = collapsedItemHeight;
		mExpandedItemHeight = expandedItemHeight;
	}

	public ElevatorListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ElevatorListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ElevatorListView);
		Log.d(getClass().getSimpleName(), "Size of obtainStyledAttributes(): "
				+ a.getIndexCount());
		float density = getResources().getDisplayMetrics().density;
		mCollapsedItemHeight = a.getDimensionPixelSize(
				R.styleable.ElevatorListView_collapsedItemHeight,
				(int) (DEFAULT_COLLAPSED_HEIGHT_DP * density));
		mExpandedItemHeight = a.getDimensionPixelSize(
				R.styleable.ElevatorListView_expandedItemHeight,
				(int) (DEFAULT_EXPANDED_HEIGHT_DP * density));
		a.recycle();
		init();
	}

	private void init() {
		super.setOnScrollListener(mResizingScrollListener);
		mSpacer = new View(getContext());
		mSpacerParams = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
				0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int spacerSize = getMeasuredHeight() - mExpandedItemHeight;
		mSpacerParams.height = spacerSize;
		mSpacer.setLayoutParams(mSpacerParams);
		if (!hasAddedSpacer) {
			ListAdapter adapter = getAdapter();
			addFooterView(mSpacer);
			setAdapter(adapter);
			hasAddedSpacer = true;
		}
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		post(new Runnable() {

			@Override
			public void run() {
				updateSizes();
			}

		});
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (adapter != null) {
			super.setAdapter(new AdapterWrapper(adapter, mExpandedItemHeight));
		} else {
			super.setAdapter(adapter);
		}
	}

	public int getCollapsedItemHeight() {
		return mCollapsedItemHeight;
	}

	public int getExpandedItemHeight() {
		return mExpandedItemHeight;
	}

	private void updateSizes() {
		mResizingScrollListener.onScroll(this, getFirstVisiblePosition(),
				getChildCount(), getCount());
		int spacerSize = getMeasuredHeight() - mExpandedItemHeight;
		mSpacerParams.height = spacerSize;
		mSpacer.setLayoutParams(mSpacerParams);
	}

	private Interpolator mInterpolator = new Interpolator() {

		@Override
		public float getInterpolation(float input) {
			return input;
		}

	};

}
