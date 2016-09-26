package grupo3.labredes.appmovilgps;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

public class MainActivity extends Activity {
    Button btnShowLocation;
    GPSTracker gps;
    TextView text;
    int currentSec;
    Button btnStartAppUDP;
    Button btnStartAppTCP;

    BroadcastReceiver onBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent i) {
            // do stuff to the UI
            Bundle extras = i.getExtras();
            text.setText("Lat: " + extras.getDouble("lat") + "\nLong: " + extras.getDouble("lon") +
                    "\nCurrent Second: " + extras.getInt("current"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentSec = 0;
        // create class object

        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        text = (TextView) findViewById(R.id.textView);
        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                gps = new GPSTracker(MainActivity.this);
                // check if GPS enabled
                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    text.setText("Lat: " + latitude + "\nLong: " + longitude);
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }
        });

        btnStartAppUDP = (Button) findViewById(R.id.btnStartUDP);
        btnStartAppUDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startService(new Intent(MainActivity.this, GPSUpdaterUDP.class));
                //updater = new GPSUpdaterUDP(MainActivity.this);
            }
        });

        btnStartAppTCP = (Button) findViewById(R.id.btnStartTCP);
        btnStartAppTCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startService(new Intent(MainActivity.this, GPSUpdaterTCP.class));
                //updater = new GPSUpdaterUDP(MainActivity.this);
            }
        });
    }
    @Override
    protected void onResume(){
        registerReceiver(onBroadcast, new IntentFilter("Update"));
        super.onResume();
    }

    @Override
    protected void onPause(){
        unregisterReceiver(onBroadcast);
        super.onPause();
    }


}
