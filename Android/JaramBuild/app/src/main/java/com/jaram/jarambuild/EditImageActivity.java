package com.jaram.jarambuild;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.jaram.jarambuild.imageUtils.PropertiesBSFragment;
import com.jaram.jarambuild.imageUtils.StickerBSFragment;
import com.jaram.jarambuild.imageUtils.TextEditorDialogFragment;
import com.jaram.jarambuild.utils.AddStickerEvent;
import com.jaram.jarambuild.utils.TinyDB;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;

public class EditImageActivity extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        StickerBSFragment.StickerListener
{
    public static final String EXTRA_IMAGE_PATHS = "extra_image_paths";
    // request codes
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    //photo editor variables
    private PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private String imageFilePath;
    private String editedImageFilePath = "";
    private String croppedFilePath;
    //log
    private static final String TAG = "EditActivity";
    //list of stickerList index's used in imageviews
    private ArrayList<String> sliList;
    //shared preferances
    TinyDB tinydb;
    //scalebar
    double dFov;
    double pixelsPerMicron;
    int scaleBarColourIndex;
    double croppedImgPixelPerMicron;
    //image size
    double mvHeight;
    double mvWidth;

    //function text
    private TextView functionText;

    public static void launch(Context context, ArrayList<String> imagesPath)
    {
        Intent starter = new Intent(context, EditImageActivity.class);
        starter.putExtra(EXTRA_IMAGE_PATHS, imagesPath);
        context.startActivity(starter);
    }

    public static void launch(Context context, String imagePath)
    {
        ArrayList<String> imagePaths = new ArrayList<>();
        imagePaths.add(imagePath);
        launch(context, imagePaths);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_edit_image);
        initViews();

        mPropertiesBSFragment = new PropertiesBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

        //Get raw Image from intent
        imageFilePath = Objects.requireNonNull(getIntent().getExtras()).getString("rawPhotoPath");
        Log.d(TAG, "imageFilePath: " + imageFilePath);
        //Get cropped Image from Intent
        croppedFilePath = Objects.requireNonNull(getIntent().getExtras()).getString("croppedPhotoPath");
        Log.d(TAG, "croppedFilePath: " + croppedFilePath);
        //Get dFov from intent
        dFov = Objects.requireNonNull(getIntent().getExtras()).getDouble("confirmedDFOv"); // of raw image scaled to a width of imageWidthInCCView
        Log.d(TAG, "dFov from intent: " + dFov + " microns"); // of raw image scaled to a width of imageWidthInCCView
        //Get Pixels per micron from intent
        pixelsPerMicron = Objects.requireNonNull(getIntent().getExtras()).getDouble("confirmedPixelsPerMicron"); // of raw image scaled to a width of imageWidthInCCView
        Log.d(TAG, "pixels per micron from intent: " + pixelsPerMicron); // of raw image scaled to a width of imageWidthInCCView
        scaleBarColourIndex = Objects.requireNonNull(getIntent().getExtras()).getInt("scaleBarColourIndex");
        Log.d(TAG, "scaleBarColourIndex from intent: " + scaleBarColourIndex);
        //Get Pixels per micron of cropped image from intent
        croppedImgPixelPerMicron = Objects.requireNonNull(getIntent().getExtras()).getDouble("croppedPixelsPerMicron");

        //get bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(croppedFilePath);

        //set bitmap to editor view
        mPhotoEditorView.getSource().setImageBitmap(bitmap);

        //hide action bar
        android.support.v7.app.ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null)
        {
            myActionBar.hide();
            Log.d(TAG, "ActionBar Hidden");
        }

        //function text
        functionText = findViewById(R.id.functionText);

        //instantiate sliList
        sliList = new ArrayList<String>();

        //shared preferences helper
        tinydb = new TinyDB(this);

        //listener for image size
        mPhotoEditorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                mPhotoEditorView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mvHeight = mPhotoEditorView.getHeight(); //height is ready
                mvWidth = mPhotoEditorView.getWidth(); //width is ready
                Log.d(TAG, "mviewHeight:" + mvHeight + " " + "mviewWidth:" + mvWidth);

                //insert scalebar
                selectSBColour(scaleBarColourIndex);
            }
        });
    }


    // This method will be called when a AddStickerEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddStickerEvent(AddStickerEvent event)
    {
        Log.d(TAG, "Sticker Index from EventBus AddStickerEvent " + event.message);
        sliList.add(event.message);
    }

    private void initViews()
    {
        Button imgPencil;
        Button imgEraser;
        Button imgUndo;
        Button imgText;
        Button imgSticker;
        Button imgSave;

        Button imgClose;

        mPhotoEditorView = findViewById(R.id.photoEditorView);

        imgSticker = findViewById(R.id.imgSticker);
        imgSticker.setOnClickListener(this);

        imgPencil = findViewById(R.id.imgPencil);
        imgPencil.setOnClickListener(this);

        imgText = findViewById(R.id.imgText);
        imgText.setOnClickListener(this);

        imgEraser = findViewById(R.id.btnEraser);
        imgEraser.setOnClickListener(this);

        imgUndo = findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(this);

        imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop()
    {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode)
    {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor()
        {
            @Override
            public void onDone(String inputText, int colorCode)
            {
                mPhotoEditor.editText(rootView, inputText, colorCode);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews)
    {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
        //stickers are added to array list in addImage() method and as such do not need to be added here
        //this listener is called BEFORE image is added and therefore cannot be used to trigger
        //image index addition to arraylist.
        //the notSticker place holder must be added into the array list so that in the instance the last view
        //added was not an image, it will be removed from the arraylist in the onRemoveListener, otherwise ONLY
        // image views would be removed from arraylist. Which would be unfortunate hehe..
        if (!viewType.equals(ViewType.IMAGE))
        {
            sliList.add("notSticker");
            Log.d(TAG, "Non image view descriptor added to arraylist");
        }
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews)
    {
        Log.d(TAG, "onRemoveViewListener() called with: numberOfAddedViews = [" + numberOfAddedViews + "]");
        //removes last added view string in arraylist
        if (sliList.size() > 0)
        {
            sliList.remove(sliList.size() - 1);
            Log.d(TAG, "View removed from arraylist");
        }
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType)
    {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType)
    {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.imgPencil:
                mPhotoEditor.setBrushDrawingMode(true);
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                functionText.setText(R.string.functxtdrawmode);
                break;
            case R.id.btnEraser:
                mPhotoEditor.brushEraser();
                functionText.setText(R.string.funtxtdraw);
                break;
            case R.id.imgText:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor()
                {
                    @Override
                    public void onDone(String inputText, int colorCode)
                    {
                        mPhotoEditor.addText(inputText, colorCode);
                    }
                });
                functionText.setText(R.string.functxttext);
                break;

            case R.id.imgUndo:
                mPhotoEditor.undo();
                functionText.setText(R.string.functxtundo);
                break;

            case R.id.imgSave:
                saveImage();
                break;

            case R.id.imgClose:
                if (!mPhotoEditor.isCacheEmpty())
                {
                    showSaveDialog();
                } else
                {
                    finish();
                }
                break;

            case R.id.imgSticker:
                mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
                functionText.setText(R.string.functxtsymbol);
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void saveImage()
    {
        //add sticker Index List to shared pref - not using intent (extras) as I want to ensure list is available should users
        //use back key.
        tinydb.putListString("stickerIndexAL", sliList);
        Log.d(TAG, "sliList added to SP");

        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
                mPhotoEditor.saveAsFile(photoFile.getAbsolutePath(), new PhotoEditor.OnSaveListener()
                {
                    @Override
                    public void onSuccess(@NonNull String imagePath)
                    {
                        showSnackbar("Image Saved Successfully");
                        // working mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                        Intent intent = new Intent(EditImageActivity.this, AddDataActivity.class);
                        intent.putExtra("editedImageUri", editedImageFilePath);
                        intent.putExtra("rawImageUri", imageFilePath);
                        intent.putExtra("dFov", dFov);
                        intent.putExtra("pixelsPerMicron", pixelsPerMicron);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception)
                    {
                        showSnackbar("Failed to save Image");
                    }
                });
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onColorChanged(int colorCode)
    {
        mPhotoEditor.setBrushColor(colorCode);
    }

    @Override
    public void onOpacityChanged(int opacity)
    {
        mPhotoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushSizeChanged(int brushSize)
    {
        mPhotoEditor.setBrushSize(brushSize);
    }

    @Override
    public void onStickerClick(Bitmap bitmap)
    {
        mPhotoEditor.addImage(bitmap);
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission)
    {
        if (isGranted)
        {
            saveImage();
        }
    }

    private void showSaveDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you want to exit without saving image ?");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                saveImage();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Discard", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });
        builder.create().show();
    }

    private void selectSBColour(int pos)
    {
        Bitmap bm;
        String colour;
        //choose bitmap colour (using position in sbcolour string array)
        switch (pos)
        {
            case 0:
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.scalegrey);
                colour = "#858585";
                insertSBandText(bm, colour);
                Log.d(TAG, "Grey Scale Bar colour selected");
                break;
            case 1:
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.scaleblack);
                colour = "#000000";
                insertSBandText(bm, colour);
                Log.d(TAG, "Black Scale Bar colour selected");
                break;
            case 2:
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.scalewhite);
                colour = "#ffffff";
                insertSBandText(bm, colour);
                Log.d(TAG, "White Scale Bar colour selected");
                break;
        }
    }

    private void insertSBandText(Bitmap bm, String textColour)
    {
        //variable to store pixel width of scalebar
        double tempPixels = croppedImgPixelPerMicron;

        //get optimal scale bar width range in pixels
        double maxSbWidthInDouble = mvWidth / 4; // 1/4 image width
        double minSbWidthInDouble = mvWidth / 6; // 1/6 image width

        //get scale bar height (1/40 image height)
        double sbHeightInDouble = mvHeight / 40;
        Log.d(TAG, "Max sbW: " + maxSbWidthInDouble + " Min sbW: " + minSbWidthInDouble);
        Log.d(TAG, "Max sbH: " + sbHeightInDouble);

        //round to int
        int maxSbWidth = (int) Math.round(maxSbWidthInDouble);
        int minSbWidth = (int) Math.round(minSbWidthInDouble);
        int h = (int) Math.round(sbHeightInDouble);

        //calculate scale bar width
        double resultSbSizeinMicrons = 1;
        while (tempPixels < minSbWidth)
        {
            //calculate the value of the scale bar
            resultSbSizeinMicrons *= 2;
            tempPixels *= 2;
            Log.d(TAG, " < loop temp pix: " + tempPixels);
        }

        while (tempPixels > maxSbWidth)
        {
            //calculate the value of the scale bar
            resultSbSizeinMicrons /= 2;
            tempPixels /= 2;
            Log.d(TAG, " > loop temp pix: " + tempPixels);
        }
        int w = (int) Math.round(tempPixels);

        //set scale bar text
        /*
        String unit = "microns";
       if( (resultSbSizeinMicrons < 0 ) && (resultSbSizeinMicrons < 0 ) ) //centimeters
       {

       }
       else if((resultSbSizeinMicrons > 1000) && (resultSbSizeinMicrons < )) // millimeters
       {

       }
       else if() // microns
       {

       }
       else if() //nano
       {

       }
       else
       {

       }*/

        String resultPrintString = "  " + Double.toString(resultSbSizeinMicrons) + " microns  ";
        Log.d(TAG, "Scale Bar width = " + w + " Scale bar height = " + h);

        Bitmap scaled = Bitmap.createScaledBitmap(bm, w, h, true); // Make sure w and h are in the correct order
        //insert bitmap
        mPhotoEditor.addSBImage(scaled, resultPrintString, textColour);
        Log.d(TAG, "Scale Bar inserted, value = " + resultPrintString);
    }

    private void showCaliRemindDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Have you calibrated the App?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Log.d(TAG, "User confirms app calibrated");
                //show scale dialog
                //createSBColourDialog();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Log.d(TAG, "User confirms app not calibrated");
                finish();
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Log.d(TAG, "User cancels image edit");
                //TODO implement file deletion (see fileUtils class)
                finish();
            }
        });
        builder.create().show();
    }

    private File createImageFile() throws IOException
    {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "EDIT_" + timeStamp + "_";
        //File storageDir = FileUtils.createFolders();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".png", storageDir);
        editedImageFilePath = image.getAbsolutePath();

        return image;
    }

    @Override
    public void onBackPressed() {

        showBackPressDialog();
        // Otherwise defer to system default behavior.
        //super.onBackPressed();
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
}
