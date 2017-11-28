package com.project.six.treasurehunt;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class main extends FragmentActivity implements OnMapReadyCallback {
    //초기 마커 위치입니다. 지도에 표기됨.
    private static final LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
    double latitude;
    double longitude;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mValueEventListener;
    private ChildEventListener mChildEventListener;
    public GregorianCalendar cal;
    long currentTimeTotal;
    long currentDateTotal;


    private ArrayList<Marker> TreasureMarkers;
    private Marker currentMarker=null;
    private GoogleMap googleMap;

    FirebaseAuth mauth;
    String userName;
    FusedLocationProviderClient mFusedLocatioinClient;
    LocationCallback mLocationCallback;
    Location mCurrentLocation;
    LocationRequest mLocationRequest;

    //query는 현재 위치에서 찾을수있는 보물을 검색하는데 쓰임
    //query2는 로그인한 유저가 묻은 보물들을 보여주기위해 쓰임
    Query query;
    Query query2;


    //보물발견시 효과음 재생
    SoundPool soundPool;
    int soundid;

    public  EditText mEditText;

    postAdapter mAdapter;
    postContext post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPool=new SoundPool(1, AudioManager.STREAM_ALARM,0);
        soundid=soundPool.load(this,R.raw.panpare,1);

        TreasureMarkers=new ArrayList<Marker>();
        mAdapter=new postAdapter(this,0);
        initView();
        initFirebaseDatabase();

        mFusedLocatioinClient=LocationServices.getFusedLocationProviderClient(this);
        createLocationCallback();
        createLocationRequest();
        getCurrentLocation();

    }
    private void getCurrentLocation(){
        String[] permissions =new String[]{android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            for(String permission:permissions){
                int result= PermissionChecker.checkSelfPermission(this,permission);
                if(result==PermissionChecker.PERMISSION_GRANTED){

                }else {
                    ActivityCompat.requestPermissions(this,permissions,1);
                }
            }
        }
        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocatioinClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
    }

    private void createLocationCallback(){
        mLocationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);
                mCurrentLocation =locationResult.getLastLocation();
                setCurrentLocation(mCurrentLocation,"현재 위치",userName);
            }
        };
    }
    private void createLocationRequest(){
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private void initFirebaseDatabase(){
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("posts");
        mauth=FirebaseAuth.getInstance();
        FirebaseUser user=mauth.getCurrentUser();
        userName=user.getDisplayName();

        query=mDatabaseReference.orderByChild("isfinded").equalTo(false);
        query2=mDatabaseReference.orderByChild("writerUID").equalTo(user.getUid());

        mValueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentDateTotal=(cal.get(java.util.Calendar.YEAR)*10000)+(cal.get(java.util.Calendar.MONTH)*100)+(cal.get(java.util.Calendar.DAY_OF_MONTH));
                currentTimeTotal=(cal.get(Calendar.HOUR_OF_DAY)*100)+cal.get(java.util.Calendar.MINUTE);
                long temptTime=(currentDateTotal*10000) + (currentTimeTotal);
                long startTimer;
                long endTimer;
                if (dataSnapshot.exists()) {
                    for(DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                        post = dataSnapshots.getValue(postContext.class);
                        post.firebaseKey=dataSnapshots.getKey();
                        startTimer=(post.startDate*10000)+post.starttime;
                        endTimer=(post.endDate*10000)+post.endTime;
                        //시간과 공간이 적합한 녀석이 발견됬다면!
                        if( ((longitude-0.00007) <= post.longitude) && ((longitude+0.00007) >= post.longitude )) {
                            if(((latitude-0.00007) <= post.latitude) && ((latitude+0.00007) >= post.latitude )) {
                                if( (startTimer <= temptTime) && ( temptTime <= endTimer) ) {
                                    getReword(post.firebaseKey);
                                    soundPool.play(soundid,1.0f,1.0f,1,0,1.0f);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        //
        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                postContext postcon=dataSnapshot.getValue(postContext.class);
                postcon.firebaseKey=dataSnapshot.getKey();
                mAdapter.add(postcon);
                setTreasureMark();
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
                    }
                }
                setTreasureMark();
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
                setTreasureMark();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query2.addChildEventListener(mChildEventListener);
    }
    public void getReword(String key){
        Intent intent=new Intent(this,findTreasureActivity.class);
        intent.putExtra("firebaseKey",key);
        startActivity(intent);
    }
    protected void onResume(){
        super.onResume();
        getCurrentLocation();
    }
    protected void onStop(){
        super.onStop();
        mFusedLocatioinClient.removeLocationUpdates(mLocationCallback);
    }


    private void initView(){
        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        cal=new GregorianCalendar();

    }
    public void clickDigButton(View view){
        query.addListenerForSingleValueEvent(mValueEventListener);
    }
    public void myInfoButton(View view){
        Intent intent=new Intent(this, findPostsActivity.class);
        startActivity(intent);
    }
    public void pushButton(View view) {

        Intent intent=new Intent(this, writePostNew.class);
        intent.putExtra("latitude",latitude);
        intent.putExtra("longitude",longitude);

        startActivity(intent);
    }
    public void pushPostViewButton(View view) {
        Intent intent=new Intent(this, postsActivity.class);
        startActivity(intent);
    }

    public void currentInfo(View view){

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // 구글 맵 객체를 불러온다.
        this.googleMap = googleMap;

        // 서울에 대한 위치 설정
        LatLng seoul = new LatLng(37.52487, 126.92723);

        UiSettings mapSetting;
        mapSetting=this.googleMap.getUiSettings();
        mapSetting.setZoomControlsEnabled(true);
        googleMap.setMinZoomPreference(14);
        //카메라를 서울 위치로 옮긴다.
    }
    public void setTreasureMark(){
        for(int i=0; i<TreasureMarkers.size(); i++){
            TreasureMarkers.get(i).remove();
        }
        TreasureMarkers.clear();
        int count= mAdapter.getCount();
        for(int i=0 ; i < count; i++){
            if(!mAdapter.getItem(i).isfinded){
                MarkerOptions markerOptions= new MarkerOptions();
                LatLng latLng=new LatLng(mAdapter.getItem(i).latitude,mAdapter.getItem(i).longitude);
                markerOptions.position(latLng);
                markerOptions.title(mAdapter.getItem(i).title);
                markerOptions.snippet("내가 숨긴 보물");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                TreasureMarkers.add(this.googleMap.addMarker(markerOptions));
            }
        }
    }
    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if ( currentMarker != null ){
            currentMarker.remove();
        }

        if ( location != null) {
            //현재위치의 위도 경도 가져옴
            LatLng currentLocation = new LatLng( location.getLatitude(), location.getLongitude());
            latitude=location.getLatitude();
            longitude=location.getLongitude();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currentMarker = this.googleMap.addMarker(markerOptions);
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
         //   this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = this.googleMap.addMarker(markerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));
      //  this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));
    }

}
