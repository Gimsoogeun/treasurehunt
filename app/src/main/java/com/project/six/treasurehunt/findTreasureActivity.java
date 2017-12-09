package com.project.six.treasurehunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
//발견한 보물의 정보를 볼수있는 activity입니다.
public class findTreasureActivity extends AppCompatActivity {

    String postfirebaseKey;
    postContext post;
    TextView textViewtitle;
    TextView textViewcontext1;
    TextView textViewcontext2;
    ImageView desImage, rewordImage;

    //firebaseDatabase 사용하기 위한 변수
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;

    //액티비티 생성시 view를 초기화하고 firebase 관련 변수들을 초기화합니다.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_treasure);
        Intent intent=getIntent();
        postfirebaseKey=intent.getStringExtra("firebaseKey");
        initFirebaseDatabase();
    }
    //firebase관련 변수들을 초기화합니다.
    private void initFirebaseDatabase(){
        mAuth=FirebaseAuth.getInstance();
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("posts").child(postfirebaseKey);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post=dataSnapshot.getValue(postContext.class);
                initView();
                post.isfinded=true;
                post.finderUID=mAuth.getUid();
                mDatabaseReference.setValue(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    //activity의 view들을 초기화합니다.
    private void initView(){
        desImage=(ImageView)findViewById(R.id.desImage);
        rewordImage=(ImageView)findViewById(R.id.rewordImage);
        textViewtitle=(TextView)findViewById(R.id.FindedPostTitle);
        textViewcontext1=(TextView)findViewById(R.id.FindedPostContext1);
        textViewcontext2=(TextView)findViewById(R.id.FindedPostContext2);

        textViewtitle.setText(post.title);
        textViewcontext1.setText(post.context1);
        textViewcontext2.setText(post.context2);
        Picasso.with(this).load(post.imageURL1).into(desImage);
        Picasso.with(this).load(post.imageURL2).into(rewordImage);

    }
    //"닫기" 버튼을 눌렀을때 이 activity를 종료합니다.
    public void clickClose(View v){
        finish();
    }
    //"내가 찾은 보물"버튼을 눌렀을시 activity 이동합니다.
    public void myInfoButton(View view){
        Intent intent=new Intent(this, findPostsActivity.class);
        startActivity(intent);
        finish();
    }
    //"게시글 보기"버튼을 눌렀을시 activity 이동합니다.
    public void pushPostViewButton(View view) {
        Intent intent=new Intent(this, postsActivity.class);
        startActivity(intent);
        finish();
    }
    //"현재 정보"버튼을 눌렀을시 activity 이동합니다.
    public void currentInfo(View view){
        Intent intent=new Intent(this, main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
