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
//postsActivity에서 선택된 게시글의 정보를 보여주는 화면입니다.
//여기서는 게시글의 힌트만을 볼수있습니다.
public class readPost extends AppCompatActivity {
    //firebase에서 게시글의 정보를 얻어옵니다.
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    //view의 정보들과 게시글의 내용을 받아오는 변수들입니다.
    ImageView descriptImage;
    postContext post;
    TextView textViewtitle;
    TextView textViewcontext;

    FirebaseAuth mAuth;
    //시작시 각 view들을 초기화하고 firebase를 초기화합니다.
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
    //파이어베이스를 초기화합니다.
    private void initFirebaseDatabase(){
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("posts");
    }
    //글 수정 버튼을 눌렀을경우 글의 작성자와 같으면 수정합니다.
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
    //닫기 버튼을 눌렀을 경우 화면을 종료합니다.
    public void clickClose(View view){
        finish();
    }
    //삭제 버튼을 눌렀을경우 글의 작성자와 현재 사용자가 같다면 삭제합니다.
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
    //현재 정보 버튼을 누르면 화면을 이동합니다.
    public void currentInfo(View view){
        Intent intent=new Intent(this, main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    //게시글 보기 버튼을 누르면 화면을 이동합니다.
    public void pushPostViewButton(View view) {
        Intent intent=new Intent(this, postsActivity.class);
        startActivity(intent);
        finish();
    }
    //현재 인터넷 접속이 되어있는지 확인합니다.
    private NetworkInfo getNetworkInfo(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }
    //내가 찾은 보물 버튼을 눌렀을 경우 화면을 이동합니다.
    public void myInfoButton(View view){
        Intent intent=new Intent(this, findPostsActivity.class);
        startActivity(intent);
        finish();
    }
}
