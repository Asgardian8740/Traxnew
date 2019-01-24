package in.akshay.traxnew;

public class Speed_Observer {
    private static  OnSpeedChangeListener listen;

    private  static float gspeed;

    public void setOnSpeedChangeListener(OnSpeedChangeListener listener)
    {
        listen = listener;
    }


    public static float getspeed()
    {
        return gspeed;
    }

    public static void setGspeed(float value)
    {
        gspeed = value;

        if(listen != null)
        {
            listen.onFLoatChanged(value);
        }
    }

}
