package com.schoolbang_2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.schoolbang_2.domain.PostItem;

import java.io.File;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class PostActivity extends AppCompatActivity {
    private ListView lv;
    private TextView postTitle;
    private ImageView userImg;
    private ImageView postImg;
    private TextView postDate;
    private TextView postContent;
    private TextView userName;
    private Button follow;
    private PostItem postItem;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        Intent intent=getIntent();
        postItem=(PostItem)intent.getSerializableExtra("postItem");
        //评论
        lv=findViewById(R.id.activity_post_listview);
        //标题
        postTitle=findViewById(R.id.activity_post_title);
        postTitle.setText(postItem.getTitle());
        //头像
        userImg=findViewById(R.id.activity_post_userimg);
        //userImg.setImageBitmap();
        //图片
        postImg=findViewById(R.id.action_bar_activity_postimg);
        if (postItem.getPhoto()==null){

        }else{
            File saveFile = new File(getExternalCacheDir(),postItem.getPhoto().getFilename());
            Bitmap mbitmap = BitmapFactory.decodeFile(saveFile.getPath());
            Bitmap mbitmap2= ThumbnailUtils.extractThumbnail(mbitmap, 350,350 );
            postImg.setImageBitmap(mbitmap2);
        }
        /*postImg.setImageBitmap(postItem.getBitmap());*/
        //创建时间
        postDate=findViewById(R.id.activity_post_date);
        postDate.setText(postItem.getCreatedAt());
        //用户名
        userName=findViewById(R.id.activity_post_username);
        userName.setText("用户名");
        //内容
        postContent=findViewById(R.id.activity_post_content);
        postContent.setText(postItem.getContent());
        //follow按钮
        follow=findViewById(R.id.activity_post_follow);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PostActivity.this,"follow!" ,Toast.LENGTH_SHORT ).show();
            }
        });
    }
}
