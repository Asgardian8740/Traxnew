package in.akshay.traxnew;

public  class ObservableInteger
{
    private static  OnIntegerChangeListener listen;

    private static double gforce;
    private  static float gspeed;

    public void setOnIntegerChangeListener(OnIntegerChangeListener listener)
    {
        listen = listener;
    }

    public static double  getforce()
    {
        return gforce;
    }


    public static void setGforce(double value)
    {
        gforce = value;

        if(listen != null)
        {
            listen.onDoubleChanged(value);
        }
    }
}