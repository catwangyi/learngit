package com.schoolbang_2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.schoolbang_2.db.dao.userDao;
import com.schoolbang_2.domain.PostItem;

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
public class commonActivity extends AppCompatActivity {
    private ListView lv;
    private List<PostItem> postItemList;
    private final int SUCCESS=1;
    private ProgressDialog pd;
    private Button get_out;
    private final int ERROR=0;
    private Button sendPost;
    private int FILEEXISTS=0;
    private  int IMAGEEXISTS=0;//有图的帖子
    private int NOIMAGE=0;//无图的帖子
    private final int NOMESSAGE=3;
    private commonAdapter adapter;
    private FloatingActionButton refresh;
    private final String TAG="commonActivity";
    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            pd.dismiss();
            switch (msg.what){
                case NOMESSAGE:
                    Toast.makeText(commonActivity.this,"没有帖子了..." ,Toast.LENGTH_SHORT ).show();
                    break;
                case SUCCESS:
                    postItemList =(List<PostItem>) msg.obj;
                    //Toast.makeText(commonActivity.this,"更新数据成功",Toast.LENGTH_SHORT ).show();
                    if (adapter==null){
                        adapter=new commonAdapter();
                        lv.setAdapter(adapter);
                    }else{
                        //通知数据适配器更新数据，而不是new新的数据适配器
                        adapter.notifyDataSetChanged();
                    }
                    lv.setAdapter(adapter);
                    Log.i(TAG, "commonAdapter适配器完成");
                    break;
                case ERROR:
                    Exception exception=(Exception) msg.obj;
                    Toast.makeText(commonActivity.this,"更新数据失败"+exception.getMessage() ,Toast.LENGTH_SHORT ).show();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Refresh();
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        lv=findViewById(R.id.lv);
        //lv.setScrollbarFadingEnabled(false);//设置滚动条
        get_out=findViewById(R.id.get_out);
        refresh=findViewById(R.id.refresh);
        sendPost=findViewById(R.id.goto_sendpost);
        sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(commonActivity.this,SendPostActivity.class);
                startActivity(intent);
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Refresh();
                Toast.makeText(commonActivity.this, "刷新成功",Toast.LENGTH_SHORT ).show();
            }
        });
        get_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showMessage("点击了");
                AlertDialog.Builder builder=new AlertDialog.Builder(commonActivity.this);
                builder.setTitle("提醒：");
                builder.setMessage("确定退出？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //showMessage("进入onclick");
                        userDao mDao=new userDao(commonActivity.this);
                        int result=mDao.deleteAll();
                        if (result!=0){
                            Toast.makeText(commonActivity.this,"已注销",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(commonActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(commonActivity.this,"退出失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
    }

    private class commonAdapter extends BaseAdapter{

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView==null) {
                //打气筒
                view = View.inflate(commonActivity.this,R.layout.postitem ,null );
            }
            else {
                view=convertView;
            }
            final TextView tv_post_title=view.findViewById(R.id.tv_post_title);
            final TextView tv_post_content=view.findViewById(R.id.tv_post_content);
            final PostItem item=postItemList.get(postItemList.size()-position-1);
            final ImageView iv_post_photo=view.findViewById(R.id.iv_post_photo);
            if (item.getPhoto()!=null){//有图片的情况
                //1.拿到图片
                final File saveFile = new File(getExternalCacheDir(),item.getPhoto().getFilename());
                //Log.i(TAG,item.getPhoto().getFilename()+"    saveFilePath:"+saveFile.getPath());
                if (saveFile.exists()){//文件存在
                    Bitmap mbitmap = BitmapFactory.decodeFile(saveFile.getPath());
                    // mbitmap.compress(Bitmap.CompressFormat.JPEG,30 , )
                    tv_post_title.setText(item.getTitle());
                    tv_post_content.setText(item.getContent());
                    Bitmap mbitmap2= ThumbnailUtils.extractThumbnail(mbitmap, 350,350 );
                    //Bitmap mbitmap2= BitmapUtils.getThumb(saveFile.getPath(),350,350);
                    iv_post_photo.setImageBitmap(mbitmap2);
                    return view;
                }else {//文件不存在

                    return view;
                }
            }else {//没有图片的情况下
                tv_post_title.setText(item.getTitle());
                tv_post_content.setText(item.getContent());
                iv_post_photo.setImageBitmap(null);
                return view;
            }
        }

        @Override
        public int getCount() {
            return postItemList.size();
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
    private void showMessage(String s){
        Toast.makeText(commonActivity.this,s ,Toast.LENGTH_LONG ).show();
    }
    private void Refresh(){
        if (pd==null){
            pd=new ProgressDialog(commonActivity.this);
        }
        pd.setMessage("正在加载中...");
        pd.show();
        new Thread(){
            @Override
            public void run() {
                final BmobQuery<PostItem> bmobQuery=new BmobQuery<>();
                bmobQuery.findObjects(new FindListener<PostItem>() {
                    @Override
                    public void done(final List<PostItem> list, BmobException e) {
                        if (e==null){//查询成功
                            if (list.size()!=0){//云端有数据
                                Log.i(TAG, "    list.size():"+list.size());
                                for (int i=0;i<list.size();i++) {
                                    if (list.get(i).getPhoto()!=null){//有图
                                        File saveFile = new File(getExternalCacheDir(),list.get(i).getPhoto().getFilename());
                                        if (saveFile.exists()){
                                            //文件存在，不处理
                                            Log.i(TAG,"文件存在"+saveFile.getPath());
                                            FILEEXISTS++;
                                            Log.i(TAG,"FILEEXISTS:"+FILEEXISTS);
                                        }else {
                                            //文件不存在
                                            Log.i(TAG,"开始下载"+saveFile.getPath()+"     I:"+i+"filename"+list.get(i).getTitle());
                                            list.get(i).getPhoto().download(saveFile, new DownloadFileListener() {
                                                @Override
                                                public void done(String s, BmobException e) {
                                                    Log.i(TAG,"下载成功"+s);
                                                    IMAGEEXISTS++;
                                                    Log.i(TAG,"IMAGEEXISTS:"+ IMAGEEXISTS);
                                                    if (NOIMAGE+IMAGEEXISTS+FILEEXISTS==list.size()){
                                                        Log.i(TAG,"ok");
                                                        Message msg=Message.obtain();
                                                        msg.obj=list;
                                                        msg.what=SUCCESS;
                                                        mHandler.sendMessage(msg);
                                                    }
                                                }
                                                @Override
                                                public void onProgress(Integer integer, long l) {
                                                }
                                            });
                                        }
                                    }else{//无图
                                        Log.i(TAG,"无图：：：："+"      I:"+i+"filename"+list.get(i).getTitle());
                                        NOIMAGE++;
                                        Log.i(TAG,"NOIMAGE:"+NOIMAGE);
                                        /*Message msg=Message.obtain();
                                        msg.obj=list;
                                        msg.what=SUCCESS;
                                        mHandler.sendMessage(msg);*/
                                    }
                                }
                                if (NOIMAGE+IMAGEEXISTS+FILEEXISTS==list.size()){
                                    Log.i(TAG,"ok");
                                    Message msg=Message.obtain();
                                    msg.obj=list;
                                    msg.what=SUCCESS;
                                    mHandler.sendMessage(msg);
                                }
                            }else{//云端没有数据
                                Message msg=Message.obtain();
                                msg.obj=null;
                                msg.what=NOMESSAGE;
                                mHandler.sendMessage(msg);
                            }
                        }
                        else
                        {//查询失败
                            //Toast.makeText(commonActivity.this,"更新数据失败" ,Toast.LENGTH_SHORT ).show();
                            Message msg=Message.obtain();
                            msg.what=ERROR;
                            msg.obj=e;
                            mHandler.sendMessage(msg);
                        }
                    }
                });
            }
        }.start();
    }
}