package com.jaram.jarambuild;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.jaram.jarambuild.CalibrateUtils.DrawingOnImage;
import com.jaram.jarambuild.CalibrateUtils.SurfaceImage;
import com.jaram.jarambuild.roomDb.CaliListByUserViewModel;
import com.jaram.jarambuild.roomDb.CaliListViewModel;
import com.jaram.jarambuild.roomDb.Calibration;
import com.jaram.jarambuild.utils.TinyDB;

import java.util.List;
import java.util.Objects;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class CalibrateActivity extends AppCompatActivity
{
    private String TAG = "CalibrateAct";
    FrameLayout imageholder;
    DrawingOnImage drawing;
    private String imageFilePath;

    //results of calibration
    double dFov;
    double pixPerMic;
    int ocularLens = 1;
    int objectiveLens = 1;
    String calibrationId;

    //buttons
    Button clearBtn;
    Button cancelBtn;
    Button okBtn;

    //textview
    TextView instructTxt;

    //get logged in user
    TinyDB tinydb;
    String loggedInUser;

    //db
    private CaliListViewModel caliViewModel;
    private CaliListByUserViewModel caliByUserViewModel;

    //quickstart
    private static final String SHOWCASE_ID = "cali_act";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_calibrate);

        //TODO: either build custom camera OR retain bitmap in fragment so that bitmap is not lost upon rotation during transition from camera to calibrate activity. https://stackoverflow.com/questions/25570293/image-on-imageview-lost-after-activity-is-destroyed/36360832#36360832

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
        instructTxt.setText(R.string.initialinstruct);

        //db
        caliViewModel = ViewModelProviders.of(this).get(CaliListViewModel.class);
        caliByUserViewModel = ViewModelProviders.of(this).get(CaliListByUserViewModel.class);

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
                    instructTxt.setBackgroundColor(Color.parseColor("#FFE4690A"));
                    instructTxt.setText(R.string.initialinstruct);
                } else if (drawing.circlePoints.size() > 2 && drawing.circlePoints.size() <= 4)
                {
                    instructTxt.setBackgroundColor(Color.parseColor("#FFB12B21"));
                    instructTxt.setText(R.string.refObjInstruct);
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
                    objects1 = true;
                    instructTxt.setBackgroundColor(Color.parseColor("#FFB12B21"));
                    instructTxt.setText(R.string.refObjInstruct);
                }
                if (drawing.circlePoints.size() == 4)
                {
                    objects1 = true;
                }
                if (drawing.circlePoints.size() > 3 && objects1)
                {
                    getInfoDialog();
                } else if (drawing.circlePoints.size() == 3)
                {
                    instructTxt.setBackgroundColor(Color.parseColor("#FFB12B21"));
                    instructTxt.setText(R.string.threepointinstruct);
                } else if (drawing.circlePoints.size() == 1)
                {
                    instructTxt.setBackgroundColor(Color.parseColor("#FFE4690A"));
                    instructTxt.setText(R.string.onepointinstruct);
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
        clearBtn.post(new Runnable() {
            @Override
            public void run() {
                presentQuickstartSequence();
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
            {  //validation
                AwesomeValidation awesomeValidation = new AwesomeValidation(BASIC);
                awesomeValidation.addValidation(CalibrateActivity.this, R.id.settingName_input, "/[\\'\\/\\!@#\\$%\\^&\\*\\(\\)_\\-\\+=\\{\\}\\[\\]\\|;:\"\\<\\>,\\.\\?\\\\\\]/", R.string.nameerror);
                int inputUnit = ((Spinner) promptsView.findViewById(R.id.input_unit_chooser)).getSelectedItemPosition();

                try
                {
                    double reference = Double.parseDouble(((EditText) promptsView.findViewById(R.id.reference_input)).getText().toString());
                    //result is dFOV in microns
                    dFov = drawing.calculate(reference, inputUnit, 1);
                    //pixPerMic is pixels per micron
                    pixPerMic = drawing.calculatePixelsPerMicron(reference, inputUnit);

                    //temp toast for debugging
                    //Toast.makeText(CalibrateActivity.this,
                            //"dFOV = " + dFov + " microns"
                                    //+ " pixel per micron = " + pixPerMic, Toast.LENGTH_LONG).show();

                } catch (NumberFormatException ex)
                {
                    Log.d(TAG, "Failed to get data from spinner");
                }

                //get ocular lens details
                try
                {
                    ocularLens = Integer.parseInt(((EditText) promptsView.findViewById(R.id.ocular_input)).getText().toString());
                } catch (
                        NumberFormatException ex)
                {
                    Log.d(TAG, "invalid ocular lens input");
                    //set to default
                    ocularLens = 1;
                }
                //get objective lens details
                try
                {
                    objectiveLens = Integer.parseInt(((EditText) promptsView.findViewById(R.id.objective_input)).getText().toString());
                } catch (
                        NumberFormatException ex)
                {
                    Log.d(TAG, "invalid objective lens input");
                    //set to default
                    objectiveLens = 1;
                }

                calibrationId = ((EditText) promptsView.findViewById(R.id.settingName_input)).getText().toString().trim();

                if (calibrationId.equals("")|| objectiveLens < 1 || ocularLens < 1)
                {
                    Log.d(TAG, "in valid data");
                    Toast.makeText(CalibrateActivity.this,"Please enter valid data", Toast.LENGTH_LONG).show();
                } else
                {
                    if(awesomeValidation.validate())
                    {
                        saveCaliToDatabase(calibrationId, dFov, pixPerMic, ocularLens, objectiveLens);
                        finish();
                        Intent homeIntent = new Intent(CalibrateActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                    }
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
    }

    private void saveCaliToDatabase(String calibrationId, double dFov, double pixPerMic, int ocularLens, int objectiveLens)
    {
        Log.d(TAG, "Calibration data prior to insert: " + calibrationId + " " + String.valueOf(dFov) + " " + String.valueOf(pixPerMic) + " " + objectiveLens + " " + ocularLens + " " + loggedInUser);
        caliViewModel.addOneCalibration(new Calibration(calibrationId, String.valueOf(dFov), String.valueOf(pixPerMic), objectiveLens, ocularLens, loggedInUser));
        Log.d(TAG, "Calibration saved to dataBase");
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
    private void getOneUserCaliListFromDb()
    {
        caliByUserViewModel.getLiveCalibrationListByUser(loggedInUser).observe(this, new Observer<List<Calibration>>()
        {
            @Override
            public void onChanged(@Nullable List<Calibration> calibrations)
            {
                if (calibrations != null)
                {
                    for (Calibration calibration : calibrations)
                    {
                        Log.d(TAG, "in loop" + calibration.toString());
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {

        showBackPressDialog();
    }

    private void showBackPressDialog()
    {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Are you want to exit without saving calibration ?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Discard Calibration", new DialogInterface.OnClickListener()
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
                        .setTarget(clearBtn)
                        .setDismissText("GOT IT")
                        .setContentTextColor(Color.parseColor("#FFFFFFFF"))
                        .setMaskColour(Color.parseColor("#E6E4690A"))
                        .setContentText("Removes the last calibration point you have created")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(cancelBtn)
                        .setDismissText("GOT IT")
                        .setContentTextColor(Color.parseColor("#FFFFFFFF"))
                        .setMaskColour(Color.parseColor("#E6E4690A"))
                        .setContentText("Cancels calibration setting creation")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(okBtn)
                        .setDismissText("GOT IT")
                        .setContentTextColor(Color.parseColor("#FFFFFFFF"))
                        .setMaskColour(Color.parseColor("#E6E4690A"))
                        .setContentText("Press OK to confirm you are ready to submit your reference measurement and object selections")
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