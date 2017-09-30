package com.anna.duanzi.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseFragment;
import com.anna.duanzi.utils.ImageUtil;
import com.anna.duanzi.widget.DragLinearView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


public class PublishImageFragment extends BaseFragment {
    DragLinearView dragLinearView;
    private static final String ARG_PARAM = "param";
    public ArrayList<String> param;
    LinearLayout desc_layout;

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_publish_image, null);
        dragLinearView = (DragLinearView) view.findViewById(R.id.dragView);
        desc_layout = (LinearLayout) view.findViewById(R.id.desc_layout);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param = getArguments().getStringArrayList(ARG_PARAM);
        }
    }

    public static PublishImageFragment newInstance(ArrayList<String> param) {
        PublishImageFragment fragment = new PublishImageFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ARG_PARAM, param);
        fragment.setArguments(bundle);
        return fragment;
    }

    private ArrayList<String> delList = new ArrayList<>();

    @Override
    public void initData() {
        dragLinearView.removeAllItemView();
        //设置最大行数
        dragLinearView.setMaxRows(15);
        //设置一行的个数
        dragLinearView.setMaxRowsItemCount(4);

        //添加元素（图片）
        dragLinearView.addMutilItemView(imageTagElements());

        //是否禁用拖拽
        dragLinearView.setDisableDrag(false);

        //是否可删除
        dragLinearView.setShowDelBtn(true);

        dragLinearView.setOnAddClickListener(new DragLinearView.OnAddClickListener() {
            @Override
            public void onAddClick() {
                Intent intent = new Intent();
                intent.putExtra("imageList", delList);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
        dragLinearView.setOnDelViewListener(new DragLinearView.OnDelViewListener() {
            @Override
            public void onDel(String tag) {
                delList.add(tag);
                desc_layout.removeView(viewMap.get(tag));
            }
        });
    }

    public HashMap<String, View> viewMap = new HashMap<>();

    public LinkedList<DragLinearView.ImageTagElement> imageTagElements() {
        LinkedList<DragLinearView.ImageTagElement> list = new LinkedList<>();
        for (int i = 0; i < param.size(); i++) {
            Bitmap bitmap = ImageUtil.decodeBitmap(param.get(i), 200, 200, 100, 100);
            list.add(new DragLinearView.ImageTagElement(bitmap, param.get(i)));
            View view = View.inflate(getContext(), R.layout.imge_desc_edit, null);
            int num = i + 1;
            ((EditText) view.findViewById(R.id.image_desc)).setHint("编辑图" + num + "描述(限50字以内)");
            if (i == 0) {
                view.setFocusable(true);
            }
            viewMap.put(param.get(i), view);
            desc_layout.addView(view);
        }
        return list;
    }
}
