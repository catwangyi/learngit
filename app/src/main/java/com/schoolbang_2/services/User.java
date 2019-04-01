package com.schoolbang_2.services;

import com.schoolbang_2.domain.CommentItem;
import com.schoolbang_2.domain.PostItem;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class User extends BmobUser {
    private String id;
    //登陆用username
    private String nickName;
    private PostItem mPostItem;
    private CommentItem mCommentItem;
    private String phone;
    private BmobFile photo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    public PostItem getPostItem() {
        return mPostItem;
    }

    public void setPostItem(PostItem postItem) {
        mPostItem = postItem;
    }

    public CommentItem getCommentItem() {
        return mCommentItem;
    }

    public void setCommentItem(CommentItem commentItem) {
        mCommentItem = commentItem;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BmobFile getPhoto() {
        return photo;
    }

    public void setPhoto(BmobFile photo) {
        this.photo = photo;
    }
}
