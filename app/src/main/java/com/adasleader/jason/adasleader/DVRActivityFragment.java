package com.adasleader.jason.adasleader;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hk.com.mobileye.jason.adasleader.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class DVRActivityFragment extends Fragment {

    public DVRActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dvr, container, false);
    }
}
