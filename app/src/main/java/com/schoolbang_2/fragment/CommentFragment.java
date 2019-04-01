package com.schoolbang_2.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.schoolbang_2.PostActivity;
import com.schoolbang_2.R;
import com.schoolbang_2.domain.CommentItem;
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
public class CommentFragment extends Fragment {
    private Button back,send_comment;
    private EditText commentContent;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.comment_fragment,container,false );
        back=view.findViewById(R.id.comment_close);
        commentContent=view.findViewById(R.id.comment_content);
        send_comment=view.findViewById(R.id.comment_send);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager=getFragmentManager();
                FragmentTransaction transaction=fragmentManager.beginTransaction();
                transaction.replace(R.id.comment_layout, new ButtonFragment());
                transaction.commit();
            }
        });
        send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment_content=commentContent.getText().toString().trim();
                if (TextUtils.isEmpty(comment_content)){
                    Toast.makeText(getActivity(),"内容不能为空！" ,Toast.LENGTH_SHORT).show();
                }else {
                    final PostActivity postActivity=(PostActivity) getActivity();
                    CommentItem commentItem=new CommentItem();
                    commentItem.setPost(postActivity.getPostItem());
                    commentItem.setContent(comment_content);
                    Bundle bundle= CommentFragment.this.getArguments();
                    User user=(User)bundle.getSerializable("User");
                    commentItem.setAuthor(user);
                    commentItem.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e!=null){
                                Toast.makeText(getActivity(),"评论失败！请检查网络连接" , Toast.LENGTH_SHORT).show();
                            }else{
                                postActivity.refresh();
                                FragmentManager fragmentManager=getFragmentManager();
                                FragmentTransaction transaction=fragmentManager.beginTransaction();
                                transaction.replace(R.id.comment_layout, new ButtonFragment());
                                transaction.commit();
                            }
                        }
                    });
                }
            }
        });

    }
}
