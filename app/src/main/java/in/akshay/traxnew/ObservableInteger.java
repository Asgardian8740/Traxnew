package in.akshay.traxnew;

public  class ObservableInteger
{
    private static  OnIntegerChangeListener listen;

    private static int val;

    public void setOnIntegerChangeListener(OnIntegerChangeListener listener)
    {
        listen = listener;
    }

    public static int get()
    {
        return val;
    }

    public static void set(int value)
    {
        val = value;

        if(listen != null)
        {
            listen.onIntegerChanged(value);
        }
    }
}