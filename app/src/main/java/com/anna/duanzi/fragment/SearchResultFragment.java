package com.anna.duanzi.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.activity.ImagePageActivity;
import com.anna.duanzi.activity.TxtActivity;
import com.anna.duanzi.activity.VideoActivity;
import com.anna.duanzi.activity.WebTxtActivity;
import com.anna.duanzi.base.BaseFragment;
import com.anna.duanzi.common.Constants;
import com.anna.duanzi.database.RecordsDao;
import com.anna.duanzi.domain.Duanzi;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.search.AVSearchQuery;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultFragment extends BaseFragment {
    private static final String ARG_PARAM = "param";
    private String param;
    LinkedList<Duanzi> searchResults = new LinkedList<>();
    SearchResultAdapter adapter;
    AVSearchQuery search;
    FindCallback<Duanzi> searchCallback;
    @Bind(R.id.search_result_listview)
    ListView listView;
    @Bind(R.id.loading)
    ProgressBar loadingView;
    @Bind(R.id.search_emtpy_result)
    TextView emptyResult;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    public static SearchResultFragment newInstance(String param) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param = getArguments().getString(ARG_PARAM);
            RecordsDao.getInstance(getActivity()).addRecords(param);
        }
    }

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_search_result, null);
        ButterKnife.bind(this, view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Duanzi duanzi = searchResults.get(position);
                category = duanzi.category;
                Intent intent = new Intent();
                switch (category) {
                    case Constants.CATEGORY_TXT:
                        intent.putExtra("id", duanzi.objectId);
                        intent.putExtra("title", duanzi.title);
                        intent.putExtra("content", duanzi.content);
                        String subString = duanzi.content.substring(0, 4);
                        if (subString.equals("http")) {
                            intent.putExtra("url", duanzi.content);
                            intent.setClass(getActivity(), WebTxtActivity.class);
                        } else {
                            intent.setClass(getActivity(), TxtActivity.class);
                        }
                        break;
                    case Constants.CATEGORY_IMAGE:
                        intent.putExtra("imageId", duanzi.objectId);
                        intent.setClass(getActivity(), ImagePageActivity.class);
                        break;
                    case Constants.CATEGORY_VIDEO:
                        intent.putExtra("videoId", duanzi.objectId);
                        AVFile videoFile = duanzi.getAVFile("data");
                        AVFile videoImageFile = duanzi.getAVFile("image");
                        intent.putExtra("videoUrl", videoFile.getUrl());
                        intent.putExtra("imageUrl", videoImageFile.getUrl());
                        intent.putExtra("title", duanzi.title);
                        intent.setClass(getActivity(), VideoActivity.class);
                        break;
                }
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        switch (param) {
            case "段子":
                param = Constants.CATEGORY_TXT;
                break;
            case "视频":
                param = Constants.CATEGORY_VIDEO;
                break;
            case "趣图":
                param = Constants.CATEGORY_IMAGE;
                break;
        }
        search = new AVSearchQuery(param, Duanzi.class);
        searchCallback = new FindCallback<Duanzi>() {
            @Override
            public void done(List<Duanzi> parseObjects, AVException e) {
                if (e == null) {
                    searchResults.addAll(parseObjects);
                    if (adapter == null) {
                        adapter = new SearchResultAdapter();
                        listView.setAdapter(adapter);
                        listView.setOnScrollListener(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                        hideLoadingView();
                    }
                    if (searchResults.size() == 0) {
                        emptyResult.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    } else {
                        emptyResult.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        search.findInBackground(searchCallback);
    }

    public class SearchResultAdapter extends BaseAdapter implements AbsListView.OnScrollListener {
        Context mContext = getActivity();
        int lastVisibleItemId;

        @Override
        public int getCount() {
            return searchResults.size();
        }

        @Override
        public Object getItem(int position) {
            return searchResults.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Duanzi item = (Duanzi) getItem(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView =
                        LayoutInflater.from(mContext).inflate(R.layout.search_item, null);
                holder = new ViewHolder();
                holder.title =
                        (TextView) convertView.findViewById(R.id.search_result_title);
                holder.description =
                        (TextView) convertView.findViewById(R.id.search_result_description);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(item.title);
            switch (item.category) {
                case Constants.CATEGORY_TXT:
                    holder.description.setText("段子");
                    break;
                case Constants.CATEGORY_IMAGE:
                    holder.description.setText("图片");
                    break;
                case Constants.CATEGORY_VIDEO:
                    holder.description.setText("视频");
                    break;
            }
            return convertView;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            lastVisibleItemId = firstVisibleItem + visibleItemCount;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (lastVisibleItemId >= searchResults.size()
                    && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                showLoadingView();
                search.findInBackground(searchCallback);
            } else {
                hideLoadingView();
            }
        }
    }

    public class ViewHolder {
        TextView title;
        TextView description;
    }

    public void showLoadingView() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    public void hideLoadingView() {
        if (loadingView != null) {
            loadingView.setVisibility(View.INVISIBLE);
        }
    }
}
