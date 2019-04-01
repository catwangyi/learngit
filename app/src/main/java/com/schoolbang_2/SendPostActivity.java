package com.schoolbang_2;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.schoolbang_2.domain.PostItem;
import com.schoolbang_2.services.User;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class SendPostActivity extends AppCompatActivity {
    private EditText post_title;
    private EditText post_content;
    public static final int TAKE_PHOTO=1;
    public static final int SUCCESS=3;
    public static final int CHOOSE_PHOTO=2;
    private static final String TAG="SendPostActivity";
    private ImageView picture;
    private Uri imageUri;
    private User user;
    private String imagePath;
    private String imagePath_take;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog=new ProgressDialog(SendPostActivity.this);
        mProgressDialog.setMessage("正在上传，请稍后...");
        setContentView(R.layout.sendpost);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        post_title=findViewById(R.id.send_post_title);
        picture=findViewById(R.id.iv_picture);
        post_content=findViewById(R.id.send_post_content);
        Intent intent=getIntent();
        user=(User)intent.getSerializableExtra("User");
        Button send_post;
        Button takePhoto=findViewById(R.id.take_photo);
        Button chooseFromAlbum=findViewById(R.id.choose_from_album);
        send_post=findViewById(R.id.send_post);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建File对象，用于存储拍照后的图片(output_image.jpg)
                Date now=new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String imagename=dateFormat.format(now)+".jpg";
                File outputImage=new File(getExternalCacheDir(),imagename);
              //  Toast.makeText(SendPostActivity.this,imagename ,Toast.LENGTH_LONG ).show();
                imagePath_take=getExternalCacheDir()+"/"+imagename;
                /*(getExternalCacheDir())获取当前应用缓存数据的位置（/sdcard/Android/data/<Package name>/cache）*/
                try{
                    if (outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT>=24){//android7.0以上
                    imageUri= FileProvider.getUriForFile(SendPostActivity.this,"com.schoolbang_2" , outputImage);
                }else{//android7.0以下
                    imageUri=Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri );
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SendPostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    //如果没有权限
                    ActivityCompat.requestPermissions(SendPostActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1 );
                }else{
                    imagePath="hasimg";//有图片
                    openAlbum();
                }
            }
        });
        send_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=post_title.getText().toString().trim();
                String content=post_content.getText().toString().trim();
                if (TextUtils.isEmpty(title)||TextUtils.isEmpty(content)){
                    Toast.makeText(SendPostActivity.this,"标题或内容不能为空!" ,Toast.LENGTH_SHORT ).show();
                }
                else{//发布
                    mProgressDialog.show();
                    picture.setDrawingCacheEnabled(true);
                    Bitmap obmp = Bitmap.createBitmap(picture.getDrawingCache());
                    if(TextUtils.isEmpty(imagePath)&&TextUtils.isEmpty(imagePath_take)){//没有图片
                        save(null);
                        /*PostItem postItem=new PostItem();
                        postItem.setTitle(post_title.getText().toString().trim());
                        postItem.setContent(post_content.getText().toString().trim());
                        postItem.setPhoto(null);
                        postItem.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e==null){
                                    Toast.makeText(SendPostActivity.this,"上传成功！" ,Toast.LENGTH_SHORT ).show();
                                    finish();
                                }else{
                                    Toast.makeText(SendPostActivity.this,"上传失败！"+e.getMessage() ,Toast.LENGTH_SHORT ).show();
                                }
                            }
                        });*/
                    }else{//有图片
                        uploadImage(imagePath);
                    }
                }
            }
        });
    }
    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this,"没有权限" ,Toast.LENGTH_SHORT ).show();
                }
                break;
            default:
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode==RESULT_OK){
                    try{//将拍摄的照片显示出来
                       /* Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);*/
                        displayImage(imagePath_take);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode==RESULT_OK){
                    //判断系统手机系统版本号
                    if (Build.VERSION.SDK_INT>=19){
                        hanleImageOnKitKat(data);  //4.4以上系统
                    }else{
                        hanleImageBeforeKitKat(data);//4.4以下系统
                    }
                }
                break;
            default:
                break;
        }
    }
    @TargetApi(19)
    private void hanleImageOnKitKat(Intent data) {
        String imagePath=null;
        Uri uri=data.getData();
        if (DocumentsContract.isDocumentUri(this,uri )){
            //如果是document类型的uri，则通过document id 处理
            String docId=DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];//解析出数字格式的id
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads")
                        ,Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null );
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri,则使用普通方法处理
            imagePath=getImagePath(uri,null );
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath=uri.getPath();
        }
        displayImage(imagePath);
    }
    private void uploadImage(String imagePath) {
        File file=new File(imagePath);
        Luban.with(this).load(file).ignoreBy(100).setCompressListener(new OnCompressListener() {
            @Override
            public void onStart() {
                Log.i(TAG,"开始压缩" );
            }

            @Override
            public void onSuccess(File file) {
                Log.i(TAG,"压缩成功" );
                final BmobFile image=new BmobFile(file);
                image.upload(new UploadFileListener() {//上传图片
                    @Override
                    public void done(BmobException e) {
                        if (e==null){
                            save(image);
                            Toast.makeText(SendPostActivity.this,"图片上传成功" ,Toast.LENGTH_SHORT ).show();
                        }else {
                            Toast.makeText(SendPostActivity.this,"图片上传失败" ,Toast.LENGTH_SHORT ).show();
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                if (e!=null){
                    Log.i(TAG,"压缩失败"+e.getMessage() );
                }
            }
        }).launch();
    }
    private void save(BmobFile image) {
        PostItem postItem=new PostItem();
        postItem.setTitle(post_title.getText().toString().trim());
        postItem.setContent(post_content.getText().toString().trim());
        postItem.setPhoto(image);
        postItem.setAuthor(user);
        postItem.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    Toast.makeText(SendPostActivity.this,"上传成功" ,Toast.LENGTH_SHORT ).show();
                    mProgressDialog.dismiss();
                    finish();
                }else{
                    Toast.makeText(SendPostActivity.this,"上传失败"+e.getMessage() ,Toast.LENGTH_SHORT ).show();
                }
            }
        });
    }

    private void hanleImageBeforeKitKat(Intent data) {
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null );
        displayImage(imagePath);
    }
    private String getImagePath(Uri uri, String selection) {
        String path=null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor=getContentResolver().query(uri,null ,selection ,null ,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore
                        .Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath) {
        if (imagePath!=null){
            final Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
            //uploadImage(imagePath);
            this.imagePath=imagePath;
        }else{
            Toast.makeText(this,"获取图片失败" ,Toast.LENGTH_SHORT ).show();
        }
    }
}
