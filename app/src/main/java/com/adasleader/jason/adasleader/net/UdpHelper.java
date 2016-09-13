package com.adasleader.jason.adasleader.net;

import android.net.wifi.WifiManager;
import android.os.Handler;

import com.adasleader.jason.adasleader.common.Constants;
import com.adasleader.jason.adasleader.common.logger.Log;
import com.adasleader.jason.adasleader.net.Message.MessageType;
import com.adasleader.jason.adasleader.net.Message.MsgConst;
import com.adasleader.jason.adasleader.net.Message.MsgUtils;
import com.adasleader.jason.adasleader.net.Message.ServiceType;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;

/**
 * Created by Jason on 2014/12/30.
 *
 */
public class UdpHelper implements Runnable {

    private static final int buffSize = 2000;
    private static final String TAG ="UdpHelper";
    private Handler mHandler;
    public Boolean isThreadDisable = false;

    private DatagramSocket socket;

    public UdpHelper() {
        try {
            //socket = new DatagramSocket(mPort);
            //Constructs a udp socket which is not bound a specific port on the localhost
            socket = new DatagramSocket();
            socket.setBroadcast(false);
        } catch (SocketException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    public UdpHelper(WifiManager manager) {
        this();
    }

    public UdpHelper(Handler handler) {
        this();
        mHandler = handler;
    }

    public void closeSocket() throws IOException {
        isThreadDisable = true;
        if (socket != null) {
            socket.close();
        }
    }

    @Override
    public void run() {
        startListen();
    }

    public void startListen() {
        byte[] recv = new byte[buffSize];
        DatagramPacket packet = new DatagramPacket(recv, recv.length);
        try {
            while (!isThreadDisable) {
                //Log.d(TAG, "Ready for receiving data");
                socket.receive(packet);
                byte[] buf = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, buf, 0, buf.length);
//                Log.d(TAG, String.format("UDP recv : %tT %s len=0x%X\n%s\n",
//                        new Date(), packet.getAddress().getHostAddress(),
//                        buf.length, MsgUtils.bytes2HexString(buf)));
                processRecv(buf);
            }
            //socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
    }

    private void processRecv(byte[] buf) {
        //Check receive data length
        if (buf.length < MsgConst.MSG_LEN_HEADER) {
            Log.e(TAG, String.format(Locale.getDefault(),"Receive data less than %d", MsgConst.MSG_LEN_HEADER));
            return;
        }

        //Check head,the first 4 bytes must equal 0xAA
        for (int i = 0; i < 4; i++) {
            if ((buf[i]&0xff) != 0xaa) { return; }
        }

        //check message length
        int len = (buf[4] & 0xff) + ((buf[5] & 0xff) << 8);
        if (buf.length < len) { return; }

        byte serviceType = buf[10];
        byte msgType = buf[11];

        if (serviceType != ServiceType.SERVICE_WARNING) {
            Log.e(TAG, MsgUtils.bytes2HexString(buf));
        }


//        Log.d(TAG, "serviceType " + serviceType);
        if (mHandler != null) {
            switch (serviceType) {
                case ServiceType.SERVICE_WARNING:
                    mHandler.obtainMessage(Constants.MSG_WARNING, buf).sendToTarget();
//                    Log.d(TAG, "Handler send message [WARNING]");
                    break;
                case ServiceType.SERVICE_HEARTBEAT:
                    break;
                case ServiceType.SERVICE_CMD:
                    switch (msgType) {
                        case MessageType.CMD_SWITCH_SCREEN_RESP:
                            mHandler.obtainMessage(Constants.MSG_SWITCH_SCREEN, buf).sendToTarget() ;
                            break;
                    }
                    break;
                case ServiceType.SERVICE_DVR:
                    switch (msgType) {
                        case MessageType.DVR_PLAY_FILE_RESP:
                            mHandler.obtainMessage(Constants.MSG_DVR_PLAY_FILE, buf).sendToTarget();
                    }
                    break;
                case ServiceType.SERVICE_DEBUG:
                    switch (msgType) {
                        case MessageType.LOG_ADASGATE_CONTENT:
                        case MessageType.LOG_MCU_CONTENT:
                            mHandler.obtainMessage(Constants.MSG_LOG_CONTENT, buf).sendToTarget();
                            break;
                    }
                    break;
            }
        } else {
            Log.e(TAG, "mHandler is null");
        }
    }

    public void send(byte[] buffer, InetAddress addr, int port) {
        if (buffer == null || buffer.length == 0)
            return;
        Log.d(TAG, String.format("UDP send : %s",MsgUtils.bytes2HexString(buffer)));
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, port);
        if (socket != null) {
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
        }
    }

    public void send(byte[] buffer, SocketAddress sockAddr) {
        if (buffer == null || buffer.length == 0) {  return;   }
//        Log.d(TAG, String.format(Locale.getDefault(), "UDP send : %tT %s\n%s\n",
//                new Date(), sockAddr.toString(), MsgUtils.bytes2HexString(buffer)));
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, sockAddr);
            if (socket != null) {
                socket.send(packet);
            } else {
                throw new Exception("Udp socket is null. Can't send message!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }


    public static void send(byte[] buffer) {
        if (buffer == null || buffer.length == 0)
            return;
        Log.d(TAG, String.format("UDP send : %s", MsgUtils.bytes2HexString(buffer)));

        InetAddress host = null;
        try {
            host = InetAddress.getByName(Constants.TEST_IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                host, Constants.UDP_PORT);

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        try {
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    private static int seq = 0;
    public static int getSeq() {
        seq++;
        if (seq > 65535) {
            seq = 1;
        }
        return seq;
    }

}


