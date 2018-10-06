package com.jaram.jarambuild;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.jaram.jarambuild.CalibrateUtils.DrawingOnImage;
import com.jaram.jarambuild.CalibrateUtils.SurfaceImage;
import com.jaram.jarambuild.roomDb.AppDatabase;
import com.jaram.jarambuild.roomDb.Calibration;
import com.jaram.jarambuild.utils.TinyDB;

import java.util.List;
import java.util.Objects;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class CheckCalibrationActivity extends AppCompatActivity
{
    private String TAG = "CheckCaliAct";
    FrameLayout imageholder;
    DrawingOnImage drawing;
    private String imageFilePath;
    private double confirmedPixelsPerMicron;

    //results of new calibration
    double dFov;
    double pixPerMic;
    int ocularLens;
    int objectiveLens;
    String calibrationId;
    double distBetweenCaliPointsInPix;
    double newPPM = 0; // new image pixel per micron
    double newDFOV = 0; // new diagonal field of view

    //settings from db
    int savedCaliPosition = 0;
    double savedDfov;
    double savedPPM;
    int savedObjLens;
    int savedOcuLens;

    //buttons
    Button clearBtn;
    Button okBtn;
    Button cancelBtn;

    //textview
    TextView instructTxt;

    //get logged in user
    TinyDB tinydb;
    String loggedInUser;

    //db
    List<Calibration> calibrationList;

    //data to pass to edit activity
    int sBcolorPosition;

    //quickstart
    private static final String SHOWCASE_ID = "cali_check_act";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_calibrate);

        imageholder = findViewById(R.id.frame);

        //get logged in user for db
        tinydb = new TinyDB(this);
        loggedInUser = tinydb.getString("loggedInAccount");
        Log.d(TAG, "loggedInUser: " + loggedInUser);

        //hide action bar
        android.support.v7.app.ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null)
        {
            myActionBar.hide();
            Log.d(TAG, "ActionBar Hidden");
        }

        //Get bitmap path from intent (from Camera)
        imageFilePath = Objects.requireNonNull(getIntent().getExtras()).getString("rawPhotoPath");
        Log.d(TAG, "imageFilePath: " + imageFilePath);

        //create canvas and drawing views
        SurfaceImage image = new SurfaceImage(this, imageFilePath);
        Log.d(TAG, "made newSI");

        //add views to frameLayout
        imageholder.addView(image);
        Log.d(TAG, "added photoView");
        drawing = new DrawingOnImage(this, 2);
        imageholder.addView(drawing);
        Log.d(TAG, "added drawingView");

        //buttons
        okBtn = findViewById(R.id.okBtn);
        clearBtn = findViewById(R.id.clearBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        //text
        instructTxt = findViewById(R.id.instructTxt);
        //set initial message
        instructTxt.setBackgroundColor(Color.parseColor("#FFE4690A"));
        instructTxt.setText(R.string.dFovInstruct);

        //get list of calibrations from database (calibrations)
        calibrationList = getOneUserCaliListFromDb();

        //clear onclick
        clearBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "in onClickClick");
                if (drawing.circlePoints.size() != 0)
                {
                    drawing.clearCanvas();
                }
            }
        });

        //ok onclick
        okBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "in onBtnClick");
                boolean objects1 = false;

                if (drawing.circlePoints.size() == 2)
                {
                    drawing.nums = 2;
                    objects1 = true;
                    Log.i(TAG, "2 points selected");
                }
                if (drawing.circlePoints.size() > 1 && objects1)
                {
                    //check user has populated calibrationlist
                    if (calibrationList.size() > 0)
                    {
                        getInfoDialog();
                        //get distance between 2 selected points in pixels
                        distBetweenCaliPointsInPix = drawing.calculateCalidFovinPixels();
                    }
                }
                if (drawing.circlePoints.size() == 1)
                {
                    instructTxt.setBackgroundColor(Color.parseColor("#FFE4690A"));
                    instructTxt.setText(R.string.threepointinstruct);
                }
            }
        });

        //cancel onclick
        cancelBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showBackPressDialog();
            }
        });

        //start Quickstart
        cancelBtn.post(new Runnable() {
            @Override
            public void run() {
                presentQuickstartSequence();
            }
        });
    }


    protected void getInfoDialog()
    {
        LayoutInflater li = LayoutInflater.from(CheckCalibrationActivity.this);

        @SuppressLint("InflateParams") final View caliCheckView = li.inflate(R.layout.calicheck_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CheckCalibrationActivity.this);

        alertDialogBuilder.setView(caliCheckView);

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                Calibration selectedCali;
                //get scale bar colour
                try
                {
                    sBcolorPosition = ((Spinner) caliCheckView.findViewById(R.id.colourSbSpinner)).getSelectedItemPosition();
                } catch (NumberFormatException ex)
                {
                    Toast.makeText(CheckCalibrationActivity.this, "Please choose scalebar colour", Toast.LENGTH_SHORT).show();
                }

                //get ocular lens details
                try
                {
                    ocularLens = Integer.parseInt(((EditText) caliCheckView.findViewById(R.id.ocular_input)).getText().toString());
                } catch (
                        NumberFormatException ex)
                {
                    Log.d(TAG, "invalid ocu lens value");
                    //set to default
                    ocularLens = 1;
                }
                //get objective lens details
                try
                {
                    objectiveLens = Integer.parseInt(((EditText) caliCheckView.findViewById(R.id.objective_input)).getText().toString());
                } catch (
                        NumberFormatException ex)
                {
                    Log.d(TAG, "invalid obj lens value");
                    //set to default
                    objectiveLens = 1;
                }

                //get saved calibration details
                try
                {
                    //get selected Calibration selection (int index)
                    savedCaliPosition = ((Spinner) caliCheckView.findViewById(R.id.caliSpinner)).getSelectedItemPosition();
                    Log.d("BUGFIX", "selected Calibration selection int: " + savedCaliPosition);
                    //get the calibration object
                    selectedCali = calibrationList.get(savedCaliPosition);
                    Log.d("BUGFIX", "selected Calibration details: " + selectedCali.toString());
                    //get values from selectedCalibration object
                    savedDfov = Double.parseDouble(selectedCali.getDFov());
                    savedPPM = Double.parseDouble(selectedCali.getPixelsPerMicron());
                    savedObjLens = selectedCali.getObjectiveLens();
                    savedOcuLens = selectedCali.getOcularLens();

                    //work out new PPM & Dfov values
                    //the saved calibration dFov is in microns
                    double scaleRatio;
                    double savedDFOVinPixels = savedDfov * savedPPM;

                    //calculated the difference between the saved field of view (in the calibration settings and the new images calibration settings
                    scaleRatio = distBetweenCaliPointsInPix / savedDFOVinPixels; //ie(50/10) = 5 new image is 5 times bigger than calibration picture

                    //therefore pixels per micron in new image is ;
                    newPPM = savedPPM * scaleRatio; //if original pixels per micron was 20 it would now be 100 as new image is 5 times bigger

                    //check lens settings
                    if((savedObjLens != objectiveLens) && (savedOcuLens == ocularLens)) //so if newPPM is 100 & the savedObjLens was 10 & new objective lens is 100 new PPM would be 100 / 10 * 100 I think.. hehe
                    {
                        newPPM = (newPPM/savedObjLens)* objectiveLens;
                        Log.d(TAG, "new ppm:" + newPPM + " = " + newPPM + "/" + savedObjLens + " * " + objectiveLens + "=" + newPPM);
                    }
                    else if((savedObjLens == objectiveLens) && (savedOcuLens != ocularLens))
                    {
                        newPPM = (newPPM/savedOcuLens * ocularLens);
                    }
                    else if((savedObjLens != objectiveLens) && (savedOcuLens != ocularLens))
                    {
                        newPPM = (((newPPM/savedObjLens)* objectiveLens) /savedOcuLens) * ocularLens;
                    }
                    //otherwise leave ppm as it is as there is no lens change

                    //results
                    Log.d(TAG, "newPPM: " + newPPM + " savedCaliPPM: " + savedPPM);
                    newDFOV = distBetweenCaliPointsInPix / newPPM;
                    Log.d(TAG, "newDFOV: " + newDFOV + " savedDFOV: " + savedDfov);

                } catch (NumberFormatException ex)
                {
                    Toast.makeText(CheckCalibrationActivity.this, "Please choose scalebar colour", Toast.LENGTH_SHORT).show();
                }

                if ((ocularLens > 0) && (objectiveLens > 0) && (newPPM != 0) && newDFOV != 0)
                {
                    //TODO add saved DFov & SavedPPM to intent for upload with images, simply as if downloaded and reannotated non cropped PPM is required.
                    int finalWidth = imageholder.getMeasuredWidth();
                    Intent intent = new Intent(CheckCalibrationActivity.this, CropActivity.class);
                    //add raw file path URI string to intent
                    intent.putExtra("rawPhotoPath", imageFilePath);
                    Log.d(TAG, "rawPhotoPath: " + imageFilePath);
                    //add scale information to intent
                    intent.putExtra("confirmedPixelsPerMicron", newPPM);
                    intent.putExtra("confirmedDFOv", newDFOV);
                    //add selected int index of colour in scale bar colour array
                    intent.putExtra("scaleBarColourIndex", sBcolorPosition);
                    Log.d(TAG, "scaleBarColourIndex: " + sBcolorPosition);
                    //width in view is used to calc scale
                    intent.putExtra("imgWidthInCCView", finalWidth);
                    Log.d(TAG, "imgWidthInCCView: " + finalWidth);
                    //open edit Image Activity
                    startActivity(intent);
                    finish();
                } else
                {
                    Toast.makeText(CheckCalibrationActivity.this, "Please ensure all fields are filled", Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                //closes dialog by default
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);

        //populate saved calibration settings spinner
        Spinner spinner = alertDialog.findViewById(R.id.caliSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CheckCalibrationActivity.this,
                R.layout.jaram_spinner, populateSpinnerArray());

        spinner.setAdapter(adapter);
    }


    private List<Calibration> getOneUserCaliListFromDb()
    {
        //TODO: add async
        return AppDatabase
                .getDatabase(CheckCalibrationActivity.this)
                .getCalibrationDao()
                .getCalibrationListByUser(loggedInUser);
    }

    private String[] populateSpinnerArray()
    {
        String[] caliPopulateArray = new String[calibrationList.size()];
        int i = 0;
        //create array for spinner from database values
        for (Calibration calibration : calibrationList)
        {
            Log.d(TAG, "Filling array: " + calibration.getCaliName());
            caliPopulateArray[i] = calibration.getCaliName() + " Ocu:" + calibration.getOcularLens() + " Obj:" + calibration.getObjectiveLens();
            i++;
        }
        return caliPopulateArray;
    }

    @Override
    public void onBackPressed() {

        showBackPressDialog();
    }

    private void showBackPressDialog()
    {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Are you want to exit without saving image ?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Discard Image", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });
        builder.create().show();
    }

    private void presentQuickstartSequence() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {
                //Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
            }
        });

        sequence.setConfig(config);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(cancelBtn)
                        .setDismissText("GOT IT")
                        .setContentTextColor(Color.parseColor("#FFFFFFFF"))
                        .setMaskColour(Color.parseColor("#E6E4690A"))
                        .setContentText("Cancels calibration check")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(clearBtn)
                        .setDismissText("GOT IT")
                        .setContentTextColor(Color.parseColor("#FFFFFFFF"))
                        .setMaskColour(Color.parseColor("#E6E4690A"))
                        .setContentText("Removes the last calibration check point you have created")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(okBtn)
                        .setDismissText("GOT IT")
                        .setContentTextColor(Color.parseColor("#FFFFFFFF"))
                        .setMaskColour(Color.parseColor("#E6E4690A"))
                        .setContentText("Press OK to confirm you are ready to submit your reference object selection")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(instructTxt)
                        .setDismissText("GOT IT")
                        .setContentTextColor(Color.parseColor("#FFFFFFFF"))
                        .setMaskColour(Color.parseColor("#E6000000"))
                        .setContentText("Instructions to guide you through calibration creation")
                        .withRectangleShape()
                        .build()
        );
        sequence.start();
    }
}
