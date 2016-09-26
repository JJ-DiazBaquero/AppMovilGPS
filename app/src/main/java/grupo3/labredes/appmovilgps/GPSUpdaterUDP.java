package grupo3.labredes.appmovilgps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static android.content.ContentValues.TAG;

/**
 * Created by user on 25/09/2016.
 */
public class GPSUpdaterUDP extends Service {

    //public final Context mContext;
    public int currentSec;
    public GPSTracker gps;
    public double latitude;
    public double longitude;
    public InetAddress ip;
    public int port;

    public GPSUpdaterUDP(Context context) {
        //this.mContext = context;
        startUpdater();
    }
    public GPSUpdaterUDP() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                startUpdater();
            }
        });
        t.run();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    public void startUpdater(){
        for (currentSec = 0; currentSec < 50; currentSec++) {
            gps = new GPSTracker(getApplicationContext());
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Log.d(TAG, "startUpdater: "+currentSec+ " Lat: "+latitude+" lon: "+longitude);
            // \n is for new line
            Intent i = new Intent("Update");
            i.putExtra("lat", latitude);
            i.putExtra("lon", longitude);
            i.putExtra("current", currentSec);
            getApplicationContext().sendBroadcast(i);

            Sender t = new Sender("192.168.0.13", ""+latitude+","+longitude, 1234);
            t.send();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
