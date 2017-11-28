package com.project.six.treasurehunt;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.GregorianCalendar;

public class writePost extends AppCompatActivity {

    


    public int sYear,sMonth,sDay,sHour,sMinute;

    public int eYear,eMonth,eDay,eHour,eMinute;

    long startDateTotal;
    long startTimeTotal;
    long endDateTotal;
    long endTimeTotal;

    long currentTimeTotal;
    long currentDateTotal;

    public GregorianCalendar cal;

    public TextView mStartDate;
    public TextView mStartTime;
    public TextView mEndDate;
    public TextView mEndTime;
    //시작시간을 정할때 쓰는 리스너
    DatePickerDialog.OnDateSetListener sDateListener= new  DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            sYear=year;
            sMonth=month;
            sDay=day;
            startTimeDateUpdate();
        }
    };
    //종료시간을 정할때 쓰는 리스너
    DatePickerDialog.OnDateSetListener eDateListener= new  DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            eYear=year;
            eMonth=month;
            eDay=day;
            endTimeDateUpdate();
        }
    };
    TimePickerDialog.OnTimeSetListener sTimeListener=new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int miniute) {
                  sHour=hour;
                  sMinute=miniute;
                  startTimeDateUpdate();
        }
    };
    TimePickerDialog.OnTimeSetListener eTimeListener=new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int miniute) {
            eHour=hour;
            eMinute=miniute;
            endTimeDateUpdate();
        }
    };

    public EditText titleET;
    public EditText context1;
    public EditText context2;

    double latitude;
    double longitude;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mValueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        Intent intent=getIntent();
        latitude=intent.getDoubleExtra("latitude",-9999);
        longitude=intent.getDoubleExtra("longitude",-9999);
        initView();
        initFirebaseDatabase();


    }
    public void initView(){
        titleET=(EditText)findViewById(R.id.titleET);
        context1=(EditText)findViewById(R.id.context1ET);
        context2=(EditText)findViewById(R.id.context2ET);
        mStartDate=(TextView)findViewById(R.id.startDate);
        mStartTime=(TextView)findViewById(R.id.startTime);
        mEndDate=(TextView)findViewById(R.id.endDate);
        mEndTime=(TextView)findViewById(R.id.endTime);
        cal=new GregorianCalendar();
        sYear=cal.get(java.util.Calendar.YEAR);
        sMonth=cal.get(java.util.Calendar.MONTH);
        sDay=cal.get(java.util.Calendar.DAY_OF_MONTH);
        sHour=cal.get(java.util.Calendar.HOUR_OF_DAY);
        sMinute=cal.get(java.util.Calendar.MINUTE);

        eYear=cal.get(java.util.Calendar.YEAR);
        eMonth=cal.get(java.util.Calendar.MONTH);
        eDay=cal.get(java.util.Calendar.DAY_OF_MONTH);
        eHour=cal.get(java.util.Calendar.HOUR_OF_DAY);
        eMinute=cal.get(java.util.Calendar.MINUTE);
        startTimeDateUpdate();
        endTimeDateUpdate();
    }


    void startTimeDateUpdate(){

        mStartDate.setText(String.format("%d/%d/%d", sYear, sMonth + 1, sDay));
        mStartTime.setText(String.format("%d:%d", sHour, sMinute));

    }
    void endTimeDateUpdate(){

        mEndDate.setText(String.format("%d/%d/%d", eYear, eMonth + 1, eDay));
        mEndTime.setText(String.format("%d:%d", eHour, eMinute));

    }
    private void initFirebaseDatabase(){
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("posts");
    }
    public void onClickStartTime(View view){
        new DatePickerDialog(writePost.this, sDateListener, sYear,
                sMonth, sDay).show();
        new TimePickerDialog(writePost.this,sTimeListener,sHour,sMinute,true).show();


    }
    public void onClickEndTime(View view){
        new DatePickerDialog(writePost.this, eDateListener, eYear,
                eMonth, eDay).show();
        new TimePickerDialog(writePost.this,eTimeListener,eHour,eMinute,true).show();

    }
    public void onClickWrite(View view){
        NetworkInfo mNetworkState=getNetworkInfo();

        if(mNetworkState!=null &&mNetworkState.isConnected()) {
            //선택시작시간이 현재시간보다 늦으면 안됨.
            startDateTotal= (sYear*10000) + (sMonth*100)+ sDay;
            endDateTotal=(eYear*10000) + (eMonth*100)+ eDay;
            startTimeTotal=(sHour*100) + (sMinute);
            endTimeTotal=(eHour*100) + (eMinute);

            currentDateTotal=(cal.get(java.util.Calendar.YEAR)*10000)+(cal.get(java.util.Calendar.MONTH)*100)+(cal.get(java.util.Calendar.DAY_OF_MONTH));
            currentTimeTotal=(cal.get(java.util.Calendar.HOUR_OF_DAY)*100)+cal.get(java.util.Calendar.MINUTE);
            if(startDateTotal == currentDateTotal){
                if(startTimeTotal< currentTimeTotal){
                    Toast.makeText(this, "시작시간이 현재 시간보다 과거입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }else if(startDateTotal<currentDateTotal){
                Toast.makeText(this, "시작시간이 현재 시간보다 과거입니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(endDateTotal == startDateTotal){
                if(endTimeTotal <= startTimeTotal){
                    Toast.makeText(this, "종료시간이 시작시간과 같거나 작습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }else if(endDateTotal < startDateTotal){
                Toast.makeText(this, "종료시간이 시작시간과 같거나 작습니다.", Toast.LENGTH_SHORT).show();
                return;
            }


            if (titleET.getText().toString().equals("")) {
                Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (context1.getText().toString().equals("")) {
                Toast.makeText(this, "내용 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (context2.getText().toString().equals("")) {
                Toast.makeText(this, "보상 내용 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            //postContext post=new postContext(titleET.getText().toString(),context1.getText().toString(),context2.getText().toString());

            postContext post = new postContext();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            post.title = titleET.getText().toString();
            post.context1 = context1.getText().toString();
            post.context2 = context2.getText().toString();
            post.writerName = user.getDisplayName();
            post.writerUID = user.getUid();
            post.latitude=latitude;
            post.longitude=longitude;
            post.startDate=startDateTotal;
            post.starttime=startTimeTotal;
            post.endDate=endDateTotal;
            post.endTime=endTimeTotal;
            mDatabaseReference.push().setValue(post, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast.makeText(getApplicationContext(), "작성에 실패했습니다.", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "작성에 성공했습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();

        }
    }
    public void currentInfo(View view){
        Intent intent=new Intent(this, main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
    public void pushPostViewButton(View view) {
        Intent intent=new Intent(this, postsActivity.class);
        startActivity(intent);
        finish();
    }
    public void myInfoButton(View view){
        Intent intent=new Intent(this, findPostsActivity.class);
        startActivity(intent);
        finish();
    }
    private NetworkInfo getNetworkInfo(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }
}
