package com.schoolbang_2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class ShowImageActivity extends AppCompatActivity {
    private String imagePath;
    private ImageView showImage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showimage);
        showImage=findViewById(R.id.show_image);
        Intent intent=getIntent();
        imagePath=(String)intent.getSerializableExtra("imagepath");
        Bitmap mbitmap = BitmapFactory.decodeFile(imagePath);
        showImage.setImageBitmap(mbitmap);
        showImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
