package com.example.dailyselfies2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private ImageButton btnCamera, btnDelete;
    // Mai sua lai thang nay doi no lai thanh bitmap
    ArrayList<Bitmap> images;
    ArrayList<String> fileName;
    private ImageListViewAdapter adapter;
    String currentPhotoPath, currentSelfieName;
    private GridView gvImage;

    private static final long INTERVAL_TWO_MINUTES = 2*60*1000L; //thuat toan giay tren phut co the bi sai

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gvImage = (GridView) findViewById(R.id.imageList);
        btnCamera = (ImageButton) findViewById(R.id.btnCamera);
        btnDelete = (ImageButton) findViewById(R.id.btnDelete);
        adapter = new ImageListViewAdapter(this, images, fileName);
        String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();

        initData(path);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCameraActivityForResult();

            }
        });
        gvImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(MainActivity.this, clickImage.class);
                intent.putExtra("image", adapter.images.get(position));
                intent.putExtra("imgName", adapter.fileName.get(position));
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Warning!");
                alertDialog.setMessage("Do you want to delete all the photos?");
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletePhotos(path);
                        Toast.makeText(MainActivity.this, "Delete succeed", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.show();
            }
        });
        createAlarm();
    }


    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        File photoFile = new File(currentPhotoPath);
                        File selfieFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), currentSelfieName + ".jpg");
                        photoFile.renameTo(selfieFile);
                        // Xoa hien File Uri trc roi de hien anh bang Bitmap vao day
                        Bitmap imageBitmap = setImageFromFilePath(Uri.fromFile(selfieFile).getPath());
                        adapter.add(imageBitmap, selfieFile.getName());
                        gvImage.setAdapter(adapter);

                        //saveImage(imageBitmap);
                    }
                }
            });


    public void openCameraActivityForResult() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Toast.makeText(MainActivity.this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
        if (photoFile != null) {
            // Goi Uri duoi day
            Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                    "com.example.dailyselfies2.fileprovider",
                    photoFile);
            //Uri photoURI = Uri.fromFile(photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            cameraActivityResultLauncher.launch(takePictureIntent);
        }

    }


    public static Bitmap setImageFromFilePath(String imagePath, int targetW, int targetH) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
        bmpOptions.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, bmpOptions);
        int photoW = bmpOptions.outWidth;
        int photoH = bmpOptions.outHeight;

        // determine scale factor
        int scaleFactor = Math.max(photoW / targetW, photoH / targetH);

        // decode the image file into a Bitmap
        bmpOptions.inJustDecodeBounds = false;
        bmpOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmpOptions);
        return bitmap;
    }

    public static Bitmap setImageFromFilePath(String imagePath) {
        return setImageFromFilePath(imagePath, 160, 120);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        currentSelfieName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + currentSelfieName + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath().toString();
        return image;
    }

    private void createAlarm() {
        try {
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + INTERVAL_TWO_MINUTES,
                    INTERVAL_TWO_MINUTES,
                    pendingIntent);
        } catch (Exception exception) {
            Log.d("ALARM", exception.getMessage().toString());
        }
    }

    private void initData(String path) {
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {
            Bitmap imageBitmap = setImageFromFilePath(files[i].getPath());
            adapter.add(imageBitmap, files[i].getName());
            Log.d("Files", "FileName:" + files[i].getName());
        }
        gvImage.setAdapter(adapter);
    }

    private void deletePhotos(String path) {
        File dir = new File(path);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
        finish();
        startActivity(getIntent());
    }
}