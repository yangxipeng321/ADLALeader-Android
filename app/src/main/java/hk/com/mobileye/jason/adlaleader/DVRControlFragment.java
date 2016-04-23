package hk.com.mobileye.jason.adlaleader;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hk.com.mobileye.jason.adlaleader.Control.CtrlInteractionListener;


public class DVRControlFragment extends Fragment implements View.OnClickListener {

    private CtrlInteractionListener mListener;

    public DVRControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dvrcontrol, container, false);

        view.findViewById(R.id.btnDVRHome).setOnClickListener(this);
        view.findViewById(R.id.btnDVRConfirm).setOnClickListener(this);
        view.findViewById(R.id.btnDVRCancel).setOnClickListener(this);
        view.findViewById(R.id.btnDVRUp).setOnClickListener(this);
        view.findViewById(R.id.btnDVRDown).setOnClickListener(this);
        view.findViewById(R.id.btnDVRPhoto).setOnClickListener(this);
        view.findViewById(R.id.btnScrCar).setOnClickListener(this);
        view.findViewById(R.id.btnScrMobileye).setOnClickListener(this);
        view.findViewById(R.id.btnScrDVR).setOnClickListener(this);
        view.findViewById(R.id.btnScrVideo).setOnClickListener(this);
        view.findViewById(R.id.btnPlayVideo).setOnClickListener(this);
        return view;
    }

    public void onClick(View view) {
        if (mListener != null) {
            mListener.onFragmentInteraction(view);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (CtrlInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(View v);
//    }

}
