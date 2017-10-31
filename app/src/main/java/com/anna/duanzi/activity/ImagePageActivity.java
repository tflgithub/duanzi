package com.anna.duanzi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.common.HttpHelper;
import com.anna.duanzi.domain.Comment;
import com.anna.duanzi.domain.Image;
import com.anna.duanzi.utils.UIUtils;
import com.anna.duanzi.widget.BadgeView;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ImagePageActivity extends BaseActivity implements View.OnClickListener {


    //viewpager
    private ViewPager view_pager;
    private TextView tv_title, tv_position;
    private String[] imageUrls;
    private String[] imageTitles;
    private List<ImageView> imageViewsList;
    //存储5个目录
    private int curIndex = 0;

    private String imageId;

    private ImageView comment_btn, digg_btn, iv_back;

    private TextView tv_comment, tv_digg_number_animation;

    private BadgeView commentBadgeView;

    private android.view.animation.Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_page);
        initView();
        initData();
    }

    String title = null;

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.header_actionbar_back);
        iv_back.setOnClickListener(this);
        title = getIntent().getExtras().getString("imageTitle");
        ((TextView) findViewById(R.id.header_actionbar_title)).setText(title);
        comment_btn = (ImageView) findViewById(R.id.comment_btn);
        digg_btn = (ImageView) findViewById(R.id.digg_btn);
        tv_comment = (TextView) findViewById(R.id.tv_comment);
        animation = AnimationUtils.loadAnimation(this, R.anim.applaud_animation);
        tv_digg_number_animation = (TextView) findViewById(R.id.tv_digg_number_animation);
        digg_btn.setOnClickListener(this);
        comment_btn.setOnClickListener(this);
        tv_comment.setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);
    }


    private void initData() {
        imageId = getIntent().getExtras().getString("imageId");
        HttpHelper.getInstance().clickStatistics(imageId);
        AVQuery<Image> query = AVObject.getQuery(Image.class);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.whereEqualTo("imageId", imageId);
        query.findInBackground(new FindCallback<Image>() {
            @Override
            public void done(List<Image> list, AVException e) {
                if (list != null && list.size() > 0) {
                    imageUrls = new String[list.size()];
                    imageTitles = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        Image image = list.get(i);
                        AVFile imageFile = image.getAVFile("imageFile");
                        imageUrls[i] = imageFile.getUrl();
                        imageTitles[i] = image.imageDec;
                    }
                }
                initViewPager();
            }
        });
        countComment();
        countDigg();
    }

    /**
     * 统计评论
     */
    private void countComment() {
        AVQuery<Comment> query = AVQuery.getQuery("Comment");
        query.whereEqualTo("commentId", imageId);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null) {
                    // 查询成功，输出计数
                    commentBadgeView = new BadgeView(ImagePageActivity.this, comment_btn);
                    commentBadgeView.setText(i + "");
                    commentBadgeView.setTextSize(8);
                    commentBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                    commentBadgeView.setAlpha(1f);
                    commentBadgeView.setBadgeMargin(0, 8);
                    commentBadgeView.show();
                } else {
                    // 查询失败
                }
            }
        });
    }

    int diggNum;

    /**
     * 统计点赞
     */
    private void countDigg() {
        AVQuery<AVObject> query = new AVQuery<>("Digg");
        query.whereEqualTo("diggId", imageId);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null) {
                    // 查询成功，输出计数
                    diggNum = i;
                    tv_digg_number_animation.setVisibility(View.VISIBLE);
                    tv_digg_number_animation.setText("+" + i);
                } else {
                    // 查询失败
                }
            }
        });
    }


    private void initViewPager() {
        imageViewsList = new ArrayList<>();
        // 热点个数与图片特殊相等
        if (imageUrls == null) {
            return;
        }
        for (int i = 0; i < imageUrls.length; i++) {
            ImageView iv = new ImageView(this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(params);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            imageViewsList.add(iv);
        }
        tv_position = (TextView) findViewById(R.id.tv_position);
        tv_title = (TextView) findViewById(R.id.tv_title);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        view_pager.setAdapter(new PicsAdapter());
        view_pager.setOnPageChangeListener(new MyPageChangeListener()); //设置页面切换监听器
        tv_title.setText(imageTitles[0]);
        tv_position.setText(curIndex + 1 + "/" + view_pager.getAdapter().getCount());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_comment:
                Intent intent = new Intent(ImagePageActivity.this, CommentActivity.class);
                intent.putExtra("commentId", imageId);
                intent.putExtra("isComment", true);
                startActivityForResult(intent, 1000);
                break;
            case R.id.digg_btn:
                digg();
                break;
            case R.id.comment_btn:
                Intent intent1 = new Intent(ImagePageActivity.this, CommentActivity.class);
                intent1.putExtra("commentId", imageId);
                startActivity(intent1);
                break;
            case R.id.header_actionbar_back:
                finish();
                break;
            case R.id.iv_share:
                UIUtils.shareImage(this,imageTitles[0],imageUrls[0]);
                break;
        }
    }


    /**
     * 点赞
     */
    private void digg() {
        tv_digg_number_animation.setVisibility(View.VISIBLE);
        tv_digg_number_animation.setText("+1");
        tv_digg_number_animation.startAnimation(animation);
        AVObject avObject = new AVObject("Digg");
        avObject.put("diggId", imageId);
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    diggNum++;
                    tv_digg_number_animation.setText("+" + diggNum);
                    digg_btn.setEnabled(false);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            int count = Integer.valueOf(commentBadgeView.getText().toString()) + 1;
            commentBadgeView.setText(count + "");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 定义ViewPager控件页面切换监听器
    class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            curIndex = position;
            tv_title.setText(imageTitles[position]);
            tv_position.setText(curIndex + 1 + "/" + view_pager.getAdapter().getCount());
        }

        //     boolean b = false;

        @Override
        public void onPageScrollStateChanged(int state) {
            //这段代码可不加，主要功能是实现切换到末尾后返回到第一张
//            switch (state) {
//                case 1:// 手势滑动
//                    b = false;
//                    break;
//                case 2:// 界面切换中
//                    b = true;
//                    break;
//                case 0:// 滑动结束，即切换完毕或者加载完毕
//                    // 当前为最后一张，此时从右向左滑，则切换到第一张
//                    if (view_pager.getCurrentItem() == view_pager.getAdapter()
//                            .getCount() - 1 && !b) {
//                        view_pager.setCurrentItem(0);
//                    }
//                    // 当前为第一张，此时从左向右滑，则切换到最后一张
//                    else if (view_pager.getCurrentItem() == 0 && !b) {
//                        view_pager.setCurrentItem(view_pager.getAdapter()
//                                .getCount() - 1);
//                    }
//                    break;
//
//                default:
//                    break;
//            }
        }
    }


    // 定义ViewPager控件适配器
    class PicsAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return imageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(imageViewsList.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return imageViewsList.indexOf(object);
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ImageView imageView = imageViewsList.get(position);
            Glide.with(ImagePageActivity.this).load(imageUrls[position]).placeholder(R.drawable.image_default_normal).into(imageView);
            ((ViewPager) container).addView(imageViewsList.get(position));
            return imageViewsList.get(position);
        }
    }
}
