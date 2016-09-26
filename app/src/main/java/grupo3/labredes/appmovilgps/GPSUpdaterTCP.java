package grupo3.labredes.appmovilgps;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import static android.content.ContentValues.TAG;

/**
 * Created by user on 25/09/2016.
 */
public class GPSUpdaterTCP extends Service {

    public int currentSec;
    public GPSTracker gps;
    public double latitude;
    public double longitude;
    public InetAddress ip;
    public int port;
    Socket socket;
    PrintWriter printer;
    AsyncTask<Void, Void, Void> async;

    private boolean connected = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                for (currentSec = 0; currentSec < 50; currentSec++) {
                    gps = new GPSTracker(getApplicationContext());
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();

                    Log.d(TAG, "startUpdater: "+currentSec+ " Lat: "+latitude+" lon: "+longitude);
                    Intent i = new Intent("Update");
                    i.putExtra("lat", latitude);
                    i.putExtra("lon", longitude);
                    i.putExtra("current", currentSec);
                    getApplicationContext().sendBroadcast(i);
                    async = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                InetAddress serverAddr = InetAddress.getByName("192.168.0.13");
                                Log.d("ClientActivity", "C: Connecting...");
                                socket = new Socket(serverAddr, 4321);
                                connected = true;
                                printer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                                        .getOutputStream())), true);
                                printer.println("" + latitude + "," + longitude);
                                Log.d(TAG, "Envia mensaje TCP");
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                            return null;
                        }
                    };
                    async.execute();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                stopSelf();
            }
        });
        t.run();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
