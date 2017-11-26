package com.project.six.treasurehunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class postsActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    postAdapter mAdapter;
    ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        initView();
        initFirebaseDatabase();
    }
    private void initFirebaseDatabase(){
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("posts");
        mChildEventListener= new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                postContext post=dataSnapshot.getValue(postContext.class);
                post.firebaseKey=dataSnapshot.getKey();
                mAdapter.add(post);
                mListView.smoothScrollToPosition(mAdapter.getCount());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String firebaseKey= dataSnapshot.getKey();
                int count=mAdapter.getCount();
                for(int i=0; i< count; i++){
                    if(mAdapter.getItem(i).firebaseKey.equals(firebaseKey)){
                        mAdapter.remove(mAdapter.getItem(i));
                        postContext post=dataSnapshot.getValue(postContext.class);
                        post.firebaseKey=dataSnapshot.getKey();
                        mAdapter.add(post);
                        mListView.smoothScrollToPosition(mAdapter.getCount());

                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String firebaseKey= dataSnapshot.getKey();
                int count=mAdapter.getCount();
                for(int i=0; i< count; i++){
                    if(mAdapter.getItem(i).firebaseKey.equals(firebaseKey)){
                        mAdapter.remove(mAdapter.getItem(i));
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.orderByChild("isfinded").equalTo(false).addChildEventListener(mChildEventListener);
    }
    public void initView(){
        mListView=(ListView)findViewById(R.id.postlist);
        mAdapter=new postAdapter(this,0);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               postContext post =mAdapter.getItem(i);
               selectPost(post);
            }
        });
    }

    public void selectPost(postContext post){
        Intent intent=new Intent(this, readPost.class);
        intent.putExtra("title",post.title);
        intent.putExtra("context1",post.context1);
        intent.putExtra("firebaseKey",post.firebaseKey);
        intent.putExtra("writerUID",post.writerUID);
        intent.putExtra("imageURL1",post.imageURL1);
        startActivity(intent);

    }
    public void currentInfo(View view){
        Intent intent=new Intent(this, main.class);
        startActivity(intent);
        finish();
    }
    public void pushPostViewButton(View view) {

    }
    public void myInfoButton(View view){
        Intent intent=new Intent(this, findPostsActivity.class);
        startActivity(intent);
        finish();
    }
}
