package com.jaram.jarambuild;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.jaram.jarambuild.utils.FileUtils.saveBitmap;

public class CropActivity extends AppCompatActivity implements View.OnClickListener
{
    //member variables
    CropImageView cropImageView;
    private String TAG = "CropAct";
    private String rawImageFilePath;
    private String croppedFilePath;
    private int imageWidthInCCView;
    private int aspectSpinnerIndex = 1;  //default 3:4
    private int originalImageWidth;
    private int originalImageHeight;
    double croppedImgPixelPerMicron;

    //buttons
    private Button ratioBtn;
    private Button cropBtn;
    private Button cancelBtn;

    //scalebar
    double dFov;
    double pixelsPerMicron;
    int scaleBarColourIndex;

    //quickstart
    private static final String SHOWCASE_ID = "crop_act";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crop);

        //hide action bar
        android.support.v7.app.ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null)
        {
            myActionBar.hide();
            Log.d(TAG, "ActionBar Hidden");
        }

        //views
        cropImageView = findViewById(R.id.cropImageView);
        ratioBtn = (Button) findViewById(R.id.ratioBtn);
        cropBtn = (Button) findViewById(R.id.cropBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);

        //register listeners
        ratioBtn.setOnClickListener(this);
        cropBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        // subscribe to async event using cropImageView.setOnCropImageCompleteListener(listener)
        cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                Bitmap croppedBitmap = result.getBitmap();
                float[] cropPoints = result.getCropPoints();
                //returns  4 points (x0,y0,x1,y1,x2,y2,x3,y3) of cropped area boundaries
                /*  -- required so I can establish ratio between full size cropped image and scaled cropped image
                        cropWindowRect.left,
                        cropWindowRect.top,
                        cropWindowRect.right,
                        cropWindowRect.top,
                        cropWindowRect.right,
                        cropWindowRect.bottom,
                        cropWindowRect.left,
                        cropWindowRect.bottom  */
                float cropWidthPriorToScaling = cropPoints[2] - cropPoints[0];
                float cropHeightPriorToScaling = cropPoints[7] -  cropPoints[1];
                Log.d(TAG, "cropped image width prior to scaling = " + cropWidthPriorToScaling);
                Log.d(TAG, "cropped image height prior to scaling = " + cropHeightPriorToScaling);
                //calculate scale ratio between raw from camera and scaled image
                //-1.- firstly calculate how many pixel % the cropped section is compared to the original image width
                float croppedImgPerc = (cropWidthPriorToScaling/originalImageWidth)*100;
                Log.d(TAG, "Cropped Image percentage: " + croppedImgPerc);
                if (croppedBitmap != null)
                {
                    Log.d(TAG, "bitmap not null, width: " + croppedBitmap.getWidth() + " height: " + croppedBitmap.getHeight());
                    //-2.- Then use the % to calculate the uncropped image width given the cropped section is imageWidthInCCView wide
                    //(ie so cropped image after sclaing = iamgeWidthInCCView, so if that is 1080, and the croppedImagePercetage = 50% the original image would be 2160
                    float extrapolatedImageWidth = ((imageWidthInCCView/croppedImgPerc)*100);
                    //-3. Then calculate the ratio between the extrapolated image width and the image width used to calculate pixels per micron (imageWidthinCCView)
                    //(ie so as per example above if extrapolated image width = 2160 and width in calibration mode was 1080 (imageWidthInCCView) the ratio would be 2
                    float differenceRatio = extrapolatedImageWidth/imageWidthInCCView;
                    //-4. Then calculated PPM of cropped image
                    //(ie so continuing from above if original pixel per micron was 10, and and width in calibration mode was 1080 (imageWidthInCCView)
                    // and the new extrapolatedImageWidth is 2060 new PPM would be 20 pixel per micron
                    croppedImgPixelPerMicron = pixelsPerMicron* differenceRatio;
                    croppedFilePath = saveBitmap("tempCropImg", croppedBitmap);
                    goToEditViaIntent();
                }
                else
                {
                    Log.d(TAG, "bitmap is null" + result.getError());
                }
            }
        });

        //Get data from intent
        getInfoFromIntent();

        //decode bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(rawImageFilePath);

        /* *****NOTE ON SCALING & RESOLUTION******
        //these measurements are required as the bitmap from the camera is very large so for the sake of reducing storage / upload size the image in the edit view
        //is scaled down to the resolution of the screen, which is most modern phones is still quite large. And for older phones, even if the resolution is not as high
        //neither are their processors power so the scaling dependant upon screen resolution ensures all phones can efficiently process images as required. To ensure maximum
        // valuable data is retained, the cropping activity actually crops the originally sized image. ie if the raw image was 2000 wide, and the phone resolution was
        // 1000 wide , the image is cropped at the 2000 resolution, meaning is the cropped image was 30% less wide than the original (so 1400 wide). This is then scaled
        // down to the screen resolution of 1000 wide. However if we scaled the visable onscreen image of 1000 wide down 10% it would be 700 wide and would be scaled UP
        // 1000 wide for output. And scaling UP obviously doesn't improve quality as the data from the larger image is already lost.
        */
        originalImageWidth = bitmap.getWidth();
        Log.d(TAG, "Original Image Width:" + originalImageWidth);
        originalImageHeight = bitmap.getHeight();
        Log.d(TAG, "Original Image Height:" + originalImageHeight);

        //flip bitmap if wider than tall.. fix for samsung exif data
        if (originalImageWidth > originalImageHeight)
        {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            matrix.preScale(1, 1);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        //Set image in view & Cropping settings
        cropImageView.setImageBitmap(bitmap);
        cropImageView.setAutoZoomEnabled(true);
        cropImageView.setScaleType(CropImageView.ScaleType.FIT_CENTER);
        cropImageView.setAspectRatio(3, 4);
        cropImageView.setFixedAspectRatio(true);
        cropImageView.setShowProgressBar(true);

        //start Quickstart
        cancelBtn.post(new Runnable() {
            @Override
            public void run() {
                presentQuickstartSequence();
            }
        });
    }

    @Override
    public void onClick(View view)
    {
        Log.d(TAG, "in onclick");
        switch (view.getId())
        {
            case R.id.cropBtn:
                if(aspectSpinnerIndex == 0) // 1:1 aspect (set the output size of the cropped image to the size of the image view in the edit window)
                {
                    cropImageView.getCroppedImageAsync(imageWidthInCCView, imageWidthInCCView);
                    Log.d(TAG, " requested Crop width = " + imageWidthInCCView);
                    Log.d(TAG, " requested Crop height = " + imageWidthInCCView);
                }
                else  // 3:4 aspect
                {
                    cropImageView.getCroppedImageAsync(imageWidthInCCView, ((imageWidthInCCView/3)*4), CropImageView.RequestSizeOptions.RESIZE_EXACT );
                    Log.d(TAG, " requested Crop width = " + imageWidthInCCView);
                    Log.d(TAG, " requested Crop height = " + ((imageWidthInCCView/3)*4));
                }
                finish();
                break;
            case R.id.cancelBtn:
                Log.d(TAG, "clicked cancel");
                showBackPressDialog();
                break;
            case R.id.ratioBtn:
                Log.d(TAG, "clicked ratio");
                cropImageView.setAspectRatio(3, 4);
                ratioSelectorDialog();
                break;
        }
    }

    private void goToEditViaIntent()
    {
        Intent intent = new Intent(this, EditImageActivity.class);
        //add raw file path URI string to intent
        intent.putExtra("rawPhotoPath", rawImageFilePath);
        //cropped photo path
        intent.putExtra("croppedPhotoPath", croppedFilePath);
        //add scale information to intent (scale is pertinant to the raw image NOT cropped)
        intent.putExtra("confirmedPixelsPerMicron", pixelsPerMicron); // of raw image scaled to a width of imageWidthInCCView
        intent.putExtra("confirmedDFOv", dFov); // of raw image scaled to a width of imageWidthInCCView
        //add selected int index of colour in scale bar colour array
        intent.putExtra("scaleBarColourIndex", scaleBarColourIndex);
        //cropped image pix per micron
        intent.putExtra("croppedPixelsPerMicron", croppedImgPixelPerMicron);
        //open edit Image Activity
        startActivity(intent);
    }

    private void getInfoFromIntent()
    {
        //Get bitmap uri from intent
        rawImageFilePath = Objects.requireNonNull(getIntent().getExtras()).getString("rawPhotoPath");
        Log.d(TAG, "rawImageFilePath: " + rawImageFilePath);
        //Get dFov from intent
        dFov = Objects.requireNonNull(getIntent().getExtras()).getDouble("confirmedDFOv");
        Log.d(TAG, "dFov from intent: " + dFov + " microns");
        //Get Pixels per micron from intent
        pixelsPerMicron = Objects.requireNonNull(getIntent().getExtras()).getDouble("confirmedPixelsPerMicron");
        Log.d(TAG, "pixels per micron from intent: " + pixelsPerMicron);
        scaleBarColourIndex = Objects.requireNonNull(getIntent().getExtras()).getInt("scaleBarColourIndex");
        Log.d(TAG, "scaleBarColourIndex from intent: " + scaleBarColourIndex);
        //width of image used to calibrate
        imageWidthInCCView = Objects.requireNonNull(getIntent().getExtras()).getInt("imgWidthInCCView");

    }

    private void ratioSelectorDialog()
    {
        LayoutInflater li = LayoutInflater.from(CropActivity.this);

        @SuppressLint("InflateParams") final View getRatioView = li.inflate(R.layout.ratio_select_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CropActivity.this);

        alertDialogBuilder.setView(getRatioView);

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                try
                {
                    aspectSpinnerIndex = ((Spinner) getRatioView.findViewById(R.id.ratioSpinner)).getSelectedItemPosition();
                    switch (aspectSpinnerIndex)
                    {
                        case 0:
                            cropImageView.setAspectRatio(1, 1);
                            break;
                        case 1:
                            cropImageView.setAspectRatio(3, 4);
                            break;
                    }

                } catch (NumberFormatException ex)
                {
                    cropImageView.setAspectRatio(3, 4);
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

    @Override
    public void onBackPressed()
    {

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
                        .setMaskColour(Color.parseColor("#E6000000"))
                        .setContentText("Exits image cropping and returns you to home screen")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(cropBtn)
                        .setDismissText("GOT IT")
                        .setContentTextColor(Color.parseColor("#FFFFFFFF"))
                        .setMaskColour(Color.parseColor("#E6000000"))
                        .setContentText("Crops the image and progresses to Annotation screen")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(ratioBtn)
                        .setDismissText("GOT IT")
                        .setContentTextColor(Color.parseColor("#FFFFFFFF"))
                        .setMaskColour(Color.parseColor("#E6000000"))
                        .setContentText("Opens a dialog to let you choose desired image aspect (ie square or rectangle)")
                        .withRectangleShape()
                        .build()
        );
        sequence.start();
    }
}
