package com.schoolbang_2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.schoolbang_2.services.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class RegisteredActivity extends AppCompatActivity {
    private EditText et_name;
    private EditText et_pwd;
    private EditText et_pwd_confirm;
    final String TAG="RegisteredActivity";
    private Button registered;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        et_name=findViewById(R.id.et_name_reg);
        et_pwd=findViewById(R.id.et_pwd_reg);
        et_pwd_confirm=findViewById(R.id.et_pwd_confirm_reg);
        registered=findViewById(R.id.registered_ac);
        registered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=et_name.getText().toString().trim();
                String pwd=et_pwd.getText().toString().trim();
                String pwd_confirm=et_pwd_confirm.getText().toString().trim();
                if (TextUtils.isEmpty(username)||TextUtils.isEmpty(pwd)){
                    Toast.makeText(RegisteredActivity.this,"账号或密码不能为空！" , Toast.LENGTH_SHORT).show();
                }else{
                if ((pwd_confirm.equals(pwd))){
                    User user=new User();
                    user.setUsername(username);
                    user.setPassword(pwd);
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
                else if (!pwd_confirm.equals(pwd)){
                    Toast.makeText(RegisteredActivity.this,"密码不一致！" , Toast.LENGTH_SHORT).show();
                }}
            }
        });
    }
}
