package in.akshay.traxnew;

import android.annotation.SuppressLint;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.Gauge;
import com.github.anastr.speedviewlib.SpeedView;
import com.jjoe64.graphview.GraphView;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Subscription;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, Speed_Interface {

    public MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private ActionBar bar;

    Location locationadd;

    SpeedView speedometer;


    private final List<Graph_Plotter> mPlotters = new ArrayList<>(3);

    private Observable<?> mShakeObservable;
    private Subscription mShakeSubscription;



    @SuppressLint({"MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Mapbox.getInstance(this, "pk.eyJ1IjoieWFoc2thIiwiYSI6ImNqcWU1MGgwNTRieTk0M3BwMGQ3YjIyMWIifQ.7xJOeeGSOvUkcP38Zl_7UQ");


        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        bar = getSupportActionBar();
       bar.hide();
        speedometer = findViewById(R.id.speedView);
        speedometer.setSpeedTextPosition(SpeedView.Position.BOTTOM_CENTER);

        speedometer.setLowSpeedPercent(30);
        speedometer.setMediumSpeedPercent(60);
        speedometer.setMaxSpeed(180);
        speedometer.setMinSpeed(0);
        speedometer.setTickNumber(9);
        speedometer.setTrembleData((float) 0.5,3000);





        setupPlotters();
        mShakeObservable = Accident_Detector.create(this);

    }


    private void setupPlotters() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> accSensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        mPlotters.add(new Graph_Plotter("ACC", (GraphView) findViewById(R.id.graph2), Accelerometer_Observable.createSensorEventObservable(accSensors.get(0), sensorManager)));
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Speedometer myLocation = new Speedometer(location, true);
            this.updateSpeed(myLocation);
            updatetxtaddress();
        }

    }

    public void finish() {
        super.finish();
        System.exit(0);
    }

    private void updateSpeed(Speedometer location) {

        if(location==null){
            speedometer.setSpeedAt(10);
        }

        float nCurrentSpeed = 0;

        if (location != null) {
            location.setUseMetricunits(true);
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());

       /* int selectedColor = Color.argb(20,nCurrentSpeed,(10*nCurrentSpeed),(100*nCurrentSpeed));

        ColorDrawable colorDrawable = new ColorDrawable(selectedColor);

        bar.setBackgroundDrawable(colorDrawable);
        bar.setTitle(String.valueOf(nCurrentSpeed));*/
        speedometer.setSpeedAt(nCurrentSpeed);










    }



    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGpsStatusChanged(int event) {
        // TODO Auto-generated method stub

    }

    @Override
    @SuppressWarnings({"MissingPermission"})

    public void onStart() {
        super.onStart();
        mapView.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();

        mapView.onResume();

        Observable.from(mPlotters).subscribe(Graph_Plotter::onResume);
        mShakeSubscription = mShakeObservable.subscribe((object)-> Check_Accident.log());

    }

    @Override
    public void onPause() {
        super.onPause();
        Observable.from(mPlotters).subscribe(Graph_Plotter::onPause);
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onMapReady(MapboxMap mapboxMap) {


        MainActivity.this.mapboxMap = mapboxMap;
        enableLocationComponent();

    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent() {

        if (PermissionsManager.areLocationPermissionsGranted(this)) {



            LocationComponentOptions options = LocationComponentOptions.builder(this)
                    .trackingGesturesManagement(true)
                    .accuracyAlpha(0)
                    .accuracyColor(ContextCompat.getColor(this, R.color.mapboxGreen))
                    .build();

            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            locationComponent.activateLocationComponent(this);

            locationComponent.activateLocationComponent(this, options);

            locationComponent.setLocationComponentEnabled(true);


            locationComponent.setCameraMode(CameraMode.TRACKING_GPS_NORTH);

            locationComponent.zoomWhileTracking(18);
            locationadd=locationComponent.getLastKnownLocation();


            Toast.makeText(this, String.valueOf(locationComponent.getLastKnownLocation()), Toast.LENGTH_LONG).show();


            locationComponent.setRenderMode(RenderMode.GPS);



        } else {

            permissionsManager = new PermissionsManager(this);

            permissionsManager.requestLocationPermissions(this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Permission", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent();
        } else {
            Toast.makeText(this, "Not granted !", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public  void  updatetxtaddress(){

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        TextView loc=(TextView) findViewById(R.id.address);


        try {

            addresses = geocoder.getFromLocation(locationadd.getLatitude(),locationadd.getLongitude(), 1);





            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                loc.setText(address + " " + city + " " + country);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
