package com.schoolbang_2;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.schoolbang_2.services.User;

import java.io.File;

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
public class RegisteredActivity extends AppCompatActivity {
    private EditText et_username;//
    private EditText et_pwd;
    private EditText et_pwd_confirm;
    private ImageView userImg;
    public static final int CHOOSE_PHOTO=2;
    private EditText et_nickname;
    private String imagePath;
    final String TAG="RegisteredActivity";
    private Button registered;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        et_username=findViewById(R.id.et_name_reg);
        et_nickname=findViewById(R.id.et_nickname_reg);
        et_pwd=findViewById(R.id.et_pwd_reg);
        et_pwd_confirm=findViewById(R.id.et_pwd_confirm_reg);
        userImg=findViewById(R.id.reg_userimg);
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(RegisteredActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    //如果没有权限
                    ActivityCompat.requestPermissions(RegisteredActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1 );
                }else{
                    imagePath="hasimg";//有图片
                    openAlbum();
                }
            }
        });
        registered=findViewById(R.id.registered_ac);
        registered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=et_username.getText().toString().trim();
                String pwd=et_pwd.getText().toString().trim();
                String nickName=et_nickname.getText().toString().trim();
                String pwd_confirm=et_pwd_confirm.getText().toString().trim();
                if (TextUtils.isEmpty(username)||TextUtils.isEmpty(pwd)||TextUtils.isEmpty(nickName)){
                    Toast.makeText(RegisteredActivity.this,"信息不能为空！" , Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(imagePath)){
                    Toast.makeText(RegisteredActivity.this,"信息不能为空！" , Toast.LENGTH_SHORT).show();
                }
                else{
                if ((pwd_confirm.equals(pwd))){
                    uploadImage(imagePath);
                    /*User user=new User();
                    user.setUsername(username);
                    user.setPassword(pwd);
                    user.setNickName(nickName);
                    user.signUp(new SaveListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if (e==null){//注册成功
                                Toast.makeText(RegisteredActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{//注册失败
                                Toast.makeText(RegisteredActivity.this, "注册失败！"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });*/
                }
                else if (!pwd_confirm.equals(pwd)){
                    Toast.makeText(RegisteredActivity.this,"密码不一致！" , Toast.LENGTH_SHORT).show();
                }}
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
            String docId= DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];//解析出数字格式的id
                String selection= MediaStore.Images.Media._ID+"="+id;
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
            final Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            userImg.setImageBitmap(bitmap);
            //uploadImage(imagePath);
            this.imagePath=imagePath;
        }else{
            Toast.makeText(this,"获取图片失败" ,Toast.LENGTH_SHORT ).show();
        }
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
                           // Toast.makeText(RegisteredActivity.this,"图片上传成功" ,Toast.LENGTH_SHORT ).show();
                        }else {
                           // Toast.makeText(RegisteredActivity.this,"图片上传失败" ,Toast.LENGTH_SHORT ).show();
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
        String username=et_username.getText().toString().trim();
        String pwd=et_pwd.getText().toString().trim();
        String nickName=et_nickname.getText().toString().trim();
        User user=new User();
        user.setUsername(username);
        user.setPassword(pwd);
        user.setPhoto(image);
        user.setNickName(nickName);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e==null){//注册成功
                    Toast.makeText(RegisteredActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    finish();
                }else{//注册失败
                    Toast.makeText(RegisteredActivity.this, "注册失败！"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
