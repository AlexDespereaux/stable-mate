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
    public static double compute(List<Point> points, double scale, int inputUnitIndex, int outputUnitIndex){
        double msPoints;
        String TAG = "MathUtils";
        if(points.size() < 4){msPoints=-1; return msPoints ;}

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
        measurement = convertUnits(inputUnitIndex, outputUnitIndex, measurement);

        msPoints = measurement;
        return msPoints;
    }

    //reference is the reference distance input by user!
    public static double computePixelPerMicron(List<Point> points, double reference, int inputUnitIndex){
        double pixelPerMicron = -1;
        String TAG = "MathUtils";
        if(points.size() < 2){return -1 ;}

        //Get reference points
        Point ref1 = points.get(0);
        Point ref2 = points.get(1);

        //Calculate pixels between the 2 reference points
        double distInPixels = getDistance(ref1, ref2);
        Log.d(TAG, "Distance between Referance points: " + distInPixels);

        //Convert reference to microns
        double refInMicrons = convertUnits(inputUnitIndex, 1, reference);

        //Calculate scale ie 1 micron is 0.5 of reference if reference is 2 microns
        double ppmScale = 1/refInMicrons;

        //Calculate pixel per micron ie if distance between points is 200 and the scale is 0.5 = 100 pixel per micron
        pixelPerMicron = ppmScale * distInPixels;

        Log.d(TAG, "Pixel Per Micron: " + pixelPerMicron);
        return pixelPerMicron;
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
     * @param meaUnit The unit of the measurement size
     * @param measurement The measurement size
     * @return measurement converted to refUnit
     */
    private static double convertUnits(int refUnit, int meaUnit, double measurement){
        if(refUnit == meaUnit)
            return measurement;

        measurement = toMillimeters(measurement, refUnit);
        switch (meaUnit){
            case 0:
                return MeasureUtils.millimetersToNanometers(measurement);
            case 1:
                return MeasureUtils.millimetersToMicrons(measurement);
            case 2:
                return measurement;
            case 3:
                return MeasureUtils.millimetersToCentimeters(measurement);
            case 4:
                return MeasureUtils.millimetersToMeters(measurement);
            default:
                return -1;
        }
    }

    /**
     * Converts a value in a given unit to meters.
     * @param measurement The length value.
     * @param refUnit The original unit.
     * @return The length value in millimeters
     */
    private static double toMillimeters(double measurement, int refUnit){
        switch (refUnit){
            case 0:
                return MeasureUtils.nanometersToMillimeters(measurement);
            case 1:
                return MeasureUtils.micronsToMillimeters(measurement);
            case 2:
                return measurement;
            case 3:
                return MeasureUtils.centimetersToMillimeters(measurement);
            case 4:
                return MeasureUtils.metersToMillimeters(measurement);
            default:
                return -1;
        }
    }
}
