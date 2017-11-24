package com.project.six.treasurehunt;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class writePost extends AppCompatActivity {
    public EditText titleET;
    public EditText context1;
    public EditText context2;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mValueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        setTheme(android.R.style.Theme_NoTitleBar);
        titleET=(EditText)findViewById(R.id.titleET);
        context1=(EditText)findViewById(R.id.context1ET);
        context2=(EditText)findViewById(R.id.context2ET);
        initFirebaseDatabase();


    }
    private void initFirebaseDatabase(){
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("posts");
    }

    public void onClickWrite(View view){
        if(titleET.getText().toString().equals("") ){
            Toast.makeText(this,"제목을 입력해 주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(context1.getText().toString().equals("") ){
            Toast.makeText(this,"내용 입력해 주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(context2.getText().toString().equals("") ){
            Toast.makeText(this,"보상 내용 입력해 주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        postContext post=new postContext(titleET.getText().toString(),context1.getText().toString(),context2.getText().toString());
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        post.writerName=user.getDisplayName();
        post.writerUID=user.getUid();

        mDatabaseReference.push().setValue(post,new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(getApplicationContext(), "작성에 실패했습니다.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "작성에 성공했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
