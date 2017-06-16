package me.nereo.multi_image_selector.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.camera.utils.BitmapUtils;
import me.nereo.multi_image_selector.utils.CropPhotoUtil;

/**
 * 照片处理
 * Created by tfl on 2015/8/3.
 */
public class PhotoProcessActivity extends Activity
        implements View.OnClickListener {
    private ImageView photoImageView;
    private TextView actionTextView,cancelBtn;
    private String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.photo_process_activity);
        initView();
        initData();

    }

    private void initData() {
        Bitmap bitmap = null;
        try {
            path = getIntent().getStringExtra(AppConstant.KEY.IMG_PATH);
            bitmap = getImage(path);
            refreshGallery(path);
            //bitmap = loadBitmap(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        photoImageView.setImageBitmap(bitmap);
        cancelBtn.setOnClickListener(this);
        actionTextView.setOnClickListener(this);
    }

    private void initView() {
        photoImageView = (ImageView) findViewById(R.id.photo_imageview);
        cancelBtn = (TextView) findViewById(R.id.btn_cancel);
        actionTextView = (TextView) findViewById(R.id.bt_ok);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 从给定路径加载图片
     */
    public static Bitmap loadBitmap(String imgpath) {
        return BitmapFactory.decodeFile(imgpath);
    }


    private Bitmap getImage(String srcPath) throws OutOfMemoryError {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是1920*1080分辨率，所以高和宽我们设置为
        float hh = 1920f;//这里设置高度为1920f
        float ww = 1080f;//这里设置宽度为1080f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;//压缩好比例大小后再进行质量压缩
    }

    private void refreshGallery(String file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(new File(file)));
        sendBroadcast(mediaScanIntent);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cancel) {
            Intent intent = new Intent();
            intent.putExtra(CameraActivity.CAMERA_RETURN_PATH, path);
            setResult(0, intent);
            finish();
        } else if (id == R.id.bt_ok) {
            gotoClipActivity(Uri.fromFile(new File(path)));
        }
    }

    /**
     * 打开截图界面
     *
     * @param uri
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, ClipImageActivity.class);
        intent.putExtra("type", AppConstant.CLIP_TYPE.FOUND);
        intent.setData(uri);
        startActivityForResult(intent, AppConstant.REQUEST_CODE.CROP_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == AppConstant.REQUEST_CODE.CROP_PHOTO) {
            setResult(RESULT_OK, data);
            finish();
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Intent intent = new Intent();
            intent.putExtra(CameraActivity.CAMERA_RETURN_PATH, path);
            setResult(0, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
