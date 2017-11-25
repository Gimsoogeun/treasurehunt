package com.project.six.treasurehunt;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class rewritePost extends AppCompatActivity {
    String postfirebaseKey;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mValueEventListener;

    postContext post;

    EditText titleEdittext;
    EditText context1Edittext;
    EditText context2Edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewrite_post);
        Intent intent=getIntent();
        postfirebaseKey=intent.getStringExtra("firebaseKey");
        initView();
        initFirebaseDatabase();
    }
    public void initView(){
        titleEdittext=(EditText)findViewById(R.id.RWtitleET);
        context1Edittext=(EditText)findViewById(R.id.RWcontext1ET);
        context2Edittext=(EditText)findViewById(R.id.RWcontext2ET);

    }
    private void initFirebaseDatabase(){
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("posts").child(postfirebaseKey);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post=dataSnapshot.getValue(postContext.class);
                titleEdittext.setText(post.title);
                context1Edittext.setText(post.context1);
                context2Edittext.setText(post.context2);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Query query;

    }
    public void onClickReWrite(View view){
        NetworkInfo mNetworkState=getNetworkInfo();
        if( mNetworkState!=null && mNetworkState.isConnected()) {
            if (titleEdittext.getText().toString().equals("")) {
                Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (context1Edittext.getText().toString().equals("")) {
                Toast.makeText(this, "내용 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (context2Edittext.getText().toString().equals("")) {
                Toast.makeText(this, "보상 내용 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            post.title = titleEdittext.getText().toString();
            post.context1 = context1Edittext.getText().toString();
            post.context2 = context2Edittext.getText().toString();
            mDatabaseReference.setValue(post, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast.makeText(getApplicationContext(), "글 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "글 수정에 성공했습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }else{
            Toast.makeText(this, "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }
    public void currentInfo(View view){
        Intent intent=new Intent(this, main.class);
        startActivity(intent);
        finish();
    }
    public void pushPostViewButton(View view) {
        Intent intent=new Intent(this, postsActivity.class);
        startActivity(intent);
        finish();
    }

    private NetworkInfo getNetworkInfo(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }
}
