package hk.com.mobileye.jason.adlaleader.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;

/**
 * Created by Jason on 2015/1/14.
 *
 * This task downloads bytes from CAN. When the task has
 * finished, it calls handleState to report its results.
 *
 */
public class ConfigDownloadRunnalbe implements Runnable {

    private static final String TAG = "ConfigDownloadRunnable";

    //Constants for indicating the state of the download
    static final int TCP_STATE_FAILED = -1;
    static final int TCP_STATE_STARTED = 0;
    static final int TCP_STATE_COMPLETED = 1;

    //Defines a field taht contains the calling object of type FileTask.
    final TaskRunnableDownloadMethods mFileTask;

    /**
     * An interface that defines methods that caller implements.
     */
    interface TaskRunnableDownloadMethods {
        /**
         * Sets the Thread that this instance is running on
         * @param currentThread the current Thread
         */
        void setDownloadThread(Thread currentThread);

        /**
         * Returns the current contents of the download buffer
         * @return The byte array downloaded from the URL in the last read
         */
        byte[] getByteBuffer();

        /**
         * Sets the current contents of the download buffer
         * @param buffer The bytes that were just read
         */
        void setByteBuffer(byte[] buffer);

        /**
         * Defines the actions for each state of the PhotoTask instance.
         * @param state The current state of the task
         */
        void handleDownloadState(int state);

        /**
         * Get the Name for the file being downloaded
         * @return The file name
         */
        String getFileName();

        /**
         * Get the ip of the server
         * @return The server ip.
         */
        String getIP();

        /**
         * Get the port of the server
         * @return The server tcp port
         */
        int getPort();
    }

    /**
     * This constructor creates an instance of PhotoDownloadRunnable and stores in it a reference
     * to the PhotoTask instance that instantiated it.
     *
     * @param fileTask The PhotoTask, which implements TaskRunnableDecodeMethods
     */
    ConfigDownloadRunnalbe(TaskRunnableDownloadMethods fileTask) {
        mFileTask = fileTask;
    }


    /**
     * Defines this object's task, which is a set of instructions designed to be run on a Thread.
     */
    @Override
    public void run() {
        /*
         * Stores the current Thread in the the PhotoTask instance, so that the instance
         * can interrupt the Thread.
         */
        mFileTask.setDownloadThread(Thread.currentThread());

        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        /*
         * Gets the image cache buffer object from the PhotoTask instance. This makes the
         * to both PhotoDownloadRunnable and PhotoTask.
         */
        byte[] byteBuffer = mFileTask.getByteBuffer();

        String ip = mFileTask.getIP();
        int port = mFileTask.getPort();
        String fileName = mFileTask.getFileName();

        /*
         * A try block that downloads a config file from the CAN. The URL value is in the field
         * PhotoTask.mImageURL
         */
        // Tries to download config file from CAN
        try {
            //Before continuing, checks to see that the Thread hasn't been interrupted.
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            if (null == byteBuffer && ip!=null && fileName!=null) {

                /*
                 *Calls the FileTask implementation of {@link #handleDownloadState} to
                 * set the state of the download
                 */
                mFileTask.handleDownloadState(TCP_STATE_STARTED);

                //Defines a handle for the byte receive stream
                InputStream bytesStream = null;

                Socket socket = null;
                //connect to server and get config file
                try {
                    //connect to server
                    socket = new Socket(ip,port);
                    OutputStream outputStream = socket.getOutputStream();
//                    PrintWriter output = new PrintWriter(out, true);
//                    output.println("hello from android");
                    outputStream.write(getReadPack(fileName));

//                    BufferedReader reader = new BufferedReader(
//                            new InputStreamReader(socket.getInputStream()));
                    bytesStream = socket.getInputStream();
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }

                    //Gets the size of the bytes received. This may or may not be returned.
                    int contentSize = socket.getReceiveBufferSize();
                    if (contentSize > 0) {
                        byteBuffer = new byte[contentSize];

                        int remainingLenght = contentSize;
                        int bufferOffset = 0;

                        /**
                         * Read into the buffer until the number of bytes
                         * equal to the length of the buffer (the size of
                         * the image) have been read.
                         */
                        while (remainingLenght > 0) {
                            int readResult = bytesStream.read(
                                    byteBuffer,
                                    bufferOffset,
                                    remainingLenght);
                            if (readResult < 0) {
                                throw new EOFException();
                            }

                            bufferOffset += readResult;
                            remainingLenght -= readResult;

                            if (Thread.interrupted()) {
                                throw new InterruptedException();
                            }
                        }
                    }

                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;

                    // If the input stream si still open, close it
                }finally {
                    if (null != socket) {
                        try {
                            socket.close();
                            socket =null;
                        } catch (IOException e) {

                        }
                    }

                    if (null != bytesStream) {
                        try {
                            bytesStream.close();
                        } catch (Exception e) {

                        }
                    }
                }
            }

            /*
             * Stores the download bytes in the byte buffer in the FileTask instance.
             */
            mFileTask.setByteBuffer(byteBuffer);

            /*
             * Sets the status message int theFileTask instance. This sets the
             * SettingsActivity to show the configuration.
             */
            mFileTask.handleDownloadState(TCP_STATE_COMPLETED);

        //Catches exceptions thrown in response to a queued interrupt
        } catch (InterruptedException e) {
            //Does nothing

        //In all cases, handle the results.
        } finally {
            // If the byteBuffer is null, reports that the download failed.
            if (null == byteBuffer) {
                mFileTask.handleDownloadState(TCP_STATE_FAILED);
            }

            /*
             * The implementation of setHTTPDownloadThread() in FileTask calls
             * FileTask.setCurrentThread(), which then locks on the static ThreadPool
             * object and returns the current thread. Locking keeps all references to Thread
             * objects the same until the reference to the current Thread is deleted.
             */

            //Sets the reference to the current Thread to null, release tis storage
            mFileTask.setDownloadThread(null);

            //Clears the Thread's interrupt flag
            Thread.interrupted();
        }
    }

    private byte[] getReadPack(String fileName) {
        int len = MsgConst.MSG_LEN_HEADER + 4 + fileName.length();
        byte[] buffer = new byte[len];

        int index = 0;
        System.arraycopy(MsgUtils.int2Bytes(MsgConst.MSG_FLAG), 0, buffer, index, 4);
        index += 4;

        //length
        buffer[index++] = (byte) (len & 0xff);
        buffer[index++] = (byte)((len>>8) & 0xff);

        //reserve
        buffer[index++] = 0;

        //response type
        buffer[index++] = (byte) (ResponseType.REQUEST & 0xff);

        //msg seq
        buffer[index++] = 0;
        buffer[index++] = 0;

        //service type
        buffer[index++] = (byte) (ServiceType.SERVICE_FILE & 0xff);

        //message type
        buffer[index++] = (byte) (MessageType.FILE_READ_REQ & 0xff);

        //TLV
        byte[] value = fileName.getBytes();
        byte[] tlv = new byte[4 + value.length];
        tlv[0] = (byte) (TLVType.TP_FILE_NAME_ID & 0xff);
        tlv[1] = (byte) ((TLVType.TP_FILE_NAME_ID >>> 8) & 0xff);
        tlv[2] = (byte) (tlv.length & 0xff);
        tlv[3] = (byte) ((tlv.length >>> 8) & 0xff);
        System.arraycopy(value, 0, tlv, 4, value.length);
        System.arraycopy(tlv, 0, buffer, MsgConst.HEART_DEAT_INTERVAL, tlv.length);

        //CRC
        int CRC = MsgUtils.getCrc32(tlv, tlv.length);
        System.arraycopy(MsgUtils.int2Bytes(CRC), 0, buffer, 12, 4);
        return buffer;
    }

    private void decode(byte[] buffer) {

    }
}
