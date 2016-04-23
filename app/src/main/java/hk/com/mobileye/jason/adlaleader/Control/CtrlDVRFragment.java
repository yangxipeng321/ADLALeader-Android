package hk.com.mobileye.jason.adlaleader.Control;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hk.com.mobileye.jason.adlaleader.R;
import hk.com.mobileye.jason.adlaleader.common.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class CtrlDVRFragment extends Fragment {


    public CtrlDVRFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ctrl_dvr, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BtnClickListener listener = new BtnClickListener();
        view.findViewById(R.id.btnDVRUp).setOnClickListener(listener);
        view.findViewById(R.id.btnDVRDown).setOnClickListener(listener);
        view.findViewById(R.id.btnDVRConfirm).setOnClickListener(listener);
        view.findViewById(R.id.btnDVRCancel).setOnClickListener(listener);
        view.findViewById(R.id.btnDVRHome).setOnClickListener(listener);
    }

    private class BtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            byte key;
            String desc = v.getContentDescription().toString();
            if (desc.equals(getString(R.string.dvr_up))) {
                key = 1;
            } else if (desc.equals(getString(R.string.dvr_down))) {
                key = 2;
            } else if (desc.equals(getString(R.string.dvr_confirm))) {
                key = 3;
            } else if (desc.equals(getString(R.string.dvr_cancel))) {
                key = 4;
            } else if (desc.equals(getString(R.string.dvr_home))) {
                key = 5;
            } else if (desc.equals(getString(R.string.dvr_photo))) {
                key = 6;
            } else {
                return;
            }

            Intent intent = new Intent(Constants.DVR_KEY_ACTION);
            intent.putExtra(Constants.EXTEND_DVR_KEY, key);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }
    }

}
