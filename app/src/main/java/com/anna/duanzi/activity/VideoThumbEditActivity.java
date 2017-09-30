package com.anna.duanzi.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;
import com.anna.duanzi.R;
import com.anna.duanzi.utils.ImageUtil;
import java.util.List;
import cn.tfl.mediarecord.util.ExtractVideoInfoUtil;
import cn.tfl.mediarecord.util.Util;

public class VideoThumbEditActivity extends AppCompatActivity implements ViewSwitcher.ViewFactory {

    private ImageSwitcher mImageSwitcher;
    private LinearLayout mLinearLayout;
    private int current = 0;//当前图片的下标
    ImageView[] images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_thumb_edit);
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String captureBitmapPath = Util.createImagePath(VideoThumbEditActivity.this);
                String imageUrl = ImageUtil.saveImageToSD(bitmapList.get(current), captureBitmapPath);
                Intent intent = new Intent();
                intent.putExtra("imageUrl", imageUrl);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        mLinearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        mImageSwitcher = (ImageSwitcher) findViewById(R.id.image_switcher);
        mImageSwitcher.setInAnimation(this, android.R.anim.fade_in);
        mImageSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        init();
    }

    List<Bitmap> bitmapList;

    private void init() {
        bitmapList = ExtractVideoInfoUtil.getVideoThumbnailsInfoForEdit(getIntent().getStringExtra("videoPath"), 10);
        mImageSwitcher.setFactory(this);
        //设置ImageView[] 中的ImageView的属性
        images = new ImageView[bitmapList.size()];
        for (int i = 0; i < images.length; i++) {
            images[i] = new ImageView(VideoThumbEditActivity.this);
            images[i].setImageBitmap(bitmapList.get(i));
            images[i].setId(i);//标记，在ImageClick()中用到
            images[i].setLayoutParams(new ViewGroup.LayoutParams(200, 200));
            images[i].setPadding(0, 2, 2, 2);
            images[i].setImageAlpha(100);//数值越小透明度越高，imags[i].setImageAlpha(255)是在api16的时候新出的
            mLinearLayout.addView(images[i]);
            images[i].setOnClickListener(new ImageClick());
            images[0].setImageAlpha(255);
        }
        mImageSwitcher.setImageDrawable(new BitmapDrawable(bitmapList.get(current)));
    }


    @Override
    public View makeView() {
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundColor(0xFF000000);
        //设置填充方式
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        return imageView;
    }

    /**
     * 点击HorizontalScrollView中的图片
     */
    public class ImageClick implements View.OnClickListener {
        public void onClick(View v) {
            ImageView img = (ImageView) v;
            current = img.getId();
            setAlpha(images);
            img.setImageAlpha(255);
            mImageSwitcher.setImageDrawable(new BitmapDrawable(bitmapList.get(current)));
        }
    }

    /**
     * 把其他图片设置上遮罩，只留当前图片为高亮
     *
     * @param images 存放ImageView的数组
     */
    private void setAlpha(ImageView[] images) {
        for (int i = 0; i < images.length; i++) {
            images[i].setImageAlpha(100);
        }
    }
}
