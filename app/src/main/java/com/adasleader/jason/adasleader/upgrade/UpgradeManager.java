package com.adasleader.jason.adasleader.upgrade;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adasleader.jason.adasleader.R;
import com.adasleader.jason.adasleader.SettingsActivity;
import com.adasleader.jason.adasleader.common.Constants;

import java.io.File;


/**
 * Created by Jason on 2015/2/5.
 *
 *
 */
public class UpgradeManager {
    private static final String TAG = "UpgradeManager";
    public static final int MSG_GET_URL = 1;
    public static final int MSG_PROGRESS = 2;
    public static final int MSG_DOWNLOAD = 3;
    private Context mContext;
    private String mUrlStr = null;
    private String mFileName = null;

    private AlertDialog checkUpgradeDialog = null;
    private AlertDialog downloadDialog = null;
    private UploadDialogFragment uploadDialog = null;
    private ProgressDialog progressDialog = null;
    private ProgressBar mProgress = null;

    final TaskUpgradeMethods mUpgradeTask;

    /**
     * An interface that defines methods that UpgradeTask implements.
     */

    public interface TaskUpgradeMethods {
        void downloadFile(String aUrlStr, String aFileName);
    }


    public UpgradeManager(Context aContext, TaskUpgradeMethods aTask) {
        mContext = aContext;
        mUpgradeTask = aTask;
    }

    private int count = 0;
    public void showCheckUpgradeDialog() {
        if (null == checkUpgradeDialog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(mContext.getString(R.string.checking_upgrade));
            checkUpgradeDialog = builder.create();
            checkUpgradeDialog.show();
            count = 1;
        } else {
            count++;
            if (!checkUpgradeDialog.isShowing())
                checkUpgradeDialog.show();
        }
    }

    public void closeCheckUpgradeDialog() {
        if (checkUpgradeDialog!=null) {
            count--;
            if (count <= 0) {
                checkUpgradeDialog.dismiss();
                checkUpgradeDialog = null;
            }
        }
    }

    public void showDownloadDialog() {
        Log.d(TAG, "showDownloadDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.find_upgrade));
        builder.setPositiveButton(R.string.download_upgrade, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (null != mUrlStr)
                    downloadUpgrade(mUrlStr);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        downloadDialog = builder.create();
        downloadDialog.show();
    }

    public void downloadProgress(int aProgress) {
        Log.d(TAG, "updateProgress : " + aProgress);
        //mProgress.setProgress(aProgress);
        if (progressDialog!=null) {
            progressDialog.setProgress(aProgress);
            if (!progressDialog.isShowing())
                progressDialog.show();
        }
    }

    public void downloadFinish() {
        progressDialog.dismiss();
        if (mFileName.endsWith(".apk"))
            installApk();
    }

    public void downloadFail() {
        progressDialog.dismiss();
        Toast.makeText(mContext, R.string.download_fail, Toast.LENGTH_LONG).show();
    }

    private void downloadUpgrade(String urlStr) {
        String fileName = mUrlStr.substring(mUrlStr.lastIndexOf("/") + 1);
        mFileName = getExternalStorageFilePath(fileName);
        Log.d(TAG, "downloadUpgreade fileName : " + fileName + "  mfileName : " + mFileName);
        showProgressDialog();
        mUpgradeTask.downloadFile(urlStr, mFileName);
    }

    private void showProgressDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setTitle(mContext.getString(R.string.downloading_upgrade));
//
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        View v = inflater.inflate(R.layout.progress, null);
//        mProgress = (ProgressBar) v.findViewById(R.id.progressBar);
//        mProgress.setProgress(0);
//        builder.setView(v);
//
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        progressDialog = builder.create();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void installApk() {
        Log.d(TAG, "installApk");
        File apkFile = new File(mFileName);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkFile.toString()),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public boolean isCancel;
    public void showUploadDialog() {
//        if (null == uploadDialog) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//            builder.setMessage(mContext.getString(R.string.uploading_firmware));
//            uploadDialog = builder.create();
//            uploadDialog.show();
//        }
        if (null == uploadDialog) {
            uploadDialog = new UploadDialogFragment();
        }
        isCancel = false;
        uploadDialog.show(((SettingsActivity)mContext).getFragmentManager(), "Upload");
    }

    public void closeUploadDialog() {
//        if (uploadDialog != null) {
//            uploadDialog.dismiss();
//            uploadDialog = null;
//        }
        if (null != uploadDialog) {
            uploadDialog.dismiss();
            uploadDialog = null;
        }
    }

    public void setUploadDialogContent(String content) {
        Log.d(TAG, "setUploadDialogContent : " + content);
        if (null != uploadDialog) {
            Log.d(TAG, "uploadDislog isAdded is " + uploadDialog.isAdded());
            uploadDialog.setMessage(content);
        }
    }

    public void startCountDown() {
        Log.d(TAG, "startCountDown");
        if (null != uploadDialog ) {
            Log.d(TAG, "uploadDislog isAdded is " + uploadDialog.isAdded());
            if (uploadDialog.isAdded())
                uploadDialog.startCountDown();
        }
    }


    public void setUrl(String aUrl) { mUrlStr = aUrl;  }
    public String getUrl() { return  mUrlStr; }
    public String getFileName() {return mFileName;}

    public static String getExternalStorageFilePath(String fileName) {
        File path = Environment.getExternalStoragePublicDirectory(Constants.APP_DIR);
        if (!path.mkdirs()) {
            Log.e(TAG, path + " not created");
        }
        File file = new File(path, fileName);
        Log.d(TAG, file.toString());
        return file.toString();
    }
}
