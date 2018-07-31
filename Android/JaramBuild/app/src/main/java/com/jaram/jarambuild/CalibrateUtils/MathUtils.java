package com.jaram.jarambuild.CalibrateUtils;

import android.graphics.Point;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.lang.Math;

public class MathUtils
{

    private MathUtils(){}

    /**
     * Based on a list of 4 points, computes the distance between the last 2 using the first 2 as a
     * reference.
     * @param points A List of 4 points
     * @param scale The value of the distance between the first 2 points
     * @param inputUnitIndex Input unit
     * @param outputUnitIndex Output unit
     * @return The value of the distance between the last 2 points
     */
    public static double[] compute(List<Point> points, double scale, int inputUnitIndex, int outputUnitIndex){
        double[] msPoints;
        msPoints = new double[2];
        String TAG = "MathUtils";
        if(points.size() < 4){msPoints[0]=-1;msPoints[1]=-1; return msPoints ;}

        //Get reference points
        Point ref1 = points.get(0);
        Point ref2 = points.get(1);
        //Get the measurement points
        Point m1 = points.get(2);
        Point m2 = points.get(3);

        double reference = getDistance(ref1, ref2);
        Log.d(TAG, "Distance between Referance points: " + reference);
        double measurement = getDistance(m1, m2);
        Log.d(TAG, "Distance between Measurement points: " + measurement);

        measurement = (measurement * scale) / reference; //Get the actual distance
        //Convert to the right unit
        measurement = convertUnits(inputUnitIndex, reference, outputUnitIndex, measurement);
        msPoints[0] = measurement;

        if (points.size() == 6){
            Point m3 = points.get(4);
            Point m4 = points.get(5);
            double measurement2 = getDistance(m3,m4);
            measurement2 = (measurement2 * scale) / reference;
            measurement2 = convertUnits(inputUnitIndex, reference, outputUnitIndex, measurement2);
            msPoints[1]= measurement2;
        }
        return msPoints;
    }

    /**
     * Get the distance between 2 points
     * @param p1 First point
     * @param p2 Second point
     * @return Distance between the 2 points
     */
    private static double getDistance(Point p1, Point p2){
        double x = Math.pow(p2.x - p1.x, 2);
        double y = Math.pow(p2.y - p1.y, 2);
        return Math.sqrt(x+y);
    }

    /**
     * Converts between units of length.
     * @param refUnit The unit of the reference size
     * @param reference The reference size
     * @param meaUnit The unit of the measurement size
     * @param measurement The measurement size
     * @return measurement converted to refUnit
     */
    private static double convertUnits(int refUnit, double reference, int meaUnit, double measurement){
        if(refUnit == meaUnit)
            return measurement;

        measurement = toMeters(measurement, refUnit);
        switch (meaUnit){
            case 0:
                return measurement;
            case 1:
                return MeasureUtils.metersToCentimeters(measurement);
            case 2:
                return MeasureUtils.metersToMillimeters(measurement);
            case 3:
                return MeasureUtils.metersToInch(measurement);
            case 4:
                return MeasureUtils.metersToFeet(measurement);
            case 5:
                return MeasureUtils.metersToYards(measurement);
            default:
                return -1;
        }
    }

    /**
     * Converts a value in a given unit to meters.
     * @param measurement The length value.
     * @param refUnit The original unit.
     * @return The length value in meters
     */
    private static double toMeters(double measurement, int refUnit){
        switch (refUnit){
            case 0:
                return measurement;
            case 1:
                return MeasureUtils.centimetersToMeters(measurement);
            case 2:
                return MeasureUtils.millimetersToMeters(measurement);
            case 3:
                return MeasureUtils.inchesToMeters(measurement);
            case 4:
                return MeasureUtils.yardsToMeters(measurement);
            default:
                return -1;
        }
    }
}
