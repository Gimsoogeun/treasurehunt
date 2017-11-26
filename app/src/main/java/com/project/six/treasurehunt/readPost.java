package com.project.six.treasurehunt;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class readPost extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    ImageView descriptImage;
    postContext post;
    TextView textViewtitle;
    TextView textViewcontext;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_post);
        Intent intent=getIntent();
        post=new postContext();
        post.title=intent.getStringExtra("title");
        post.context1=intent.getStringExtra("context1");
        post.firebaseKey=intent.getStringExtra("firebaseKey");
        post.writerUID=intent.getStringExtra("writerUID");
        post.imageURL1=intent.getStringExtra("imageURL1");
        textViewtitle=(TextView)findViewById(R.id.readPostTitle);
        textViewcontext=(TextView)findViewById(R.id.readPostContext);
        descriptImage=(ImageView)findViewById(R.id.descriptImage);
        textViewtitle.setText(post.title);
        textViewcontext.setText(post.context1);
        Picasso.with(this).load(post.imageURL1).into(descriptImage);
        mAuth=FirebaseAuth.getInstance();
        initFirebaseDatabase();
    }
    private void initFirebaseDatabase(){
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("posts");
    }
    public void onRewrite(View view){
        NetworkInfo mNetworkState=getNetworkInfo();
        if(mNetworkState!=null && mNetworkState!=null && mNetworkState.isConnected()) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user.getUid().equals(post.writerUID)) {
                Intent intent = new Intent(this, rewritePost.class);
                intent.putExtra("firebaseKey", post.firebaseKey);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "작성자만 수정할수 있습니다.", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }
    public void clickClose(View view){
        finish();
    }
    public void clickRemove(View view){
        NetworkInfo mNetworkState=getNetworkInfo();
        if(mNetworkState!=null &&mNetworkState.isConnected()) {

            FirebaseUser user = mAuth.getCurrentUser();
            if (user.getUid().equals(post.writerUID)) {
                mDatabaseReference.child(post.firebaseKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

            } else {
                Toast.makeText(this, "작성자만 수정할수 있습니다.", Toast.LENGTH_SHORT).show();
            }
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
    public void myInfoButton(View view){
        Intent intent=new Intent(this, findPostsActivity.class);
        startActivity(intent);
        finish();
    }
}
