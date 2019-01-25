package in.akshay.traxnew;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;


public class Accident_Service extends IntentService {

    public static final String ACTION_1 = "MY_ACTION_1";
    public static float nspeed;
    public static double nforce;


    public Accident_Service() {
        super("Accident_Service");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        int k = -1;

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Accident_Service.ACTION_1);

        Speed_Observer obsSpeed = new Speed_Observer();
        obsSpeed.setOnSpeedChangeListener(newValue -> nspeed = newValue);

        ObservableInteger obsForce = new ObservableInteger();
        obsForce.setOnIntegerChangeListener(newValue -> nforce = newValue);

        if (nforce >= 3) {
            if (nspeed > 20) {
                k = 0;
            }
        }

        if (nforce >= 3 && nforce <= 4) {
            if (nspeed > 24) {
                k = 1;
            }
        }

        if (nforce > 4) {
            if (nspeed >= 24) {
                k = 2;
            }
        }
        if (nforce >= 4.5) {
            k = 3;
        }
        for (int i = 0; i <= 100; i++) {
            broadcastIntent.putExtra("percel", k+i);
            sendBroadcast(broadcastIntent);

            SystemClock.sleep(100);

        }


    }


}
