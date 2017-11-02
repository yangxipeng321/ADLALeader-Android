package com.adasleader.jason.adasleader.control;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adasleader.jason.adasleader.R;


public class CtrlCarFragment extends Fragment {


    public CtrlCarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ctrl_car, container, false);
    }


}
