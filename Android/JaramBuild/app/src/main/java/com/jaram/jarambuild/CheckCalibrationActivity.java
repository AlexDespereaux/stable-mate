package com.jaram.jarambuild;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.jaram.jarambuild.CalibrateUtils.DrawingOnImage;
import com.jaram.jarambuild.CalibrateUtils.SurfaceImage;
import com.jaram.jarambuild.roomDb.AppDatabase;
import com.jaram.jarambuild.roomDb.Calibration;
import com.jaram.jarambuild.utils.TinyDB;
import java.util.List;
import java.util.Objects;

public class CheckCalibrationActivity extends AppCompatActivity
{
    private String TAG = "CheckCaliAct";
    FrameLayout imageholder;
    DrawingOnImage drawing;
    private String imageFilePath;
    private double confirmedPixelsPerMicron;

    //results of calibration
    double dFov;
    double pixPerMic;
    int ocularLens;
    int objectiveLens;
    String calibrationId;

    //buttons
    Button clearBtn;
    Button okBtn;

    //textview
    TextView instructTxt;

    //get logged in user
    TinyDB tinydb;
    String loggedInUser;

    //db
    List<Calibration> calibrationList;

    //data to pass to edit activity
    int sBcolorPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_calibrate);

        imageholder = (FrameLayout) findViewById(R.id.frame);

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
        okBtn = (Button) findViewById(R.id.okBtn);
        clearBtn = (Button) findViewById(R.id.clearBtn);

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
                    if(calibrationList.size()>0)
                    {
                        getInfoDialog();
                    }
                    else
                    {
                        //TODO make dialog with button to calibration activity
                        Toast.makeText(CheckCalibrationActivity.this, "Please save calibrations prior to taking photo", Toast.LENGTH_LONG).show();
                    }
                }
                if (drawing.circlePoints.size() == 1)
                {
                    Toast.makeText(CheckCalibrationActivity.this, "Please draw all dots first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void getInfoDialog()
    {
        LayoutInflater li = LayoutInflater.from(CheckCalibrationActivity.this);

        final View caliCheckView = li.inflate(R.layout.calicheck_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CheckCalibrationActivity.this);

        alertDialogBuilder.setView(caliCheckView);

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                //get scale bar colour
                sBcolorPosition = ((Spinner) caliCheckView.findViewById(R.id.colourSbSpinner)).getSelectedItemPosition();
                try
                {
                    Toast.makeText(CheckCalibrationActivity.this, "placeholder in cali check dialog", Toast.LENGTH_SHORT).show();

                } catch (NumberFormatException ex)
                {
                    Toast.makeText(CheckCalibrationActivity.this, "Please choose scalebar colour", Toast.LENGTH_SHORT).show();
                }

                //get saved calibration details
                int savedCaliPosition = ((Spinner) caliCheckView.findViewById(R.id.caliSpinner)).getSelectedItemPosition();
                try
                {
                    Calibration selectedCali = calibrationList.get(savedCaliPosition);
                    double savedDfov = Double.parseDouble(selectedCali.getDFov());
                    double savedPPM = Double.parseDouble(selectedCali.getPixelsPerMicron());
                    int savedObjLens = selectedCali.getObjectiveLens();
                    int savedOcuLens = selectedCali.getOcularLens();
                    checkCalibrationMath(savedDfov, savedPPM, savedObjLens, savedOcuLens);

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
                }
                //get objective lens details
                try
                {
                    objectiveLens = Integer.parseInt(((EditText) caliCheckView.findViewById(R.id.objective_input)).getText().toString());
                } catch (
                        NumberFormatException ex)
                {
                    Log.d(TAG, "invalid obj lens value");
                }

                if ((ocularLens > 0) || (objectiveLens > 0))
                {
                    Intent intent = new Intent(CheckCalibrationActivity.this, EditImageActivity.class);
                    //add raw file path URI string to intent
                    intent.putExtra("rawPhotoPath", imageFilePath);
                    intent.putExtra("confirmedPixelsPerMicron", confirmedPixelsPerMicron);
                    Log.d(TAG, "rawPhotoPath: " + imageFilePath);
                    //open edit Image Activity
                    startActivity(intent);
                } else
                {
                    Toast.makeText(CheckCalibrationActivity.this, "Please enter lens values", Toast.LENGTH_SHORT).show();
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
        Spinner spinner = (Spinner) alertDialog.findViewById(R.id.caliSpinner);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(CheckCalibrationActivity.this,
                android.R.layout.simple_spinner_item,populateSpinnerArray());

        spinner.setAdapter(adapter);
    }

    private void checkCalibrationMath(double savedDfov, double savedPPM, int savedObjLens, int savedOcuLens)
    {
        //do stuff with stuff
        //aka get pixels in current dFov and compare to saved dFov, check lenses and compute scale
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
            caliPopulateArray[i] = calibration.getCaliName();
            i++;
        }
        return caliPopulateArray;
    }
}
