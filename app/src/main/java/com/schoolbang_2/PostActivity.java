package com.schoolbang_2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.schoolbang_2.domain.PostItem;
import com.schoolbang_2.fragment.ButtonFragment;
import com.schoolbang_2.fragment.CommentFragment;
import com.schoolbang_2.services.User;

import java.io.File;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class PostActivity extends AppCompatActivity  {
    private ListView lv;
    private TextView postTitle;
    private ImageView userImg;
    private ImageView postImg;
    private TextView postDate;
    private User user;//帖子对应用户
    private TextView commentCount;
    private CommentFragment mCommentFragment;
    private ButtonFragment mButtonFragment;
    private TextView postContent;
    private TextView userName;
    private PostItem postItem;

    public PostItem getPostItem() {
        return postItem;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPostItem(PostItem postItem) {
        this.postItem = postItem;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mButtonFragment=new ButtonFragment();
        replaceFragment(mButtonFragment);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        Intent intent=getIntent();
        postItem=(PostItem)intent.getSerializableExtra("postItem");
        user=(User)intent.getSerializableExtra("User");
        //评论
        lv=findViewById(R.id.activity_post_listview);
        //标题
        postTitle=findViewById(R.id.activity_post_title);
        postTitle.setText(postItem.getTitle());
        //评论数量
        commentCount=findViewById(R.id.activity_post_commentCount);
        //头像
        userImg=findViewById(R.id.activity_post_userimg);
        //userImg.setImageBitmap();
        //图片
        postImg=findViewById(R.id.activity_post_postimg);
        if (postItem.getPhoto()==null){

        }else{
            File saveFile = new File(getExternalCacheDir(),postItem.getPhoto().getFilename());
            Bitmap mbitmap = BitmapFactory.decodeFile(saveFile.getPath());
            Bitmap mbitmap2= ThumbnailUtils.extractThumbnail(mbitmap, 350,350 );
            postImg.setImageBitmap(mbitmap2);
            postImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PostActivity.this, "点击",Toast.LENGTH_SHORT ).show();
                }
            });
        }
        //创建时间
        postDate=findViewById(R.id.activity_post_date);
        postDate.setText(postItem.getCreatedAt());
        //用户名
        userName=findViewById(R.id.activity_post_username);
        userName.setText("用户名");
        //内容
        postContent=findViewById(R.id.activity_post_content);
        postContent.setText(postItem.getContent());
    }
    public void dianji (View view){
            Toast.makeText(PostActivity.this,"follow！" ,Toast.LENGTH_SHORT ).show();
    }

    public void editcomment(View view){
        //Toast.makeText(PostActivity.this,"编辑评论！" ,Toast.LENGTH_SHORT ).show();
        if (mCommentFragment==null){
            mCommentFragment=new CommentFragment();
        }
        replaceFragment(mCommentFragment);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.comment_layout,fragment);
        if (fragment==mButtonFragment){
        }else {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    private class myAdapter extends BaseAdapter{

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView==null){
                view=View.inflate(PostActivity.this,R.layout.commentitem,null);
            }else {
                view=convertView;
            }
            ImageView userImg=view.findViewById(R.id.commentitem_userimg);
            TextView userName=view.findViewById(R.id.commentitem_username);
            TextView commentDate=view.findViewById(R.id.commentitem_commentdate);
            TextView commentContent=view.findViewById(R.id.commentitem_content);
            return view;
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}
