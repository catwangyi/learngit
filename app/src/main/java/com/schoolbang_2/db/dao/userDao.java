package com.schoolbang_2.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.schoolbang_2.db.userDBOpenHelper;
import com.schoolbang_2.services.User;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class userDao {
    private userDBOpenHelper mHelper;
    //只有一个有参的构造方法，要求必须传入上下文
    public userDao(Context context){
        mHelper=new userDBOpenHelper(context);
    }

    public long save(String name,String password){
        SQLiteDatabase db=mHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("username",name);
        values.put("password",password );
        long result=db.insert("user", null,values );
        db.close();
        return result;
    }

    public int delete(String name){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        int result=db.delete("user", "username=?",new String[]{name} );
        db.close();
        return result;
    }

    public int deleteAll(){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        int result=db.delete("user", null,null);
        db.close();
        return result;
    }

    public User find(){
        User user =new User();
        SQLiteDatabase db=mHelper.getReadableDatabase();
        Cursor cursor=db.query("user",new String[]{"username","password"},null,null,null,null,null);
        boolean result=cursor.moveToNext();
        if (result){
            String name=cursor.getString(0);
            String password=cursor.getString(1);
            user.setUsername(name);
            user.setPassword(password);
        }
        cursor.close();
        db.close();
        return user;
    }
}
