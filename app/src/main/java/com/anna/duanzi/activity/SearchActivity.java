package com.anna.duanzi.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseFragment;
import com.anna.duanzi.fragment.SearchDefaultFragment;
import com.anna.duanzi.fragment.SearchResultFragment;
import com.anna.duanzi.widget.SearchView;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private TextView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView = (SearchView) findViewById(R.id.search_view);
        search = (TextView) findViewById(R.id.search_btn);
        searchView.setSearchEnableListener(new SearchView.SearchEnableListener() {
            @Override
            public void enable() {
                search.setEnabled(true);
            }

            @Override
            public void disEnable() {
                search.setEnabled(false);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(SearchResultFragment.newInstance(searchView.getSearchContent()));
            }
        });
        loadFragment(new SearchDefaultFragment());
    }

    private void loadFragment(BaseFragment fragment) {
        BaseFragment baseFragment = fragment;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, baseFragment);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_actionbar_back:
                onBackPressed();
                break;
        }
    }
}
