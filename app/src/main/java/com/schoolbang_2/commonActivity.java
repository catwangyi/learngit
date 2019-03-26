package com.schoolbang_2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.schoolbang_2.services.BitmapUtils;

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
    private final int DOWNLOADFLAG=2;
    private commonAdapter adapter;
    private FloatingActionButton refresh;
    private final String TAG="commonActivity";
    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            pd.dismiss();
            switch (msg.what){
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
        pd=new ProgressDialog(commonActivity.this);
        pd.setMessage("正在加载中...");
        pd.show();
        Refresh();
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
                    if (mbitmap==null){
                        Log.i(TAG, "bitmap为空");
                    }else{
                        //Bitmap mbitmap2= ThumbnailUtils.extractThumbnail(mbitmap, 350,350 );
                        Bitmap mbitmap2= BitmapUtils.getThumb(saveFile.getPath(),350,350);
                        iv_post_photo.setImageBitmap(mbitmap2);
                    }
                    return view;
                }else {//文件不存在
                    item.getPhoto().download(saveFile, new DownloadFileListener() {
                        @Override
                        public void done(String s, BmobException e) {
                            //下载，再显示
                            /*Bitmap mbitmap = BitmapFactory.decodeFile(saveFile.getPath());
                            iv_post_photo.setImageBitmap(mbitmap);
                            tv_post_title.setText(item.getTitle());
                            tv_post_content.setText(item.getContent());*/
                        }
                        @Override
                        public void onProgress(Integer integer, long l) {

                        }
                    });
                    return view;
                }
            }else {//没有图片的情况下
                tv_post_title.setText(item.getTitle());
                tv_post_content.setText(item.getContent());
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
        new Thread(){
            @Override
            public void run() {
                final BmobQuery<PostItem> bmobQuery=new BmobQuery<>();
                bmobQuery.findObjects(new FindListener<PostItem>() {
                    @Override
                    public void done(final List<PostItem> list, BmobException e) {
                        if (e==null){//查询成功
                            for (final PostItem p:list) {
                                if (p.getPhoto()!=null){
                                    File saveFile = new File(getExternalCacheDir(),p.getPhoto().getFilename());
                                    if (saveFile.exists()){
                                        //文件存在，不处理
                                        Message msg=Message.obtain();
                                        msg.obj=list;
                                        msg.what=SUCCESS;
                                        mHandler.sendMessage(msg);
                                    }else {
                                        //文件不存在
                                        p.getPhoto().download(saveFile, new DownloadFileListener() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                Log.i(TAG,"下载成功"+p.getPhoto().getFilename());
                                                if (list.get(list.size()-1)==p){
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
                                }
                            }
                            //Toast.makeText(commonActivity.this,"刷新成功" ,Toast.LENGTH_SHORT ).show();
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
