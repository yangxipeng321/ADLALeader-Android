package hk.com.mobileye.jason.adlaleader.net;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Locale;

import hk.com.mobileye.jason.adlaleader.common.Constants;
import hk.com.mobileye.jason.adlaleader.common.MyApplication;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.net.Message.ServiceType;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class TcpIntentService extends IntentService {
    private static final String TAG = "TcpIntentService";

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FILE_SERVICE = "hk.com.mobileye.jason.adlaleader.Net.action.FileService";

    private static final String EXTRA_MESSAGE_PACK = "hk.com.mobileye.jason.adlaleader.Net.extra.MessagePack";
    private static final String EXTRA_SENDER = "hk.com.mobileye.jason.adlaleader.Net.extra.SENDER";
    private static final String EXTRA_DECRIPTION = "hk.com.mobileye.jason.adlaleader.Net.extra.DESCRIPTION";

    private LocalBroadcastManager mBroadcaster;

    /**
     * Starts this service to perform action File Service with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFileService(Context context, byte[] aMsgPack, int description) {
        Intent intent = new Intent(context, TcpIntentService.class);
        intent.setAction(ACTION_FILE_SERVICE);
        intent.putExtra(EXTRA_MESSAGE_PACK, aMsgPack);
        intent.putExtra(EXTRA_SENDER, context.getClass().getSimpleName());
        intent.putExtra(EXTRA_DECRIPTION, description);
        context.startService(intent);
    }



    public TcpIntentService() {
        super("TcpIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mBroadcaster = LocalBroadcastManager.getInstance(this);
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FILE_SERVICE.equals(action)) {
                byte[] msgPack = intent.getByteArrayExtra(EXTRA_MESSAGE_PACK);
                if (msgPack == null) {
                    Log.d(TAG, "msg pack extra is null, use mpp.getSendBuf");
                    msgPack = ((MyApplication)getApplication()).getSendBuf();
                }
                final String sender = intent.getStringExtra(EXTRA_SENDER);
                final int description = intent.getIntExtra(EXTRA_DECRIPTION, Constants.DESC_UNKNOW);
                handleActionFileService(sender, msgPack, description);
            }
        }
        mBroadcaster = null;
    }

    /**
     * Handle action read file in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFileService(String sender, byte[] msgPack, int description) {
        MyApplication app = (MyApplication)getApplication();
        /**
         * Work requests run sequentially. If an operation is running in the IntentService,
         * and you send it another request, the request waits until the first operation is
         * finished. So must check whether the app still connect to the CAN before create
         * socket connection.
         */
        if (!app.isOnCAN) {
            broadcastIntentTcpResult(sender, Constants.STATE_ACTION_COMPLETE, null, description);
            return;
        }

        try {
            Thread.sleep(Constants.TCP_SLEEP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String aIp = app.mIp;
        int aPort = app.mPort;

        Socket socket= null;
        byte[]  recvBuf = null;
        try {
            //Broadcasts an Intent indicating that processing has started.
            broadcastIntentTcpStatus(sender, Constants.STATE_ACTION_STARTED,description);
            Log.i(TAG, String.format("Socket connect to %s:%d", aIp, aPort));

            socket = new Socket();
            InetSocketAddress addr = new InetSocketAddress(aIp, aPort);

            //Broadcast an Intent indicating that the service is about to connect to the CAN.
            broadcastIntentTcpStatus(sender, Constants.STATE_ACTION_CONNECTING, description);

            socket.connect(addr, Constants.SOCKET_CONNECT_TIMEOUT);

            //Calculate wait time depend on message size. The speed is 10kB/s
            int waitTimeout = Math.max((msgPack.length / (1024*5))*1000, Constants.SOCKET_READ_TIMEOUT);
            //Set socket receive timeout.
            Log.d(TAG, String.format("socket receive timeout %d", waitTimeout));
            socket.setSoTimeout(waitTimeout);

            //Broadcast an Intent indicating that the service is sending request data to the CAN.
            broadcastIntentTcpStatus(sender, Constants.STATE_ACTION_SEND, description);

            sendMsg(socket, msgPack, sender, description);

            //Broadcast an Intent indicating that the service is waiting the response from the CAN.
            broadcastIntentTcpStatus(sender, Constants.STATE_ACTION_RECEIVE,description);

            recvBuf = recvMsg(socket, sender, description);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }finally {
            if (null != socket) {
                try {
                    Log.d(TAG, "close socket");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //Report the response result
        broadcastIntentTcpResult(sender, Constants.STATE_ACTION_COMPLETE, recvBuf,description);
    }

    private void sendMsg(Socket socket, byte[] buffer, String sender, int description) {
        try {
            if (null != buffer && buffer.length > 0) {
                DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
                writer.write(buffer);
                writer.flush();
                Log.d(TAG, String.format("Send data %d bytes", buffer.length));
            } else {
                Log.e(TAG, "Buffer is null or length = 0");
            }
        } catch (IOException e) {
            //Broadcast an Intent indicating that the service failed to send data.
            broadcastIntentTcpStatus(sender, Constants.STATE_ACTION_SEND_FAILED, description);
            Log.e(TAG, e.toString());
        }
    }

    private byte[] recvMsg(Socket socket, String sender, int description) {
        if (null == socket) { return null;}

        byte[] result = null;
        try {
            //First read the message header, get the total length of the message.
            byte[] header = new byte[MsgConst.MSG_LEN_HEADER];
            InputStream inputStream = socket.getInputStream();
            int len = inputStream.read(header);
            if (len < MsgConst.MSG_LEN_HEADER) {
                throw new IOException(String.format(Locale.getDefault(), "[Read Header] receive " +
                        "%d bytes, less than header size.", len));
            }
            Log.d(TAG, String.format("[Read Header] : %s", MsgUtils.bytes2HexString(header)));

            //Get the total message length
            len = (header[4] & 0xff) + ((header[5] & 0xff) << 8);
            //Get the Service Type
            int SrvType = header[10] & 0xff;
            //if the Service Type is Service File, the Reserve Byte indicate the message length too.
            if (SrvType==ServiceType.SERVICE_FILE)
                len += (header[6] & 0xff) << 16;

            if (len <= MsgConst.MSG_LEN_HEADER) {
                //Broadcast an Intent indicating that the service is timeout to wait receive.
                broadcastIntentTcpStatus(sender, Constants.STATE_ACTION_TIMEOUT, description);
                throw new IOException(String.format(Locale.getDefault(), "[Read Header] the " +
                        "length in header is %d, less than header size.", len));
            }

            //get the crc in header
            int crcinHeader = MsgUtils.bytes2Int(header, 12);

            //Create body buffer
            int remainingLen = len - MsgConst.MSG_LEN_HEADER;
            byte[] body = new byte[remainingLen];
            int readLen;
            int offset = 0;
            while (remainingLen>0) {
                readLen = inputStream.read(body, offset, remainingLen);
                if (readLen==-1) { break;}
                offset += readLen;
                remainingLen -= readLen;
                Log.d(TAG, String.format("[Read Body]receive %d bytes, remain %d bytes ",
                        readLen, remainingLen));
            }
            Log.d(TAG, String.format("[Read Body]Total receive %d bytes. " +
                    "The body size is %d bytes.", offset, body.length));
            if (offset != body.length) {
                throw new IOException(String.format(Locale.getDefault(),
                        "[Read Body]Total receive data size[%d] is not equal the body size[%d]",
                        offset, body.length));
            }
            //Calculate the CRC of body.
            int crc = MsgUtils.getCrc32(body, body.length);
            Log.d(TAG, String.format("%d -- CRC in Header   %d -- Calculated", crcinHeader, crc));
            if (crcinHeader != crc) {
                throw new IOException(String.format(Locale.getDefault(),
                        "The CRC[%d] in Header is not equal the body CRC[%d]  ", crcinHeader, crc));
            }

            //Create the complete message pack
            result = new byte[header.length + body.length];
            System.arraycopy(header, 0, result, 0, header.length);
            System.arraycopy(body, 0, result, header.length, body.length);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        return  result;
    }

    private void broadcastIntentTcpResult(String sender, int status, byte[] recv, int description) {
        Intent localIntent = new Intent();

        // The Intent contains the custom broadcast action for this app
        localIntent.setAction(Constants.TCP_WORK_STATUS_ACTION);

        // Puts the status into the Intent
        localIntent.putExtra(Constants.EXTENDED_TCP_STATUS, status);
        if (null != recv) {
            localIntent.putExtra(Constants.EXTENDED_TCP_RECEIVE_DATA, recv);
        }
        localIntent.putExtra(Constants.EXTENDED_OWNER, sender);
        localIntent.putExtra(Constants.EXTENTED_DESCRIPTION, description);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Broadcasts the Intent
        mBroadcaster.sendBroadcast(localIntent);
    }

    private void broadcastIntentTcpStatus(String sender, int status, int description) {
        broadcastIntentTcpResult(sender, status, null, description);
    }
}
