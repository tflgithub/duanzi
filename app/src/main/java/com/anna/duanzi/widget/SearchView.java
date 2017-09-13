package com.anna.duanzi.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.anna.duanzi.R;

/**
 * Created by tfl on 2017/8/31.
 */

public class SearchView extends LinearLayout implements TextWatcher, View.OnClickListener {
    /**
     * 输入框
     */
    private EditText et_search;
    /**
     * 输入框后面的那个清除按钮
     */
    private Button bt_clear;


    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        /**加载布局文件*/
        LayoutInflater.from(context).inflate(R.layout.search_view, this, true);
        /***找出控件*/
        et_search = (EditText) findViewById(R.id.et_search);
        bt_clear = (Button) findViewById(R.id.bt_clear);
        bt_clear.setVisibility(GONE);
        et_search.addTextChangedListener(this);
        bt_clear.setOnClickListener(this);
        et_search.requestFocus();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    public void setSearchContent(String str) {
        et_search.setText(str);
    }

    public String getSearchContent() {
        return et_search.getText().toString().trim();
    }


    public void setSearchEnableListener(SearchEnableListener searchEnableListener) {
        this.searchEnableListener = searchEnableListener;
    }

    public SearchEnableListener searchEnableListener;


    public interface SearchEnableListener {
        void enable();

        void disEnable();
    }

    /****
     * 对用户输入文字的监听
     *
     * @param editable
     */
    @Override
    public void afterTextChanged(Editable editable) {
        /**获取输入文字**/
        String input = et_search.getText().toString().trim();
        boolean isEnable;
        if (input.isEmpty()) {
            bt_clear.setVisibility(GONE);
            isEnable = false;
        } else {
            bt_clear.setVisibility(VISIBLE);
            isEnable = true;
            et_search.setSelection(input.length());
        }
        if (searchEnableListener != null) {
            if (isEnable) {
                searchEnableListener.enable();
            } else {
                searchEnableListener.disEnable();
            }
        }
    }

    @Override
    public void onClick(View view) {
        et_search.setText("");
    }
}
