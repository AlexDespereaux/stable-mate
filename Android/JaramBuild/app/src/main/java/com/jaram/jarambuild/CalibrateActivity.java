package com.jaram.jarambuild;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
//zoom
import com.jaram.jarambuild.CalibrateUtils.DrawingOnImage;
import com.jaram.jarambuild.CalibrateUtils.SurfaceImage;
import com.jaram.jarambuild.roomDb.AppDatabase;
import com.jaram.jarambuild.roomDb.CaliListViewModel;
import com.jaram.jarambuild.roomDb.Calibration;
import com.jaram.jarambuild.utils.TinyDB;
import com.otaliastudios.zoom.ZoomImageView;
import com.otaliastudios.zoom.ZoomLayout;
import com.otaliastudios.zoom.ZoomLogger;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;


public class CalibrateActivity extends AppCompatActivity
{
    private String TAG = "CalibrateAct";
    FrameLayout imageholder;
    DrawingOnImage drawing;
    private String imageFilePath;

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
    private CaliListViewModel caliViewModel;


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
                if (drawing.circlePoints.size() > 0 && drawing.circlePoints.size() <= 2)
                {
                    Log.d(TAG, "Do This");
                } else if (drawing.circlePoints.size() > 2 && drawing.circlePoints.size() <= 4)
                {
                    Log.d(TAG, "Do This");
                } else if (drawing.circlePoints.size() > 4 && drawing.circlePoints.size() <= 6)
                {
                    Log.d(TAG, "Do This");
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
                    drawing.nums = 4;
                    //objects1 = true;
                    Log.i("size 2 select  1", "in size 2 sleted 1");
                }
                if (drawing.circlePoints.size() == 4)
                {
                    Log.i("in ize 4 select  2", "in size 2 sleted 2");
                    objects1 = true;
                }
                if (drawing.circlePoints.size() > 3 && objects1)
                {
                    getInfoDialog();
                } else if (drawing.circlePoints.size() == 3)
                {
                    Toast.makeText(CalibrateActivity.this, "Please draw all dots first", Toast.LENGTH_SHORT).show();
                } else if (drawing.circlePoints.size() == 1)
                {
                    Toast.makeText(CalibrateActivity.this, "Please draw all dots first", Toast.LENGTH_SHORT).show();
                }

                if (drawing.circlePoints.size() == 2)
                {
                    Log.d(TAG, "Do This");
                } else if (drawing.circlePoints.size() == 4 && objects1 == false)
                {
                    Log.d(TAG, "Do This");
                } else if (drawing.circlePoints.size() == 4 && objects1 == true)
                {
                    Log.d(TAG, "Do This");
                }
            }
        });

    }

    protected void getInfoDialog()
    {
        //LayoutInflater li = (LayoutInflater) .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater li = LayoutInflater.from(CalibrateActivity.this);

        final View promptsView = li.inflate(R.layout.unit_prompt_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CalibrateActivity.this);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                int inputUnit = ((Spinner) promptsView.findViewById(R.id.input_unit_chooser)).getSelectedItemPosition();

                try
                {
                    double reference = Double.parseDouble(((EditText) promptsView.findViewById(R.id.reference_input)).getText().toString());
                    //result is dFOV in microns
                    dFov = drawing.calculate(reference, inputUnit, 1);
                    //pixPerMic is pixels per micron
                    pixPerMic = drawing.calculatePixelsPerMicron(reference, inputUnit);

                    //temp toast for debugging
                    Toast.makeText(CalibrateActivity.this,
                            "dFOV = " + dFov + " microns"
                                    + " pixel per micron = " + pixPerMic, Toast.LENGTH_LONG).show();

                } catch (NumberFormatException ex)
                {
                    Toast.makeText(CalibrateActivity.this, "Please enter valid reference", Toast.LENGTH_SHORT).show();
                }

                //get ocular lens details
                try
                {
                    ocularLens = Integer.parseInt(((EditText) promptsView.findViewById(R.id.ocular_input)).getText().toString());
                } catch (
                        NumberFormatException ex)
                {
                    Toast.makeText(CalibrateActivity.this, "Please enter valid ocular lens", Toast.LENGTH_SHORT).show();
                }
                //get objective lens details
                try
                {
                    objectiveLens = Integer.parseInt(((EditText) promptsView.findViewById(R.id.objective_input)).getText().toString());
                } catch (
                        NumberFormatException ex)
                {
                    Toast.makeText(CalibrateActivity.this, "Please enter valid objective lens", Toast.LENGTH_SHORT).show();
                }

                calibrationId = ((EditText) promptsView.findViewById(R.id.settingName_input)).getText().toString().trim();

                if (calibrationId.equals(""))
                {
                    Toast.makeText(CalibrateActivity.this, "Please enter Calibration Id", Toast.LENGTH_SHORT).show();
                } else
                {
                    saveCaliToDatabase(calibrationId, dFov, pixPerMic, ocularLens, objectiveLens);
                    finish();
                    Intent homeIntent = new Intent(CalibrateActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
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
        //final Spinner mSpinner = (Spinner) promptsView.findViewById(R.id.input_unit_chooser);

        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    private void saveCaliToDatabase(String calibrationId, double dFov, double pixPerMic, int ocularLens, int objectiveLens)
    {
        //TODO resolve cali
        Log.d(TAG, "Calibration: " + calibrationId + " " + String.valueOf(dFov) + " " + String.valueOf(pixPerMic) + " " + objectiveLens + " " + ocularLens + " " + loggedInUser);
        //Calibration inCali = new Calibration(calibrationId, String.valueOf(dFov), String.valueOf(pixPerMic), objectiveLens, ocularLens, "me@me.com");
        //Log.d(TAG, "Calibration inCali generated" + inCali.toString());
        //caliViewModel.addOneCalibration(inCali);
        caliViewModel.addOneCalibration(new Calibration(calibrationId, String.valueOf(dFov), String.valueOf(pixPerMic), objectiveLens, ocularLens, loggedInUser));
        Log.d(TAG, "Calibration saved to dataBase");
        //debugging
        getOneUserCaliListFromDb(loggedInUser);
    }

    //for debugging
    private void getAllCalibrationsFromDb()
    {

        caliViewModel.getCalibrationList().observe(this, new Observer<List<Calibration>>()
        {
            @Override
            public void onChanged(@Nullable List<Calibration> calibrations)
            {
                if(calibrations != null)
                {
                        for (Calibration calibration : calibrations)
                        {
                            Log.d(TAG, "in loop" + calibration.toString());
                        }
                }
            }
        });
    }

    //for debugging
    private void getOneUserCaliListFromDb(String loggedInUser)
    {
        caliViewModel.getCalibrationListByUser(loggedInUser).observe(this, new Observer<List<Calibration>>()
        {
            @Override
            public void onChanged(@Nullable List<Calibration> calibrations)
            {
                if(calibrations != null)
                {
                    for (Calibration calibration : calibrations)
                    {
                        Log.d(TAG, "in loop" + calibration.toString());
                    }
                }
            }
        });
    }
}