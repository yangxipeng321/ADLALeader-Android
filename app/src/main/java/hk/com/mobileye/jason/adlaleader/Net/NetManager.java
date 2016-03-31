package hk.com.mobileye.jason.adlaleader.Net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;

import hk.com.mobileye.jason.adlaleader.common.Constants;

/**
 * Created by Jason on 2014/12/29.
 * NetManager
 */
public class NetManager {
    private static String TAG = "NetManager";
    private WifiInfo wifiInfo;
    private NetworkInfo networkInfo;


    public NetManager(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
    }

    public String getWifiInfo() {
        StringBuilder strBuilder = new StringBuilder();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        strBuilder.append(simpleDateFormat.format(new Date()));
        strBuilder.append(String.format("%tT", new Date()));
        if (wifiInfo != null && networkInfo!=null) {
            strBuilder.append("\r\nSSID : ").append(wifiInfo.getSSID());
            strBuilder.append("\r\nBSSID : ").append(wifiInfo.getBSSID());
            strBuilder.append("\nIP : ").append(getIPAddress(wifiInfo.getIpAddress()));
            strBuilder.append("\r\nSpeed : ").append(wifiInfo.getLinkSpeed());
            strBuilder.append("\r\nSupplicant State : ").append(wifiInfo.getSupplicantState());
            strBuilder.append("\r\nNetinfo State : ").append(networkInfo.getState());
        }
        return strBuilder.toString();
    }

    private String getIPAddress(int ip) {
        if (ip == 0) return null;
        return (ip & 0xff) + "." + (ip >> 8 & 0xff) + "." + (ip >> 16 & 0xff)
                + "." + (ip >> 24 & 0xff);
    }

    public String getSSID() {
        String result =  (wifiInfo == null) ? null : wifiInfo.getSSID();
        if (null != result) {
            if (result.startsWith("\"") && result.endsWith("\""))
                result = result.substring(1, result.length()-1);
        }
        return result;
    }

    //get ip of wifi network
    public static String getIfConfig() {
        StringBuilder ifconfig = new StringBuilder();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                     enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddr = enumIpAddr.nextElement();
                    if (!inetAddr.isLoopbackAddress() && !inetAddr.isLinkLocalAddress()
                            && inetAddr.isSiteLocalAddress()) {
                        ifconfig.append(Arrays.toString(inetAddr.getAddress())).append("\n");
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        return ifconfig.toString();
    }

    //Whether there is a wifi internet connection.
    public boolean isOnline() {
        return (checkWifiConnection() && !checkSSID(Constants.SSID)
                && !checkSSID(Constants.TEST_SSID)) || checkMobileyeConnection();
    }

    //Whether there is a connection on ADASLeader wifi or Test Wifi
    public boolean isOnCAN() {
        return checkWifiConnection() && (checkSSID(Constants.SSID)
                || checkSSID(Constants.TEST_SSID)) ;
    }

    //return true if ssid is same
    public boolean checkSSID(String ssid) {
        String thisSSID = getSSID().toLowerCase();
        if (ssid != null && thisSSID != null
                && thisSSID.startsWith(ssid.toLowerCase())) {
            Log.d(TAG, String.format("return true ssid : %s   thisSSID : %s", ssid, thisSSID));
            return true;
        } else {
            Log.d(TAG, String.format("return false ssid : %s   thisSSID : %s", ssid, thisSSID));
            return false;
        }
    }

    //return true if wifi connected
    public boolean checkWifiConnection() {
//        if (networkInfo != null && networkInfo.isConnected()) {
//            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
//        } else {
//            return false;
//        }
        return networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public boolean checkMobileyeConnection() {
        return networkInfo!=null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    //Gets address according to SSID.
    public SocketAddress getUdpServerAddr() {
        SocketAddress addr = null;
        if (checkSSID(Constants.SSID)) {
            addr = new InetSocketAddress(Constants.IP, Constants.UDP_PORT);
        }else if (checkSSID(Constants.TEST_SSID)) {
            addr = new InetSocketAddress(Constants.TEST_IP, Constants.UDP_PORT);
        }
        return addr;
    }

    public InetSocketAddress getTcpServerAddr() {
        InetSocketAddress addr = null;
        if (checkSSID(Constants.SSID)) {
            addr = new InetSocketAddress(Constants.IP, Constants.TCP_PORT);
        }else if (checkSSID(Constants.TEST_SSID)) {
            addr = new InetSocketAddress(Constants.TEST_IP, Constants.TCP_PORT);
        }
        return addr;
    }

}
