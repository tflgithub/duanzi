package com.anna.duanzi.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.common.Contants;
import com.anna.duanzi.utils.StringUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UpdateNickFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UpdateNickFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateNickFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EditText editText;

    private Button btn_ok;

    public UpdateNickFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateNickFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateNickFragment newInstance(int param1, String param2) {
        UpdateNickFragment fragment = new UpdateNickFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_nick, container, false);
        btn_ok = (Button) view.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        editText = (EditText) view.findViewById(R.id.et_update_content);
        editText.setText(mParam2);
        editText.setSelection(mParam2.length());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (mParam1) {
                    case Contants.UPDATE_USERINFO.NICK_NAME:
                        if (s.length() > 5) {
                            btn_ok.setEnabled(true);
                        } else {
                            btn_ok.setEnabled(false);
                        }
                        break;
                    case Contants.UPDATE_USERINFO.EMAIL:
                        if (StringUtils.isEmail(s.toString())) {
                            btn_ok.setEnabled(true);
                        } else {
                            btn_ok.setEnabled(false);
                        }
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                actionUpdate();
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void actionUpdate() {
        switch (mParam1) {
            case Contants.UPDATE_USERINFO.NICK_NAME:
                AVUser.getCurrentUser().put("nickName", editText.getText().toString());
                break;
            case Contants.UPDATE_USERINFO.EMAIL:
                AVUser.getCurrentUser().put("email", editText.getText().toString());
                break;
        }
        AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Intent data = new Intent();
                    data.putExtra("update_content", editText.getText().toString());
                    getActivity().setResult(getActivity().RESULT_OK, data);
                    getActivity().finish();
                }
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
