package com.schoolbang_2.services;

import com.schoolbang_2.domain.PostItem;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class PostInfoServices {


    /**
     * @param title
     * @param content

     * @return 0表示上传未成功，1表示上传成功
     */
    public static void sendPost( String title, String content /*User user*/){
        PostItem postItem=new PostItem();
        postItem.setTitle(title);
        postItem.setContent(content);
        //postItem.setAuthor(user);//作者User类型
        postItem.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {

            }
        });
    }

    public List<PostItem> getAllPosts(){
        List<PostItem> postItems=new ArrayList<>();



        return postItems;
    }
}
