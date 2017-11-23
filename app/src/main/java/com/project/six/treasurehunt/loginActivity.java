package com.project.six.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class loginActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{
    //firebase -Authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;    //구글 api 클라이언트


    //firebase Realtime Database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private ValueEventListener mValueEventListener;

    //views
    //Sign in button
    SignInButton signInButton; //로그인 버튼
    Button signOutButton;  //로그아웃 버튼
    TextView mTxtProfileInfo;     //사용자 정보표시
    private ImageView mImgProfile; //사용자 프로필이미지

    private String userName; //사용자 이름

    TextView statusTextView;

    private static final int RC_SIGN_IN=9001;

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);

        //초기화함
        initViews();
        initFirebaseDatabase();
        initFirebaseAuth();

        Toast.makeText(this, "commit test", Toast.LENGTH_SHORT).show();
        textView=(TextView) findViewById(R.id.textview);
    }

    //뷰를 초기화함
    private void initViews(){
        signInButton=(SignInButton)findViewById(R.id.sign_in_button);
        signOutButton=(Button)findViewById(R.id.sign_out_button);
        signInButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);

        mTxtProfileInfo=(TextView) findViewById(R.id.txt_profile_info);
        mImgProfile=(ImageView)findViewById(R.id.img_profile);

    }
    //firebase databse 를 초기화함
    private void initFirebaseDatabase(){
        mFirebaseDatabase= FirebaseDatabase.getInstance();


        //파이어베이스 데이터베이스에서 가져올 벨류의 이름

        mDatabaseReference=mFirebaseDatabase.getReference("name");
        mValueEventListener =new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String msg= dataSnapshot.getValue().toString();
                textView.setText(msg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addValueEventListener(mValueEventListener);
    }

    //auth 관련 초기화함
    private void initFirebaseAuth(){
        mAuth=FirebaseAuth.getInstance();
        //구글 로그인
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,  this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               updateProfile();
            }
        };


    }
    //처음 켰을시 로그인이 안되어있다면 손님숫자.
    private void initValues() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            userName = "Guest" + new Random().nextInt(5000);
        } else {
            userName = user.getDisplayName();
        }
    }

    //auth 리스너에서 사용할 함수
    //현재 로그인 상태에 따라 view들을 숨기거나 보여줌.
    private void updateProfile(){
        FirebaseUser user=mAuth.getCurrentUser();
        if(user== null){
            //현재 로그인한 유저가 없다요!!
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
            mTxtProfileInfo.setVisibility(View.GONE);
            mImgProfile.setVisibility(View.GONE);
            //추가로 로그인 안됬을시 변경할것들 추가
            Toast.makeText(this,"현재 로그인이 안되어있다!!",Toast.LENGTH_LONG).show();
        }else{
            //로그인이 되어있다!!!
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
            mTxtProfileInfo.setVisibility(View.VISIBLE);
            mImgProfile.setVisibility(View.VISIBLE);

            userName=user.getDisplayName();
            mTxtProfileInfo.setText(userName);
            Picasso.with(this).load(user.getPhotoUrl()).into(mImgProfile);
            Toast.makeText(this,"현재 로그인이 되어있다!!",Toast.LENGTH_LONG).show();


        }
    }


    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
//        mGoogleApiClient.connect();

    }
    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener!=null){
           mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), ""+connectionResult, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }
    private void signIn(){
        Intent signInIntent=Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    private void signOut(){
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateProfile();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result= Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result){
        //로그인 결과가 성공이면 인증단계를 마무리함
        if(result.isSuccess()){
            GoogleSignInAccount acct=result.getSignInAccount();
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(loginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            updateProfile();
        }

    }
    public void clickedButton(View view){
        //파이어베이스 데이터베이스에서 가져올 벨류의 이름
        mDatabaseReference=mFirebaseDatabase.getReference("name");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String msg= dataSnapshot.getValue().toString();
                textView.setText(msg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void testNextActivity(View view){
        Intent intent=new Intent(this, main.class);
        startActivity(intent);
    }
}
