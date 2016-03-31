package hk.com.mobileye.jason.adlaleader.Net;

import hk.com.mobileye.jason.adlaleader.Net.ConfigDownloadRunnalbe.TaskRunnableDownloadMethods;

/**
 * Created by Jason on 2015/1/14.
 */
public class FileTask implements TaskRunnableDownloadMethods{


    byte[] mFileBuffer;

    @Override
    public void setDownloadThread(Thread currentThread) {
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public String getIP() {
        return null;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public void handleDownloadState(int state) {
        int outState;
        switch (state) {
            case ConfigDownloadRunnalbe.TCP_STATE_COMPLETED:
                outState = 1;
                break;
            case ConfigDownloadRunnalbe.TCP_STATE_FAILED:
                outState = -1;
                break;
            default:
                outState = 0;
                break;
        }
        handleState(outState);
    }


    void handleState(int state) {

    }

    @Override
    public void setByteBuffer(byte[] buffer) {
        mFileBuffer = buffer;
    }

    @Override
    public byte[] getByteBuffer() {
        return mFileBuffer;
    }
}
