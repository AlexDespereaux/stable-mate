package com.jaram.jarambuild.CalibrateUtils;


public class MeasureUtils
{
    private MeasureUtils()
    {
    }




    //Metric measurement conversions
    public static double millimetersToMeters(double measurement)
    {
        return measurement * 0.001;
    }

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

    public static double metersToMillimeters(double measurement)
    {
        return measurement * 1000;
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
