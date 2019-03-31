package com.schoolbang_2.domain;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class PostItem extends BmobObject{
    private String title;//帖子标题
    private String content;//帖子内容
    //private User author;//帖子作者
    private BmobFile photo;


    public BmobFile getPhoto() {
        return photo;
    }

    public void setPhoto(BmobFile photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /*public User getAuthor() {
        return author;
    }*/

    /*public void setAuthor(User author) {
        this.author = author;
    }*/
}
