package com.appiedi.datastorage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sensorcon.sdhelper.SDHelper;
import com.sensorcon.sensordrone.CoreDrone;
import com.sensorcon.sensordrone.DroneEventListener;
import com.sensorcon.sensordrone.DroneEventObject;
import com.sensorcon.sensordrone.DroneStatusListener;
import com.sensorcon.sensordrone.android.Drone;
import com.sensorcon.sensordrone.android.tools.DroneQSStreamer;
import com.sensorcon.sensordrone.android.tools.DroneStreamer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Pietro on 14/02/14.
 * Copyright Pietro 2014
 */
public class MainActivity extends Activity implements LocationListener{

    public Drone myDrone;
    public SDHelper myHelper;
    public DroneEventListener myListener;
    public DroneStatusListener myDListener;
    public DroneStreamer myBlinker;
    public DroneQSStreamer[] streamerArray = new DroneQSStreamer[2];
    public int streamingRate = 1000;
    public int blinkerRate = 500;
    public boolean led = true;

    String provider = "null";

    ProgressDialog progressDialog;
    QuickMessage mex;
    TextView sd_state;
    Button start;

    File myFile;
    FileOutputStream fOut;
    OutputStreamWriter myOutWriter;
    String dir,ID;
    String filename = "DataStorage.txt";

    Calendar calendar;
    SimpleDateFormat dayFormat;

    BluetoothAdapter myBluetooth;

//LOCATION VARIABLES********************************

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

//****************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        setContentView(R.layout.fragment_main);
        sd_state = (TextView) findViewById(R.id.db_sd_state);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        ID = String.valueOf(telephonyManager.getDeviceId());

        dayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.UK);

        start = (Button) findViewById(R.id.db_stop);
        start.setVisibility(View.INVISIBLE);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        mex = new QuickMessage(this);

        if (myBluetooth == null){
            //Bluetooth not available
            mex.quickMessage("Bluetooth not available");
            finish();
        }

        myHelper = new SDHelper();
        myDrone = new Drone();
        myBlinker = new DroneStreamer(myDrone,blinkerRate) {
            @Override
            public void repeatableTask() {
                if (led)
                {
                    myDrone.setRightLED(0,0,255);
                    myDrone.setLeftLED(0,0,255);
                }
                else
                    myDrone.setLEDs(0,0,0);
                led = !led;
            }
        };
        streamerArray[0] = new DroneQSStreamer(myDrone, CoreDrone.QS_TYPE_PRECISION_GAS);
        myListener = new DroneEventListener() {
            @Override
            public void capacitanceMeasured(DroneEventObject droneEventObject) {

            }

            @Override
            public void adcMeasured(DroneEventObject droneEventObject) {

            }

            @Override
            public void rgbcMeasured(DroneEventObject droneEventObject) {

            }

            @Override
            public void pressureMeasured(DroneEventObject droneEventObject) {

            }

            @Override
            public void altitudeMeasured(DroneEventObject droneEventObject) {

            }

            @Override
            public void irTemperatureMeasured(DroneEventObject droneEventObject) {

            }

            @Override
            public void humidityMeasured(DroneEventObject droneEventObject) {

            }

            @Override
            public void temperatureMeasured(DroneEventObject droneEventObject) {

            }

            @Override
            public void reducingGasMeasured(DroneEventObject droneEventObject) {

            }

            @Override
            public void oxidizingGasMeasured(DroneEventObject droneEventObject) {

            }

            @Override
            public void precisionGasMeasured(DroneEventObject droneEventObject) {
                //save data into file
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        calendar = Calendar.getInstance();
                        String date = dayFormat.format(calendar.getTime());
                        Location location1 = getLocation();
                        try {
                            myOutWriter.append(ID)
                                    .append("\t")
                                    .append(date)
                                    .append("\t")
                                    .append(String.valueOf(myDrone.precisionGas_ppmCarbonMonoxide))
                                    .append("\t")
                                    .append(String.valueOf(location1.getLatitude()))
                                    .append("\t")
                                    .append(String.valueOf(location1.getLongitude()))
                                    .append("\t")
                                    .append(provider)
                                    .append("\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                streamerArray[0].streamHandler.postDelayed(streamerArray[0],streamingRate);
            }

            @Override
            public void uartRead(DroneEventObject droneEventObject) {

            }

            @Override
            public void i2cRead(DroneEventObject droneEventObject) {

            }

            @Override
            public void usbUartRead(DroneEventObject droneEventObject) {

            }

            @Override
            public void customEvent(DroneEventObject droneEventObject) {

            }

            @Override
            public void connectEvent(DroneEventObject droneEventObject) {
                myBlinker.start();
                streamerArray[0].enable();
                myDrone.quickEnable(CoreDrone.QS_TYPE_PRECISION_GAS);
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sd_state.setText("Connected!");
                        start.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void disconnectEvent(DroneEventObject droneEventObject) {
                streamerArray[0].disable();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sd_state.setText("Disconnected!");
                        start.setVisibility(View.INVISIBLE);
                    }
                });
                saveData();
            }

            @Override
            public void connectionLostEvent(DroneEventObject droneEventObject) {
                myBlinker.stop();
                mex.quickMessage("Connection lost!");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sd_state.setText("Disconnected!");
                        start.setEnabled(false);
                    }
                });
                saveData();
            }

            @Override
            public void unknown(DroneEventObject droneEventObject) {

            }
        };
        myDListener = new DroneStatusListener() {
            @Override
            public void capacitanceStatus(DroneEventObject droneEventObject) {

            }

            @Override
            public void adcStatus(DroneEventObject droneEventObject) {

            }

            @Override
            public void rgbcStatus(DroneEventObject droneEventObject) {

            }

            @Override
            public void pressureStatus(DroneEventObject droneEventObject) {

            }

            @Override
            public void altitudeStatus(DroneEventObject droneEventObject) {

            }

            @Override
            public void irStatus(DroneEventObject droneEventObject) {

            }

            @Override
            public void humidityStatus(DroneEventObject droneEventObject) {

            }

            @Override
            public void temperatureStatus(DroneEventObject droneEventObject) {

            }

            @Override
            public void oxidizingGasStatus(DroneEventObject droneEventObject) {

            }

            @Override
            public void reducingGasStatus(DroneEventObject droneEventObject) {

            }

            @Override
            public void precisionGasStatus(DroneEventObject droneEventObject) {
                if (myDrone.precisionGasStatus)
                    streamerArray[0].run();
            }

            @Override
            public void batteryVoltageStatus(DroneEventObject droneEventObject) {

            }

            @Override
            public void chargingStatus(DroneEventObject droneEventObject) {

            }

            @Override
            public void customStatus(DroneEventObject droneEventObject) {

            }

            @Override
            public void unknownStatus(DroneEventObject droneEventObject) {

            }

            @Override
            public void lowBatteryStatus(DroneEventObject droneEventObject) {

            }
        };
        myDrone.registerDroneListener(myListener);
        myDrone.registerDroneListener(myDListener);

        if (!myBluetooth.isEnabled()){
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setTitle("Sensordrone")
                    .setMessage("Connect your bluetooth!")
                    .setCancelable(true)
                    .setPositiveButton("Ok", null);
            AlertDialog dialog = build.create();
            dialog.show();
        }else{
            try {
                dir = Environment.getExternalStorageDirectory().getAbsolutePath();
                myFile = new File(dir+"/"+filename);
                fOut = new FileOutputStream(myFile);
                myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append("Phone ID")
                        .append("\t")
                        .append("Date - Time")
                        .append("\t")
                        .append("CO value (ppm)")
                        .append("\t")
                        .append("latitude")
                        .append("\t")
                        .append("longitude")
                        .append("\t")
                        .append("provider")
                        .append("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (myDrone.isConnected)
                myDrone.disconnect();

            progressDialog = ProgressDialog.show(this, "", "Connecting...", true, true);
            myHelper.scanToConnect(myDrone, this, this, true, progressDialog);

        }
    }

    public void saveData(){
        try {
            myOutWriter.close();
            fOut.close();
            mex.quickMessage("saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                provider = "null";
                AlertDialog.Builder build = new AlertDialog.Builder(this);
                build.setTitle("Location")
                        .setMessage("Connect GPS or Internet!")
                        .setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                AlertDialog dialog = build.create();
                dialog.show();
            } else {
                this.canGetLocation = true;

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        provider = "GPS";
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                //get location from Network Provider
                if (isNetworkEnabled) {
                    provider = "NetworkProfile";
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.db_connect) {
            if (!myBluetooth.isEnabled()){
                AlertDialog.Builder build = new AlertDialog.Builder(this);
                build.setTitle("Sensordrone")
                        .setMessage("Connect your bluetooth!")
                        .setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                AlertDialog dialog = build.create();
                dialog.show();
            }else{
                if (!myDrone.isConnected){
                    progressDialog = ProgressDialog.show(this, "", "Connecting...", true, true);
                    myHelper.scanToConnect(myDrone, this, this, true, progressDialog);
                }else{
                    mex.quickMessage("Already connected");
                }
            }
            return true;
        }else if (id == R.id.db_disconnect){
            if (myDrone.isConnected){
                SensordroneDisconnect();
            }else{
                mex.quickMessage("Not connected");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.db_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        if (myDrone.isConnected){
            SensordroneDisconnect();
        }
        super.onDestroy();
    }

    public void SensordroneDisconnect(){
        myBlinker.stop();
        myDrone.setLEDs(0,0,0);
        myDrone.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude =  (location.getLatitude());
        longitude = (location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}
