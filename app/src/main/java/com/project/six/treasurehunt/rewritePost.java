package com.project.six.treasurehunt;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.GregorianCalendar;

public class rewritePost extends AppCompatActivity {
    public int sYear,sMonth,sDay,sHour,sMinute;

    public int eYear,eMonth,eDay,eHour,eMinute;

    long startDateTotal;
    long startTimeTotal;
    long endDateTotal;
    long endTimeTotal;

    long currentTimeTotal;
    long currentDateTotal;

    public GregorianCalendar cal;


    String postfirebaseKey;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mValueEventListener;

    postContext post;
    public TextView mStartDate;
    public TextView mStartTime;
    public TextView mEndDate;
    public TextView mEndTime;
    EditText titleEdittext;
    EditText context1Edittext;
    EditText context2Edittext;
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
    void startTimeDateUpdate(){

        mStartDate.setText(String.format("%d/%d/%d", sYear, sMonth + 1, sDay));
        mStartTime.setText(String.format("%d:%d", sHour, sMinute));

    }
    void endTimeDateUpdate(){

        mEndDate.setText(String.format("%d/%d/%d", eYear, eMonth + 1, eDay));
        mEndTime.setText(String.format("%d:%d", eHour, eMinute));

    }
    public void onClickStartTime(View view){
        new DatePickerDialog(rewritePost.this, sDateListener, sYear,
                sMonth, sDay).show();
        new TimePickerDialog(rewritePost.this,sTimeListener,sHour,sMinute,true).show();


    }
    public void onClickEndTime(View view){
        new DatePickerDialog(rewritePost.this, eDateListener, eYear,
                eMonth, eDay).show();
        new TimePickerDialog(rewritePost.this,eTimeListener,eHour,eMinute,true).show();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewrite_post);
        Intent intent=getIntent();
        postfirebaseKey=intent.getStringExtra("firebaseKey");
        initView();
        initFirebaseDatabase();
    }
    public void initView(){
        titleEdittext=(EditText)findViewById(R.id.RWtitleET);
        context1Edittext=(EditText)findViewById(R.id.RWcontext1ET);
        context2Edittext=(EditText)findViewById(R.id.RWcontext2ET);
        mStartDate=(TextView)findViewById(R.id.startDate);
        mStartTime=(TextView)findViewById(R.id.startTime);
        mEndDate=(TextView)findViewById(R.id.endDate);
        mEndTime=(TextView)findViewById(R.id.endTime);
        cal=new GregorianCalendar();
    }
    private void initFirebaseDatabase(){
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("posts").child(postfirebaseKey);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post=dataSnapshot.getValue(postContext.class);
                titleEdittext.setText(post.title);
                context1Edittext.setText(post.context1);
                context2Edittext.setText(post.context2);
                sHour=(int)post.starttime/100;
                sMinute=(int)((post.starttime)-(sHour*100));
                sYear=(int)post.startDate/10000;
                sMonth=(int)(post.startDate-(sYear*10000))/100;
                sDay=(int)(post.startDate-(sYear*10000)-(sMonth*100));
                startTimeDateUpdate();
                eHour=(int)post.endTime/100;
                eMinute=(int)((post.endTime)-(eHour*100));
                eYear=(int)post.endDate/10000;
                eMonth=(int)(post.endDate-(eYear*10000))/100;
                eDay=(int)(post.endDate-(eYear*10000)-(eMonth*100));
                endTimeDateUpdate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Query query;

    }
    public void onClickReWrite(View view){
        NetworkInfo mNetworkState=getNetworkInfo();
        if( mNetworkState!=null && mNetworkState.isConnected()) {
            //선택시작시간이 현재시간보다 늦으면 안됨.
            startDateTotal= (sYear*10000) + (sMonth*100)+ sDay;
            endDateTotal=(eYear*10000) + (eMonth*100)+ eDay;
            startTimeTotal=(sHour*100) + (sMinute);
            endTimeTotal=(eHour*100) + (eMinute);

            currentDateTotal=(cal.get(java.util.Calendar.YEAR)*10000)+(cal.get(java.util.Calendar.MONTH)*100)+(cal.get(java.util.Calendar.DAY_OF_MONTH));
            currentTimeTotal=(cal.get(java.util.Calendar.HOUR)*100)+cal.get(java.util.Calendar.MINUTE);
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
            if (titleEdittext.getText().toString().equals("")) {
                Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (context1Edittext.getText().toString().equals("")) {
                Toast.makeText(this, "내용 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (context2Edittext.getText().toString().equals("")) {
                Toast.makeText(this, "보상 내용 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            post.title = titleEdittext.getText().toString();
            post.context1 = context1Edittext.getText().toString();
            post.context2 = context2Edittext.getText().toString();
            post.startDate=startDateTotal;
            post.starttime=startTimeTotal;
            post.endDate=endDateTotal;
            post.endTime=endTimeTotal;
            mDatabaseReference.setValue(post, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast.makeText(getApplicationContext(), "글 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "글 수정에 성공했습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
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
}
