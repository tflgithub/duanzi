package com.anna.duanzi.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.zxing.QRcodeUtils;
import com.google.zxing.Result;

public class PayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ((TextView) findViewById(R.id.header_actionbar_title)).setText("支付");
        final ImageView imageView = (ImageView) findViewById(R.id.iv_qr_code);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wx_pay);
                Result result = QRcodeUtils.parseQRcodeBitmap(bitmap);
                Log.d("PayActivity", result.toString());
                Intent intent = new Intent(PayActivity.this, WebTxtActivity.class);
                intent.putExtra("url", result.toString());
                startActivity(intent);
                return false;
            }
        });
    }


    public void onClick(View view) {
        finish();
    }
}
