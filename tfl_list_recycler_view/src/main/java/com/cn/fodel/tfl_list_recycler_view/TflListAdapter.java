package com.cn.fodel.tfl_list_recycler_view;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tfl on 2016/10/12.
 * <p/>
 * this is a recycler view adapter with empty view, loading view,data view,header view,footer view
 * if you want to enable each views, you should Override each onCreateViewHolder method and onBindViewHolder method
 */
public abstract class TflListAdapter<T> extends RecyclerView.Adapter {

    private static final String TAG = "TflListAdapter";

    /**
     * data set
     */
    public List<T> mData;
    /**
     * headers
     */
    public List<Object> mHeaders;
    /**
     * footers
     */
    public List<Object> mFooters;
    /**
     * recycler view mode
     */
    public int mMode;

    /**
     * tool bar height
     */
    //because our toobar is ThemeOverlay, so we should minus toolbar height,
    //you can use AppBarLayout.getMeasuredHeight method to get toobar height.
    protected int mToolBarHeight;

    protected TflListInterface.OnEmptyViewClickListener mOnEmptyViewClickListener;
    protected TflListInterface.OnErrorViewClickListener mOnErrorViewClickListener;
    protected TflListInterface.OnItemClickListener mOnItemClickListener;
    public TflListAdapter(List<T> data) {
        this(data, TflListModel.MODE_DATA, 0);
    }

    public TflListAdapter(List<T> data, int mode) {
        this(data, mode, 0);
    }

    public TflListAdapter(List<T> data, int mode, int toolBarHeight) {
        this(data, null, null, mode, toolBarHeight);
    }

    public TflListAdapter(List<T> data, List<Object> headers, List<Object> footers, int mode, int toolBarHeight) {
        this.mData = null == data ? new ArrayList<T>() : data;
        this.mHeaders = null == headers ? new ArrayList<Object>() : headers;
        this.mFooters = null == footers ? new ArrayList<Object>() : footers;
        this.mMode = mData.isEmpty() ? TflListModel.MODE_EMPTY : mode;
        this.mToolBarHeight = toolBarHeight;
    }

    public void setData(List<T> data, int mode) {
        this.mData = null == data ? new ArrayList<T>() : data;
        this.mMode = mData.isEmpty() ? TflListModel.MODE_EMPTY : mode;
        this.notifyDataSetChanged();
    }

    public void addData(List<T> data) {
        if (null == data || data.isEmpty()) {
            return;
        }
        int startPosition = mData.size() + mHeaders.size();
        this.mData.addAll(data);
        this.notifyItemRangeInserted(startPosition, data.size());
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> data) {
        setData(data, TflListModel.MODE_DATA);
    }

    public List<Object> getHeaders() {
        return mHeaders;
    }

    public List<Object> getFooters() {
        return mFooters;
    }

    /**
     * change recycler view display mode
     *
     * @param mode mode
     */
    public void changeMode(int mode) {
        if (mode < 0 || mode > TflListModel.MODE_EMPTY) {
            mode = 0;
        }
        mMode = mode;
        this.notifyDataSetChanged();
    }

    public RecyclerView.ViewHolder onCreateLoadingViewHolder(ViewGroup parent) {
        return null;
    }

    public RecyclerView.ViewHolder onCreateErrorViewHolder(ViewGroup parent) {
        return null;
    }

    public abstract RecyclerView.ViewHolder onCreateEmptyViewHolder(ViewGroup parent);

    public abstract RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent);

    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent) {
        return null;
    }

    public abstract void onBindDataViewHolder(RecyclerView.ViewHolder holder, int position);

    public void onBindLoadingViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    public void onBindErrorViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    public void onBindEmptyViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    public void onBindFooterViewHolder(RecyclerView.ViewHolder holder, int position) {
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TflListModel.MODE_LOADING) {
            RecyclerView.ViewHolder loadingViewHolder = onCreateLoadingViewHolder(parent);
            //because our toobar is ThemeOverlay, so we should minus toolbar height
            loadingViewHolder.itemView.setLayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parent.getHeight() - mToolBarHeight)
            );
            return loadingViewHolder;
        }
        if (viewType == TflListModel.MODE_ERROR) {
            RecyclerView.ViewHolder errorViewHolder = onCreateErrorViewHolder(parent);
            //because our toobar is ThemeOverlay, so we should minus toolbar height
            errorViewHolder.itemView.setLayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parent.getHeight() - mToolBarHeight)
            );
            errorViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (null != mOnErrorViewClickListener) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mOnErrorViewClickListener.onErrorViewClick(v);
                            }
                        }, 200);
                    }
                }
            });
            return errorViewHolder;
        }
        if (viewType == TflListModel.MODE_EMPTY) {
            RecyclerView.ViewHolder emptyViewHolder = onCreateEmptyViewHolder(parent);
            //because our toobar is ThemeOverlay, so we should minus toolbar height
            emptyViewHolder.itemView.setLayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parent.getHeight() - mToolBarHeight)
            );
            emptyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (null != mOnEmptyViewClickListener) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mOnEmptyViewClickListener.onEmptyViewClick(v);
                            }
                        }, 200);
                    }
                }
            });
            return emptyViewHolder;
        }
        if (viewType == TflListModel.MODE_HEADER_VIEW) {
            RecyclerView.ViewHolder headerViewHolder = onCreateHeaderViewHolder(parent);
            return headerViewHolder;
        }
        if (viewType == TflListModel.MODE_FOOTER_VIEW) {
            RecyclerView.ViewHolder footerViewHolder = onCreateFooterViewHolder(parent);
            return footerViewHolder;
        }
        RecyclerView.ViewHolder dataViewHolder = onCreateDataViewHolder(parent);
        dataViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (null != mOnItemClickListener) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mOnItemClickListener.onItemClick(v, v.getTag());
                        }
                    }, 200);
                }
            }
        });
        return dataViewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mMode == TflListModel.MODE_LOADING) {
            onBindLoadingViewHolder(holder, position);
        } else if (mMode == TflListModel.MODE_ERROR) {
            onBindErrorViewHolder(holder, position);
        } else if (mMode == TflListModel.MODE_EMPTY) {
            onBindEmptyViewHolder(holder, position);
        } else {
            if (position < mHeaders.size()) {
                if (mHeaders.size() > 0) {
                    onBindHeaderViewHolder(holder, position);
                }
            } else if (position >= mHeaders.size() + mData.size()) {
                if (mFooters.size() > 0) {
                    onBindFooterViewHolder(holder, position - mHeaders.size() - mData.size());
                }
            } else {
                onBindDataViewHolder(holder, position - mHeaders.size());
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mMode == TflListModel.MODE_DATA) {
            return mData.size() + mHeaders.size() + mFooters.size();
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mMode == TflListModel.MODE_LOADING) {
            return TflListModel.MODE_LOADING;
        }
        if (mMode == TflListModel.MODE_ERROR) {
            return TflListModel.MODE_ERROR;
        }
        if (mMode == TflListModel.MODE_EMPTY) {
            return TflListModel.MODE_EMPTY;
        }
        //check what type our position is, based on the assumption that the order is headers > items > footers
        if (position < mHeaders.size()) {
            return TflListModel.MODE_HEADER_VIEW;
        } else if (position >= mHeaders.size() + mData.size()) {
            return TflListModel.MODE_FOOTER_VIEW;
        }
        return TflListModel.MODE_DATA;
    }

    public void setOnEmptyViewClickListener(TflListInterface.OnEmptyViewClickListener onEmptyViewClickListener) {
        mOnEmptyViewClickListener = onEmptyViewClickListener;
    }

    public void setOnErrorViewClickListener(TflListInterface.OnErrorViewClickListener onErrorViewClickListener) {
        mOnErrorViewClickListener = onErrorViewClickListener;
    }

    public void setOnItemClickListener(TflListInterface.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setToolBarHeight(int toolBarHeight) {
        mToolBarHeight = toolBarHeight;
        this.notifyDataSetChanged();
    }


    public int getFooterCount() {
        return mFooters.size();
    }

    public int getHeaderCount() {
        return mHeaders.size();
    }


    /**
     * add a header to the adapter
     *
     * @param header header
     */
    public void addHeader(Object header) {
        if (mMode != TflListModel.MODE_DATA) {
            Log.e(TAG, "error: you can not add header or footer while you are not in data mode");
            return;
        }
        if (!mHeaders.contains(header)) {
            mHeaders.add(header);
            //animate
            notifyItemInserted(mHeaders.size() - 1);
        }
    }

    /**
     * remove a header from the adapter
     *
     * @param header header
     */
    public void removeHeader(Object header) {
        if (mHeaders.contains(header)) {
            int position = mHeaders.indexOf(header);
            mHeaders.remove(position);
            //animate
            notifyItemRemoved(position);
        }
    }

    /**
     * add a footer to the adapter
     *
     * @param footer footer
     */
    public void addFooter(Object footer) {
        if (mMode != TflListModel.MODE_DATA) {
            Log.e(TAG, "error: you can not add header or footer while you are not in data mode");
            return;
        }
        if (!mFooters.contains(footer)) {
            mFooters.add(footer);
            //animate
            notifyItemInserted(mHeaders.size() + mData.size() + mFooters.size() - 1);
        }
    }

    /**
     * remove a footer from the adapter
     *
     * @param footer footer view
     */
    public void removeFooter(Object footer) {
        if (mFooters.contains(footer)) {
            int position = mFooters.indexOf(footer);
            mFooters.remove(position);
            //animate
            notifyItemRemoved(mHeaders.size() + mData.size() + position);
        }
    }

    /**
     * remove all headers
     */
    public void removeAllHeader() {
        if (mHeaders.size() > 0) {
            notifyItemRangeRemoved(0, mHeaders.size());
            mHeaders.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * remove all footers
     */
    public void removeAllFooters() {
        if (mFooters.size() > 0) {
            notifyItemRangeChanged(0, mFooters.size());
            mFooters.clear();
            notifyDataSetChanged();
        }
    }
}
