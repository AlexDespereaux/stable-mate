package com.jaram.jarambuild.CalibrateUtils;


public class MeasureUtils
{
    private MeasureUtils()
    {
    }

    //Meter conversions - to be used if required (Leaving them out at the moment as too many options on the app makes use fiddly)
    public static double millimetersToMeters(double measurement)
    {
        return measurement * 0.001;
    }

    public static double metersToMillimeters(double measurement)
    {
        return measurement * 1000;
    }

    //Metric measurement conversions

    public static double millimetersToCentimeters(double measurement)
    {
        return measurement * 0.1;
    }

    public static double millimetersToMicrons(double measurement)
    {
        return measurement * 1000;
    }

    public static double millimetersToNanometers(double measurement)
    {
        return measurement * 1000000;
    }

    public static double centimetersToMillimeters(double measurement)
    {
        return measurement * 10;
    }

    public static double micronsToMillimeters(double measurement)
    {
        return measurement * 0.001;
    }

    public static double nanometersToMillimeters(double measurement)
    {
        return measurement * 0.000001; //
    }

}
