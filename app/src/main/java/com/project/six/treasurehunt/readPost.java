package com.project.six.treasurehunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class readPost extends AppCompatActivity {
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
        textViewtitle=(TextView)findViewById(R.id.readPostTitle);
        textViewcontext=(TextView)findViewById(R.id.readPostContext);

        textViewtitle.setText(post.title);
        textViewcontext.setText(post.context1);

        mAuth=FirebaseAuth.getInstance();

    }

    public void onRewrite(View view){
        FirebaseUser user=mAuth.getCurrentUser();
        if(user.getUid().equals(post.writerUID)) {
            Intent intent = new Intent(this, rewritePost.class);
            intent.putExtra("firebaseKey", post.firebaseKey);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this,"작성자만 수정할수 있습니다.",Toast.LENGTH_SHORT).show();
        }
    }

}
