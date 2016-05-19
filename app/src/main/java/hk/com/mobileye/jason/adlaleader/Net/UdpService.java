package hk.com.mobileye.jason.adlaleader.net;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class UdpService extends Service {
    public UdpService() {
    }

//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Udp service start", Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);

    }


    public void dealRecv(byte[] buf) {
        //AlarmActivity.alarmHandler.obtainMessage(Constants.MSG_WARNING, -1, -1, buf).sendToTarget();
    }


}
