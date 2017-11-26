package com.project.six.treasurehunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class findTreasureActivity extends AppCompatActivity {

    String postfirebaseKey;
    postContext post;
    TextView textViewtitle;
    TextView textViewcontext1;
    TextView textViewcontext2;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReference2;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_treasure);
        Intent intent=getIntent();
        postfirebaseKey=intent.getStringExtra("firebaseKey");
        initFirebaseDatabase();
    }
    private void initFirebaseDatabase(){
        mAuth=FirebaseAuth.getInstance();
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("posts").child(postfirebaseKey);
//        mDatabaseReference2=mFirebaseDatabase.getReference("users").child(mAuth.getUid());
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
    private void initView(){
        textViewtitle=(TextView)findViewById(R.id.FindedPostTitle);
        textViewcontext1=(TextView)findViewById(R.id.FindedPostContext1);
        textViewcontext2=(TextView)findViewById(R.id.FindedPostContext2);

        textViewtitle.setText(post.title);
        textViewcontext1.setText(post.context1);
        textViewcontext2.setText(post.context2);

    }
    public void clickClose(View v){
        finish();
    }
    public void myInfoButton(View view){
        Intent intent=new Intent(this, findPostsActivity.class);
        startActivity(intent);
        finish();
    }
    public void pushPostViewButton(View view) {
        Intent intent=new Intent(this, postsActivity.class);
        startActivity(intent);
        finish();
    }
    public void currentInfo(View view){
        Intent intent=new Intent(this, main.class);
        startActivity(intent);
        finish();
    }
}
