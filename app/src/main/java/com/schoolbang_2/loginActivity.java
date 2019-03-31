package com.schoolbang_2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.schoolbang_2.db.dao.userDao;
import com.schoolbang_2.services.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class loginActivity extends AppCompatActivity{
    private EditText et_name;
    private EditText et_pwd;
    private Button login;
    private userDao mDao;
    private Button registered;
    private final String TAG="loginActivity";
    private void check(){
        User user =new User();
        //此处替换为你的用户名
        user =mDao.find();
        //此处替换为你的密码
        BmobQuery<User> personQuery = new BmobQuery<User>();
        //增加查询条件
        personQuery.addWhereEqualTo("id", user.getId());
        personQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e == null) {//本地有账号

                }else{
                    Toast.makeText(loginActivity.this,
                            "数据获取成功！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        mDao=new userDao(this);
        //check();
        et_name = findViewById(R.id.et_name_login);
        et_pwd = findViewById(R.id.et_pwd_login);
        login=findViewById(R.id.login_ac);
        registered=findViewById(R.id.registered_at_login);
        registered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(loginActivity.this,RegisteredActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = et_name.getText().toString().trim();
                final String pwd = et_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
                    Toast.makeText(loginActivity.this, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Toast.makeText(loginActivity.this, "进入！", Toast.LENGTH_SHORT).show();
                User user = new User();
                user.setUsername(name);
                user.setPassword(pwd);
                user.login(new SaveListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                                if(e == null) {//登录成功
                                    Intent intent=new Intent(loginActivity.this,commonActivity.class);
                                    startActivity(intent);
                                    mDao.save(name,pwd);
                                    finish();
                                }else{
                                    Toast.makeText(loginActivity.this, "登录失败:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                    }
                });
            }

        });
    }
}
