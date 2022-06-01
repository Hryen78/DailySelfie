package com.example.dailyselfies2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class clickImage extends AppCompatActivity {
    ImageView imageView;
    TextView tvName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_item);

        imageView = (ImageView) findViewById(R.id.imageView);
        tvName = (TextView) findViewById(R.id.imgName);
        Intent intent = getIntent();

        if(intent.getExtras() != null){
            Bundle extras = intent.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("image");
            String imgName = intent.getStringExtra("imgName");
            imageView.setImageBitmap(imageBitmap);
            tvName.setText(imgName);
        }

    }
}
