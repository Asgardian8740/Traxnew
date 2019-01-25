package in.akshay.traxnew;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorEvent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Formatter;

import rx.Observable;
import rx.Subscription;

public class Graph_Plotter {
    public static final int MAX_DATA_POINTS = 50;
    public static final int VIEWPORT_SECONDS = 5;
    public static final int FPS = 10;

    @NonNull
    private final String mName;

    private final long mStart = System.currentTimeMillis();

    protected final LineGraphSeries<DataPoint> mSeriesX;
   // protected final LineGraphSeries<DataPoint> mSeriesY;
    //protected final LineGraphSeries<DataPoint> mSeriesZ;
    private final Observable<SensorEvent> mSensorEventObservable;
    private long mLastUpdated = mStart;
    private Subscription mSubscription;



    public Graph_Plotter(@NonNull String name, @NonNull GraphView graphView,
                         @NonNull Observable<SensorEvent> sensorEventObservable) {
        mName = name;
        mSensorEventObservable = sensorEventObservable;


        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(VIEWPORT_SECONDS * 1000); // number of ms in viewport

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(-20);
        graphView.getViewport().setMaxY(20);

        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);

        mSeriesX = new LineGraphSeries<>();
        //mSeriesY = new LineGraphSeries<>();
        //mSeriesZ = new LineGraphSeries<>();
        mSeriesX.setColor(Color.RED);
        //mSeriesY.setColor(Color.GREEN);
        //mSeriesZ.setColor(Color.BLUE);

        graphView.addSeries(mSeriesX);
      //  graphView.addSeries(mSeriesY);
        //graphView.addSeries(mSeriesZ);
    }


    public void onResume(){
        mSubscription = mSensorEventObservable.subscribe(this::onSensorChanged);
    }


    public void onPause(){
        mSubscription.unsubscribe();
    }

    private void onSensorChanged(SensorEvent event) {
        if (!canUpdateUi()) {
            return;
        }



        double g = Math.sqrt(event.values[0]*event.values[0]+ event.values[1]*event.values[1] + event.values[2]*event.values[2]) / 9.8;
       ObservableInteger.setGforce(g);
        appendData(mSeriesX, g);
       // appendData(mSeriesY, event.values[1]);
        //appendData(mSeriesZ, event.values[2]);
    }

    private boolean canUpdateUi() {
        long now = System.currentTimeMillis();
        if (now - mLastUpdated < 1000 / FPS) {
            return false;
        }
        mLastUpdated = now;
        return true;
    }

    private void appendData(LineGraphSeries<DataPoint> series, double value) {
        series.appendData(new DataPoint(getX(), value), true, MAX_DATA_POINTS);
    }

    private long getX() {
        return System.currentTimeMillis() - mStart;
    }




}

