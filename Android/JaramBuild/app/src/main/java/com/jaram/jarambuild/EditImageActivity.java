package com.jaram.jarambuild;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaram.jarambuild.imageUtils.PropertiesBSFragment;
import com.jaram.jarambuild.imageUtils.StickerBSFragment;
import com.jaram.jarambuild.imageUtils.TextEditorDialogFragment;
import com.jaram.jarambuild.utils.AddStickerEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;

////Original Code Source by Burhanuddin Rashid on 1/17/2018 as part of the https://github.com/burhanrashid52/PhotoEditor
////Used under the http://www.apache.org/licenses/LICENSE-2.0
////Edited May 2018 team Jaram >> JARAM12358@gmail.com

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
    private TextView mTxtCurrentTool;
    private String imageFilePath;
    //log
    private static final String TAG = "EditActivity";
    //list of stickerList index's used in imageviews
    private ArrayList<String> sliList;

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

        //Get bitmap uri from intent
        imageFilePath = Objects.requireNonNull(getIntent().getExtras()).getString("rawPhotoPath");
        Log.d(TAG, "imageFilePath: " + imageFilePath);
        //set bitmap to editor view
        mPhotoEditorView.getSource().setImageBitmap(BitmapFactory.decodeFile(imageFilePath));

        //hide action bar
        android.support.v7.app.ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null)
        {
            myActionBar.hide();
            Log.d(TAG, "ActionBar Hidden");
        }

        //instantiate sliList
        sliList = new ArrayList<String>();

        //show Calibration check dialog
        //TODO: Settings check for calibration reminder
        if (true)
        {
            //show calibration reminder
            showCaliRemindDialog();
        } else
        {
            //show scale bar colour dialog
            createSBColourDialog();
        }
    }


    // This method will be called when a AddStickerEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddStickerEvent(AddStickerEvent event)
    {
        Log.d(TAG, "Sticker Index from EventBus AddStickerEvent " + event.message);
        sliList.add(event.message);

        //For Debugging
        /*Iterator itr=sliList.iterator();
        while(itr.hasNext())
        {
            Log.d(TAG, "iterated array list " + itr.next());
        }*/
    }

    private void initViews()
    {
        ImageView imgPencil;
        ImageView imgEraser;
        ImageView imgUndo;
        ImageView imgRedo;
        ImageView imgText;
        ImageView imgCamera;
        ImageView imgGallery;
        ImageView imgSticker;
        ImageView imgSave;
        ImageView imgClose;

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);

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

        imgRedo = findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(this);

        imgCamera = findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);

        imgGallery = findViewById(R.id.imgGallery);
        imgGallery.setOnClickListener(this);

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
                mTxtCurrentTool.setText(R.string.label_text);
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
        if(!viewType.equals(ViewType.IMAGE))
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
        if(sliList.size() > 0)
        {
            sliList.remove(sliList.size()-1);
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
                mTxtCurrentTool.setText(R.string.label_brush);
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                break;
            case R.id.btnEraser:
                mPhotoEditor.brushEraser();
                mTxtCurrentTool.setText(R.string.label_eraser);
                break;
            case R.id.imgText:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor()
                {
                    @Override
                    public void onDone(String inputText, int colorCode)
                    {
                        mPhotoEditor.addText(inputText, colorCode);
                        mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                break;

            case R.id.imgUndo:
                mPhotoEditor.undo();
                break;

            case R.id.imgRedo:
                mPhotoEditor.redo();
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
                break;

            case R.id.imgCamera:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;

            case R.id.imgGallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void saveImage()
    {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            showLoading("Saving...");
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + ""
                    + System.currentTimeMillis() + ".png");
            try
            {
                file.createNewFile();
                mPhotoEditor.saveImage(file.getAbsolutePath(), new PhotoEditor.OnSaveListener()
                {
                    @Override
                    public void onSuccess(@NonNull String imagePath)
                    {
                        hideLoading();
                        showSnackbar("Image Saved Successfully");
                        // working mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                        //TODO send image path to addData via intent
                        Intent intent = new Intent(EditImageActivity.this, AddDataActivity.class);
                        intent.putExtra("editedImageUri", imagePath);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception)
                    {
                        hideLoading();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case CAMERA_REQUEST:
                    mPhotoEditor.clearAllViews();
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    mPhotoEditorView.getSource().setImageBitmap(photo);
                    break;
                case PICK_REQUEST:
                    try
                    {
                        mPhotoEditor.clearAllViews();
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        mPhotoEditorView.getSource().setImageBitmap(bitmap);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onColorChanged(int colorCode)
    {
        mPhotoEditor.setBrushColor(colorCode);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity)
    {
        mPhotoEditor.setOpacity(opacity);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize)
    {
        mPhotoEditor.setBrushSize(brushSize);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onStickerClick(Bitmap bitmap)
    {
        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_sticker);
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

    // this dialog lets the user choose the colour of the scale bar
    private void createSBColourDialog()
    {
        AlertDialog.Builder sBColourDialog = new AlertDialog.Builder(this);

        sBColourDialog.setTitle("Set Scale Bar Colour")
                .setItems(R.array.sbcolour, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        selectSBColour(pos);
                    }
                });
        sBColourDialog.show();
    }

    private void selectSBColour(int pos)
    {
        Bitmap bm;
        //choose bitmap colour (using position in sbcolour string array)
        switch (pos)
        {
            case 0: //grey
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.scalegrey);
                insertSBandText(bm);
                Log.d(TAG, "Grey Scale Bar colour selected");
                break;
            case 1:
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.scaleblack);
                insertSBandText(bm);
                Log.d(TAG, "Black Scale Bar colour selected");
                break;
            case 2:
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.scalewhite);
                insertSBandText(bm);
                Log.d(TAG, "White Scale Bar colour selected");
                break;
        }
    }

    private void insertSBandText(Bitmap bm)
    {
        //size of image in view
        double vHeight = mPhotoEditorView.getHeight();
        double vWidth = mPhotoEditorView.getWidth();
        Log.d(TAG, "viewHeight:" + vHeight + " " + "viewWidth:" + vWidth);
        //size of actual image
        double mvHeight = mPhotoEditorView.getMeasuredHeight();
        double mvWidth = mPhotoEditorView.getMeasuredWidth();
        Log.d(TAG, "mviewHeight:" + mvHeight + " " + "mviewWidth:" + mvWidth);

        //scale to desired width etc  TODO: use calibration data here
        int h = 30; // height in pixels
        int w = 150; // width in pixels
        Bitmap scaled = Bitmap.createScaledBitmap(bm, w, h, true); // Make sure w and h are in the correct order
        //insert bitmap
        mPhotoEditor.addSBImage(scaled);
        Log.d(TAG, "Scale Bar inserted");
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
                createSBColourDialog();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Log.d(TAG, "User confirms app not calibrated");
                //HomeActivity.openCameraIntent("calibrateActivity");
                //TODO: openCalibrate activity until then return to Home with finish();
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
                //deleting captured image by filepath
                /*
                if (deleteFile(imageFilePath))
                {
                    Log.d(TAG, "File deleted");
                } else
                {
                    Log.d(TAG, "File not deleted");
                }*/

                finish();
            }
        });
        builder.create().show();
    }
}
