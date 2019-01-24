package in.akshay.traxnew;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;


public class Accident_Service extends IntentService {

    public static final String ACTION_1 ="MY_ACTION_1";
    public static float nspeed;
    public static double nforce;



    public Accident_Service() {
        super("Accident_Service");
    }



    @Override
    protected void onHandleIntent(Intent intent) {


        Speed_Observer obsSpeed = new Speed_Observer();
        obsSpeed.setOnSpeedChangeListener(new OnSpeedChangeListener() {
            @Override
            public void onFLoatChanged(float newValue) {
                nspeed=newValue;
            }
        });


        ObservableInteger obsForce=new ObservableInteger();
        obsForce.setOnIntegerChangeListener(new OnIntegerChangeListener()
        {
            @Override
            public void onDoubleChanged(double newValue) {
                nforce=newValue;
            }

        });

        






    }

}
