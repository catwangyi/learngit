package com.schoolbang_2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.schoolbang_2.domain.CommentItem;
import com.schoolbang_2.domain.PostItem;
import com.schoolbang_2.fragment.ButtonFragment;
import com.schoolbang_2.fragment.CommentFragment;
import com.schoolbang_2.services.User;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

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
    private List<CommentItem> mCommentItems;
    private User user;//帖子对应用户
    private TextView commentCount;
    private CommentFragment mCommentFragment;
    private ButtonFragment mButtonFragment;
    private TextView postContent;
    private TextView userName;
    private Bundle bundle;
    private PostItem postItem;
    private myAdapter adapter;
    private final int SUCCESS=1;
    private final int ERROR=0;
    private static final String TAG="PostActivity";
    private ProgressDialog pd;
    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //pd.dismiss();
            switch (msg.what){
                case SUCCESS:
                    mCommentItems= (List<CommentItem>) msg.obj;
                    if (adapter==null){
                        adapter=new myAdapter();
                        lv.setAdapter(adapter);
                    }else{
                        adapter.notifyDataSetChanged();
                    }
                    lv.setAdapter(adapter);
                    break;
                case ERROR:
                    Toast.makeText(PostActivity.this,"获取数据失败，请检查网络连接！" ,Toast.LENGTH_SHORT ).show();
                    finish();
                    break;
            }
        }
    };

    public PostItem getPostItem() {
        return postItem;
    }

    public void setPostItem(PostItem postItem) {
        this.postItem = postItem;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        if (pd==null){
            pd=new ProgressDialog(PostActivity.this);
        }
        /*pd.setMessage("加载中，请稍后");
        pd.setCancelable(false);
        pd.show();*/
        mButtonFragment=new ButtonFragment();
        replaceFragment(mButtonFragment);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        Intent intent=getIntent();
        postItem=(PostItem)intent.getSerializableExtra("postItem");
        //Log.i(TAG, "postItem ObjId:"+postItem.getObjectId());
        user=(User)intent.getSerializableExtra("User");
        //评论
        lv=findViewById(R.id.activity_post_listview);
        //标题
        postTitle=findViewById(R.id.activity_post_title);
        postTitle.setText(postItem.getTitle());

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
                    //点击查看原图
                    //Toast.makeText(PostActivity.this, "点击",Toast.LENGTH_SHORT ).show();
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
        refresh();
        //评论数量
        commentCount=findViewById(R.id.activity_post_commentCount);
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
        bundle=new Bundle();
        bundle.putSerializable("User",user);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.comment_layout,fragment);
        if (fragment==mButtonFragment){
        }else {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }
    public void refresh(){
        new Thread(){
            @Override
            public void run() {
                BmobQuery<CommentItem> query=new BmobQuery<>();
                query.addWhereEqualTo("post",postItem.getObjectId());
                query.include("post");
                query.findObjects(new FindListener<CommentItem>() {
                    @Override
                    public void done(List<CommentItem> list, BmobException e) {
                        if (e==null){
                            Log.i(TAG,"Id"+postItem.getObjectId());
                            if (list.isEmpty()){
                                Log.i(TAG, "empty");
                            }else {
                                for (int i=0;i<list.size();i++){
                                    Log.i(TAG,"评论内容+"+ list.get(i).getContent());
                                }
                                commentCount.setText(""+list.size());
                                Message msg=Message.obtain();
                                msg.what=SUCCESS;
                                msg.obj=list;
                                mHandler.sendMessage(msg);
                            }
                        }else{
                            Log.i(TAG,"e:"+e.getMessage());
                            Message msg=Message.obtain();
                            msg.what=ERROR;
                            mHandler.sendMessage(msg);
                        }
                    }
                });
            }
        }.start();
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
            //Log.i(TAG, "commentitems"+mCommentItems.get(1).getContent());
            CommentItem commentItem=mCommentItems.get(mCommentItems.size()-position-1);
            ImageView userImg=view.findViewById(R.id.commentitem_userimg);
            TextView userName=view.findViewById(R.id.commentitem_username);
            TextView commentDate=view.findViewById(R.id.commentitem_commentdate);
            TextView commentContent=view.findViewById(R.id.commentitem_content);
            userName.setText(commentItem.getAuthor().getUsername());
            commentDate.setText(commentItem.getCreatedAt());
            commentContent.setText(commentItem.getContent());
            Log.i(TAG, "数据适配成功");
            return view;
        }

        @Override
        public int getCount() {
            return mCommentItems.size();
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
