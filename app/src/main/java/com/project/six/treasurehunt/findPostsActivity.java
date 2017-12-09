package com.project.six.treasurehunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//findPostActivity는 사용자가 발견한 게시물들을 보여주는 Ativity화면입니다.
public class findPostsActivity extends AppCompatActivity {
    //firebaseDatabase 사용하기 위한 변수
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    //유저 정보를 받아오기위해 FirebaseAuth 를 사용합니다.
    FirebaseAuth mAuth;
    //발견된 게시물들을 listview에 적용하기위한 adapter입니다.
    openpostAdapter mAdapter;
    //Activity에 발견된 게시물들을 listview로 보여줍니다.
    ListView mListView;
    //액티비티 생성시 view를 초기화하고 firebase 관련 변수들을 초기화합니다.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_posts);
        initView();
        initFirebaseDatabase();
    }
    //firebase관련 변수들을 초기화합니다.
    private void initFirebaseDatabase(){
        mAuth=FirebaseAuth.getInstance();
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
        mDatabaseReference.orderByChild("finderUID").equalTo(mAuth.getCurrentUser().getUid()).addChildEventListener(mChildEventListener);
    }
    //activity의 view들을 초기화합니다.
    public void initView(){
        mListView=(ListView)findViewById(R.id.postlist);
        mAdapter=new openpostAdapter(this,0);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                postContext post =mAdapter.getItem(i);
                selectPost(post);
            }
        });
    }
    //게시글을 클릭했을때 게시물을 보여주는 activity인 findTreasureActivity로 화면이 이동합니다.
    public void selectPost(postContext post){
        Intent intent=new Intent(this, findTreasureActivity.class);
        intent.putExtra("firebaseKey",post.firebaseKey);
        startActivity(intent);

    }
    //상단의 버튼중 "현재정보"를 선택시 main으로 이동합니다.
    public void currentInfo(View view){
        Intent intent=new Intent(this, main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    //상단의 버튼중 "게시글 보기"를 선택시 postActivity으로 이동합니다.
    public void pushPostViewButton(View view) {
        Intent intent=new Intent(this, postsActivity.class);
        startActivity(intent);
        finish();
    }
}
