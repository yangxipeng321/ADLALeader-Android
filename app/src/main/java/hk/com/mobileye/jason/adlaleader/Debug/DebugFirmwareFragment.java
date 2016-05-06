package hk.com.mobileye.jason.adlaleader.Debug;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hk.com.mobileye.jason.adlaleader.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DebugFirmwareFragment extends Fragment {


    public DebugFirmwareFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_debug_firmware, container, false);
    }

}
