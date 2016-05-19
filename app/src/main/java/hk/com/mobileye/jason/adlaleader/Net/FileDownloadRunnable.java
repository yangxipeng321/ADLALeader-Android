package hk.com.mobileye.jason.adlaleader.net;

/**
 * Created by Jason on 2015/2/3.
 * This task downloads file from ta resource addressed by a URL.
 */
public class FileDownloadRunnable implements Runnable{
    private static final String TAG = "FileDownloadRunnable";
    private static final int READ_SIZE = 1024*64;

    @Override
    public void run() {

        try {
            //Before continuing, checks to see that the Thread hasn't been interrupted.
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }



        } catch (InterruptedException e) {
            //Does nothing

        //In all cases, handle the results
        }finally {


        }
    }
}
