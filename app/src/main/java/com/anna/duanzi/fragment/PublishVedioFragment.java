package com.anna.duanzi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.anna.duanzi.R;
import com.anna.duanzi.activity.PublishActivity;
import com.anna.duanzi.activity.RecorderActivity;
import com.anna.duanzi.activity.VideoThumbEditActivity;
import com.anna.duanzi.base.BaseFragment;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

public class PublishVedioFragment extends BaseFragment {

    private String videoUrl;
    public  String imageUrl;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private JCVideoPlayer jcVideoPlayer;
    public EditText editText;
    private Button button;

    @Override
    public View initView() {
        View view = View.inflate(getContext(), R.layout.fragment_publish_vedio, null);
        jcVideoPlayer = (JCVideoPlayer) view.findViewById(R.id.video_controller);
        editText = (EditText) view.findViewById(R.id.et_title);
        button = (Button) view.findViewById(R.id.edit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VideoThumbEditActivity.class);
                intent.putExtra("videoPath", videoUrl);
                startActivityForResult(intent, PublishActivity.EDIT_VIDEO_REQUEST_CODE);
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoUrl = getArguments().getString(ARG_PARAM1);
            imageUrl = getArguments().getString(ARG_PARAM2);
        }
    }

    public static PublishVedioFragment newInstance(String param1, String param2) {
        PublishVedioFragment fragment = new PublishVedioFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, param1);
        bundle.putString(ARG_PARAM2, param2);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initData() {
        jcVideoPlayer.setUp(videoUrl, "file://" + imageUrl, "", false);
    }

    public void stop() {
        Intent intent = new Intent(getActivity(), RecorderActivity.class);
        startActivity(intent);
    }
}
