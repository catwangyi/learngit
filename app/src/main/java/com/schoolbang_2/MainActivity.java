package com.schoolbang_2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.schoolbang_2.db.dao.userDao;
import com.schoolbang_2.services.User;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * banned
 */
public class MainActivity extends AppCompatActivity {
    private userDao mDao;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        Bmob.initialize(this, "831152d39b86d435aecb2e72e6aca107");
        mDao = new userDao(this);
        user=mDao.find();//登录
        user.login(new SaveListener<Object>() {
            @Override
            public void done(Object o, BmobException e) {
                if(e == null) {
                    Toast.makeText(MainActivity.this,"欢迎"+user.getUsername(), Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MainActivity.this,commonActivity.class);
                    intent.putExtra("User", user);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(MainActivity.this, "请登录", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MainActivity.this,loginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}