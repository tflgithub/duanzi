package com.anna.duanzi.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;
import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.fragment.BackHandledFragment;
import com.anna.duanzi.fragment.BackHandledInterface;
import com.anna.duanzi.fragment.ImageTitleFragment;
import butterknife.ButterKnife;

public class ImageAreaActivity extends BaseActivity implements BackHandledInterface {

    private BackHandledFragment mBackHandedFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_area_acitvity);
        ButterKnife.bind(this);
        ((TextView) findViewById(R.id.header_actionbar_title)).setText(getString(R.string.image_area));
        ImageTitleFragment imageTitleFragment=new ImageTitleFragment();
        loadFragment(imageTitleFragment);
    }

    public void loadFragment(BackHandledFragment fragment) {
        BackHandledFragment backHandledFragment = fragment;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_container, backHandledFragment, "default");
        ft.addToBackStack("tag");
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    finish();
                }
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_actionbar_back:
                onBackPressed();
                break;
        }
    }
}
