package com.anna.duanzi.fragment;

import android.os.Bundle;
import android.view.View;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseFragment;
import com.anna.duanzi.database.RecordsDao;
import com.anna.duanzi.widget.SearchView;
import com.anna.duanzi.widget.TagView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchDefaultFragment extends BaseFragment {

    @Bind(R.id.tv_fast)
    TagView tagViewFast;
    @Bind(R.id.tv_history)
    TagView tagViewHistory;
    @Bind(R.id.tv_guess)
    TagView tagViewGuess;
    SearchView searchView;

    public SearchDefaultFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_search_default, null);
        searchView = (SearchView) getActivity().findViewById(R.id.search_view);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        tagViewFast.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, TagView.Tag tag) {
                searchView.setSearchContent(tag.text);
                getActivity().findViewById(R.id.search_btn).performClick();
            }
        });
        String[] fastTags = getResources().getStringArray(R.array.query_hot);
        tagViewFast.addTags(fastTags);
        String[] guessTags = getResources().getStringArray(R.array.query_guess);
        for (String guessTag : guessTags) {
            addGuessTag(guessTag);
        }
        List<String> history = RecordsDao.getInstance(getActivity()).getRecordsList();
        for (String historyTag : history) {
            addHistoryTag(historyTag);
        }
        tagViewGuess.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, TagView.Tag tag) {
                searchView.setSearchContent(tag.text);
                getActivity().findViewById(R.id.search_btn).performClick();
            }
        });
        tagViewHistory.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, TagView.Tag tag) {
                searchView.setSearchContent(tag.text);
                getActivity().findViewById(R.id.search_btn).performClick();
            }
        });

        tagViewHistory.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(int position, TagView.Tag tag) {
                RecordsDao.getInstance(getActivity()).deleteRecords(tag.text);
            }
        });
    }

    private void addHistoryTag(String tagStr) {
        TagView.Tag tag = new TagView.Tag(tagStr, getResources().getColor(R.color.gray), true);
        tagViewHistory.addTag(tag);
    }

    private void addGuessTag(String tagStr) {
        TagView.Tag tag = new TagView.Tag(tagStr, getResources().getColor(R.color.green), false);
        tagViewGuess.addTag(tag);
    }
}
