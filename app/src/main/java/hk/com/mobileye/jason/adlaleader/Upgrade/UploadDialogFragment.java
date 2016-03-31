package hk.com.mobileye.jason.adlaleader.Upgrade;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import hk.com.mobileye.jason.adlaleader.R;
import hk.com.mobileye.jason.adlaleader.common.Constants;

/**
 * Created by Jason on 2015/3/4.
 */
public class UploadDialogFragment extends DialogFragment {

    private static final String TAG = "UploadDialogFragment";
    private ProgressDialog mDialog;
    private CountDownTimer mTimer;

    /** The activity that creates an instance of this dialog fragment must
     *  implement this interface in order to receive event callbacks.
     *  Each method passes the DialogFragment in case the host needs to
     *  query it.
     */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialognegativeClick(DialogFragment dialog);
        public void onDialogCancel(DialogFragment dialogFragment);
    }

    //Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Verify that the host activity implements the callback interface
        try {
            //Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            //The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(R.string.upload_title);
//        builder.setMessage(R.string.uploading_firmware);
//        builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                mListener.onDialogPositiveClick(UploadDialogFragment.this);
//            }
//        });
//        setCancelable(false);
//        mDialog = builder.create();
        mDialog = new ProgressDialog(getActivity());
        //mDialog.setTitle(R.string.upload_title);
        mDialog.setMessage(getString(R.string.uploading_firmware));
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setIndeterminate(true);
        mDialog.setMax(0);
        mDialog.setProgress(0);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        return mDialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(TAG, "onCancle");
        if (mTimer!=null) {
            mTimer.cancel();
        }
        mListener.onDialogCancel(UploadDialogFragment.this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(TAG, "onDismiss");
    }

    public void setMessage(String msg) {
        Log.d(TAG, "setMessage : " + msg);
        if (mDialog != null)
            mDialog.setMessage(msg);
    }

    public void startCountDown() {
          mTimer = new CountDownTimer(Constants.COUNTDOWN, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //setPosButtonEnable(false);
                setMessage(String.format("%s(%d)",  getString(R.string.waiting_device_restart),
                        millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                if (null!=mDialog) {mDialog.dismiss();}
                Toast.makeText(getActivity().getApplicationContext(),
                        R.string.restart_complete, Toast.LENGTH_LONG).show();
                //setMessage(getString(R.string.restart_complete));
            }
        };
        mTimer.start();
    }



}
