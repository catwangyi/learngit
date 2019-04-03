package com.schoolbang_2;

import android.animation.Animator;
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
import cn.bmob.v3.listener.DownloadFileListener;
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
    private Animator mCurrentAnimator;//动画，可以再动画执行过程中取消
    private TextView postDate;
    private List<CommentItem> mCommentItems;
    private User user;//帖子对应用户
    private TextView commentCount;
    private CommentFragment mCommentFragment;
    private ButtonFragment mButtonFragment;
    private TextView postContent;
    private TextView nickName;
    private Bundle bundle;
    private PostItem postItem;
    private int mShortAnimationDuration;
    private myAdapter adapter;
    private final int SUCCESS=1;
    private String imgpath;
    //private int UserImgOfPost;
    private int UserImgOfComment;
    private final int ERROR=0;
    private final int USERIMG=3;
    private final int NO=2;
    private static final String TAG="PostActivity";
    private ProgressDialog pd;
    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            pd.dismiss();
            switch (msg.what){
                case SUCCESS:
                    mCommentItems= (List<CommentItem>) msg.obj;
                    Log.i(TAG,"mCommentItems.size()"+mCommentItems.size() );
                        UserImgOfComment=0;
                        if (adapter==null){
                            adapter=new myAdapter();
                            lv.setAdapter(adapter);
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                        lv.setAdapter(adapter);

                    break;
                case ERROR:
                    Toast.makeText(PostActivity.this,"请检查网络连接！" ,Toast.LENGTH_SHORT ).show();
                    //finish();
                    break;
                case NO:
                    //Toast.makeText(PostActivity.this,"请检查网络连接！" ,Toast.LENGTH_SHORT ).show();
                    //finish();
                    break;
                    case USERIMG:
                        Bitmap mbitmap = BitmapFactory.decodeFile((String) msg.obj);
                        userImg.setImageBitmap(mbitmap);
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
        pd=new ProgressDialog(PostActivity.this);
        mButtonFragment=new ButtonFragment();
        replaceFragment(mButtonFragment);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        final Intent intent=getIntent();
        postItem=(PostItem)intent.getSerializableExtra("postItem");
       // Log.i(TAG,"authorname" +postItem.getAuthor().getPhoto().getFilename());
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
        //创建时间
        postDate=findViewById(R.id.activity_post_date);
        postDate.setText(postItem.getCreatedAt());
        //用户名
        nickName=findViewById(R.id.activity_post_nickname);
        nickName.setText(postItem.getAuthor().getNickName());
        //内容
        postContent=findViewById(R.id.activity_post_content);
        postContent.setText(postItem.getContent());
        //评论数量
        commentCount=findViewById(R.id.activity_post_commentCount);
        //图片
        postImg=findViewById(R.id.activity_post_postimg);
        if (postItem.getPhoto()==null){

        }else{
            final File saveFile = new File(getExternalCacheDir(),postItem.getPhoto().getFilename());
            //原图
            final Bitmap mbitmap = BitmapFactory.decodeFile(saveFile.getPath());
            //缩略图
            Bitmap mbitmap2= ThumbnailUtils.extractThumbnail(mbitmap, 350,350 );
            postImg.setImageBitmap(mbitmap2);
            postImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击查看原图
                    Intent intentToImage=new Intent(PostActivity.this, ShowImageActivity.class);
                    intentToImage.putExtra("imagepath",saveFile.getPath());
                    startActivity(intentToImage);
                }
            });
        }
        refresh();
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
        pd.setMessage("加载中，请稍后");
        pd.setCancelable(false);
        pd.show();
        new Thread(){
            @Override
            public void run() {
                final File userimg = new File(getExternalCacheDir(),postItem.getAuthor().getPhoto().getFilename());
                Log.i(TAG,"头像的真实名称:"+ postItem.getAuthor().getPhoto().getFilename());
                Log.i(TAG, "头像保存路径:"+userimg.getPath());
                Log.i(TAG,"userid:"+ postItem.getAuthor().getObjectId());
                imgpath=userimg.getPath();
                if (userimg.exists()){
                    //文件存在，不处理
                    Log.i(TAG,"文件存在"+userimg.getPath());
                    /*Message msg=Message.obtain();
                    msg.what=USERIMG;
                    msg.obj=imgpath;
                    mHandler.sendMessage(msg);*/
                    Bitmap mbitmap = BitmapFactory.decodeFile(imgpath);
                    userImg.setImageBitmap(mbitmap);
                }else {
                    imgpath=userimg.getPath();
                    BmobQuery<PostItem> query=new BmobQuery<>();
                    query.addWhereEqualTo("objectId",(String)postItem.getObjectId());
                    query.include("author");
                    query.findObjects(new FindListener<PostItem>() {
                        @Override
                        public void done(List<PostItem> list, BmobException e) {
                            if (e==null){

                                list.get(0).getAuthor().getPhoto().download(userimg, new DownloadFileListener() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        /*Message msg=Message.obtain();
                                        msg.what=USERIMG;
                                        msg.obj=imgpath;
                                        mHandler.sendMessage(msg);*/
                                        Bitmap mbitmap = BitmapFactory.decodeFile(imgpath);
                                        userImg.setImageBitmap(mbitmap);
                                    }

                                    @Override
                                    public void onProgress(Integer integer, long l) {

                                    }
                                });
                            }else{
                                Message msg=Message.obtain();
                                msg.what=ERROR;
                                mHandler.sendMessage(msg);
                            }
                        }
                    });
                }
                BmobQuery<CommentItem> query=new BmobQuery<>();
                query.addWhereEqualTo("post",postItem.getObjectId());
                query.include("post");
                query.include("author");
                query.findObjects(new FindListener<CommentItem>() {
                    @Override
                    public void done(List<CommentItem> list, BmobException e) {
                        if (e==null){
                            if (!list.isEmpty()){
                                Log.i(TAG,"Id"+postItem.getObjectId());
                                if (list.size()==0){
                                    Log.i(TAG, "empty");
                                    Message msg=Message.obtain();
                                    msg.what=NO;
                                    mHandler.sendMessage(msg);
                                }else {
                                    for (int i=0;i<list.size();i++){
                                        Log.i(TAG,"评论内容+"+ list.get(i).getContent());
                                        final File userimg_comment = new File(getExternalCacheDir(),list.get(i).getAuthor().getPhoto().getFilename());
                                        if (userimg_comment.exists()){
                                            //文件存在，不处理
                                            Log.i(TAG,"文件存在"+userimg_comment.getPath());
                                            UserImgOfComment++;
                                        }else {
                                            list.get(i).getAuthor().getPhoto().download(userimg_comment, new DownloadFileListener() {
                                                @Override
                                                public void done(String s, BmobException e) {
                                                    UserImgOfComment++;
                                                }

                                                @Override
                                                public void onProgress(Integer integer, long l) {

                                                }
                                            });
                                        }
                                    }
                                    if (UserImgOfComment==list.size()){
                                        commentCount.setText(""+list.size());
                                        Message msg=Message.obtain();
                                        msg.what=SUCCESS;
                                        msg.obj=list;
                                        mHandler.sendMessage(msg);
                                    }
                                }
                            }else {
                               /* Message msg=Message.obtain();
                                msg.what=NO;
                                mHandler.sendMessage(msg);*/
                            }
                        }else {
                            Log.i(TAG, e.getMessage());
                            /*Message msg=Message.obtain();
                            msg.what=ERROR;
                            mHandler.sendMessage(msg);*/
                            Toast.makeText(PostActivity.this,"请检查网络连接！" ,Toast.LENGTH_SHORT ).show();
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
            File comment_img=new File(getExternalCacheDir(),commentItem.getAuthor().getPhoto().getFilename());
            Bitmap bitmap=BitmapFactory.decodeFile(comment_img.getPath());
            userImg.setImageBitmap(bitmap);
            TextView nickName=view.findViewById(R.id.commentitem_nickname);
            TextView commentDate=view.findViewById(R.id.commentitem_commentdate);
            TextView commentContent=view.findViewById(R.id.commentitem_content);
            nickName.setText(commentItem.getAuthor().getNickName());
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
