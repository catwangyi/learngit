package com.schoolbang_2.domain;

import com.schoolbang_2.services.User;

import cn.bmob.v3.BmobObject;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class CommentItem extends BmobObject {
    private String content;//评论内容
    private User post;//评论对应的帖子
    private User author;//评论的人

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getPost() {
        return post;
    }

    public void setPost(User post) {
        this.post = post;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
