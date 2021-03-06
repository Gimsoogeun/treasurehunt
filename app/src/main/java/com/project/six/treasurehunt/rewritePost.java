package com.project.six.treasurehunt;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

//글을 재작성하는 activity입니다.
public class rewritePost extends AppCompatActivity {
    //이미지 업로드를 위한 경로, firebasestorage 에 저장되는 위치입니다.
    String Storage_Path="All_Image_Uploads/";
    Button ChooseButton1, ChooseButton2;
    // 이미지들을 보여주기위해 사용하는 imageview들입니다.
    ImageView SelectImage1, SelectImage2;
    // 업로드할 파일의 경로를 얻습니다.
    Uri FilePathUri1;
    Uri FilePathUri2;
    //데이터베이스에 저장될 url 입니다.
    String image1URL;
    String image2URL;
    boolean isEnded1=false;
    boolean isEnded2=false;
    boolean imGoingtoDo=false;
    //이미지를 업로드하기 위해 firestorage사용
    StorageReference storageReference;
    //이미지 업로드중이라는것을 보여주는 창입니다.
    ProgressDialog progressDialog ;
    ProgressDialog progressDialog2;
    //  onActivityResult() 에서 결과값을 받아올때 사용합니다.
    int Image_Request_Code1 = 7;
    int Image_Request_Code2 = 8;

    //게시글을 발견할수있는 시작시간과 종료시간입니다
    public int sYear,sMonth,sDay,sHour,sMinute;
    public int eYear,eMonth,eDay,eHour,eMinute;
    long startDateTotal;
    long startTimeTotal;
    long endDateTotal;
    long endTimeTotal;
    //현재 시각을 나타냅니다.
    long currentTimeTotal;
    long currentDateTotal;
    public GregorianCalendar cal;

    //게시글의 firebasekey를 나타냅니다.
    String postfirebaseKey;

    //firebase를 사용하기위한 변수들입니다.
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    //view들과 게시글의 내용을 저장할 postContext입니다.
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
    //시작시간과 날짜를 view에 나타냅니다.
    void startTimeDateUpdate(){

        mStartDate.setText(String.format("%d/%d/%d", sYear, sMonth + 1, sDay));
        mStartTime.setText(String.format("%d:%d", sHour, sMinute));

    }
    //종료시간과 날짜를 view에 나타냅니다.
    void endTimeDateUpdate(){

        mEndDate.setText(String.format("%d/%d/%d", eYear, eMonth + 1, eDay));
        mEndTime.setText(String.format("%d:%d", eHour, eMinute));

    }
    //사용자가 시작시간을 설정합니다.
    public void onClickStartTime(View view){
        new DatePickerDialog(rewritePost.this, sDateListener, sYear,
                sMonth, sDay).show();
        new TimePickerDialog(rewritePost.this,sTimeListener,sHour,sMinute,true).show();


    }
    //사용자가 종료시간을 설정합니다.
    public void onClickEndTime(View view){
        new DatePickerDialog(rewritePost.this, eDateListener, eYear,
                eMonth, eDay).show();
        new TimePickerDialog(rewritePost.this,eTimeListener,eHour,eMinute,true).show();

    }
    //activity가 시작시 뷰를 초기화하고 firebase를 초기화합니다.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewrite_post);
        Intent intent=getIntent();
        postfirebaseKey=intent.getStringExtra("firebaseKey");
        initView();
        initFirebaseDatabase();
    }
    //view를 초기화합니다.
    public void initView(){
        titleEdittext=(EditText)findViewById(R.id.RWtitleET);
        context1Edittext=(EditText)findViewById(R.id.RWcontext1ET);
        context2Edittext=(EditText)findViewById(R.id.RWcontext2ET);
        mStartDate=(TextView)findViewById(R.id.startDate);
        mStartTime=(TextView)findViewById(R.id.startTime);
        mEndDate=(TextView)findViewById(R.id.endDate);
        mEndTime=(TextView)findViewById(R.id.endTime);
        SelectImage1=(ImageView)findViewById(R.id.desImage);
        SelectImage2=(ImageView)findViewById(R.id.rewordImage);
        progressDialog=new ProgressDialog(this);
        progressDialog2=new ProgressDialog(this);
        ChooseButton1=(Button)findViewById(R.id.chooseButton1);
        ChooseButton2=(Button)findViewById(R.id.chooseButton2);

        cal=new GregorianCalendar();
    }
    //이미지 추가버튼을 눌렀을경우 이미지를 찾도록합니다.
    public void ChooseButton1Click(View view){
        Intent intent=new Intent();
        // Setting intent type as image to select image from phone storage.
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code1);
    }
    public void ChooseButton2Click(View view){
        Intent intent=new Intent();
        // Setting intent type as image to select image from phone storage.
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code2);
    }
    //이미지가 선택되었을때 버튼의 text를 변경합니다. 그리고 이미지의 uri를 받아옵니다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri1 = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri1);

                // Setting up bitmap selected image into ImageView.
                SelectImage1.setImageBitmap(bitmap);

                // After selecting image change choose button above text.
                ChooseButton1.setText("이미지 선택됨");

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }else if(requestCode == Image_Request_Code2 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri2 = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri2);

                // Setting up bitmap selected image into ImageView.
                SelectImage2.setImageBitmap(bitmap);

                // After selecting image change choose button above text.
                ChooseButton2.setText("이미지 선택됨");

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
    //URI 를 파일로 변환하여 STORAGE 에 업로드하기위한 함수입니다.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }
    //firebase와 관련된 변수를 초기화하고 리스너를 추가합니다.
    private void initFirebaseDatabase(){
        storageReference= FirebaseStorage.getInstance().getReference();
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
                Picasso.with(getApplicationContext()).load(post.imageURL1).into(SelectImage1);
                Picasso.with(getApplicationContext()).load(post.imageURL2).into(SelectImage2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Query query;

    }
    //수정 버튼을 눌렀을경우 게시글이 수정하기 적합하닞 확인합니다.
    //시작시간은 현재 시간보다 뒤여야하고, 종료시간은 시작시간보다 뒤여야합니다.
    //문제가 없다면 이미지가 업로드 될때까지 대기하도록합니다.
    //모든게 문제없이 진행됬다면 수정창을 종료합니다.
    public void onClickReWrite(View view){
        NetworkInfo mNetworkState=getNetworkInfo();
        if( mNetworkState!=null && mNetworkState.isConnected()) {
            //선택시작시간이 현재시간보다 늦으면 안됨.
            startDateTotal= (sYear*10000) + (sMonth*100)+ sDay;
            endDateTotal=(eYear*10000) + (eMonth*100)+ eDay;
            startTimeTotal=(sHour*100) + (sMinute);
            endTimeTotal=(eHour*100) + (eMinute);

            currentDateTotal=(cal.get(java.util.Calendar.YEAR)*10000)+(cal.get(java.util.Calendar.MONTH)*100)+(cal.get(java.util.Calendar.DAY_OF_MONTH));
            currentTimeTotal=(cal.get(Calendar.HOUR_OF_DAY)*100)+cal.get(java.util.Calendar.MINUTE);
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


            // Checking whether FilePathUri Is empty or not.
            if (FilePathUri1 != null) {
                isEnded1=false;
                // Setting progressDialog Title.
                progressDialog.setTitle("Image is Uploading...");
                // Showing progressDialog.
                progressDialog.show();
                // Creating second StorageReference.
                StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri1));
                // Adding addOnSuccessListener to second StorageReference.
                storageReference2nd.putFile(FilePathUri1)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Getting image name from EditText and store into string variable.
                                String TempImageName = "image1";
                                // Hiding the progressDialog after done uploading.
                                progressDialog.dismiss();
                                // Showing toast message after done uploading.
                                Toast.makeText(getApplicationContext(), "이미지가 업로드 되었습니다. ", Toast.LENGTH_SHORT).show();
                                image1URL=taskSnapshot.getDownloadUrl().toString();
                                post.imageURL1=image1URL;
                                isEnded1=true;
                                endUpload();

                            }
                        })
                        // If something goes wrong .
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Hiding the progressDialog.
                                progressDialog.dismiss();
                                // Showing exception erro message.
                                Toast.makeText(rewritePost.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                                isEnded1=true;
                                endUpload();

                            }
                        })

                        // On progress change upload time.
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                // Setting progressDialog Title.
                                progressDialog.setTitle("Image is Uploading...");
                            }
                        });
            }else {
                isEnded1=true;
            }
            // Checking whether FilePathUri Is empty or not.
            if (FilePathUri2 != null) {
                isEnded2=false;
                // Setting progressDialog Title.
                progressDialog2.setTitle("Image is Uploading...");
                // Showing progressDialog.
                progressDialog2.show();
                // Creating second StorageReference.
                StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri2));
                // Adding addOnSuccessListener to second StorageReference.
                storageReference2nd.putFile(FilePathUri2)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Getting image name from EditText and store into string variable.
                                String TempImageName = "image2";
                                // Hiding the progressDialog after done uploading.
                                progressDialog2.dismiss();
                                // Showing toast message after done uploading.
                                Toast.makeText(getApplicationContext(), "이미지가 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
                                image2URL=taskSnapshot.getDownloadUrl().toString();
                                post.imageURL2=image2URL;
                                isEnded2=true;
                                endUpload();

                            }
                        })
                        // If something goes wrong .
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Hiding the progressDialog.
                                progressDialog2.dismiss();
                                // Showing exception erro message.
                                Toast.makeText(rewritePost.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                                isEnded2=true;
                                endUpload();

                            }
                        })
                        // On progress change upload time.
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                // Setting progressDialog Title.
                                progressDialog2.setTitle("Image is Uploading...");
                            }
                        });
            }else {
                isEnded2=true;
            }
            endUpload();
        }else{
            Toast.makeText(this, "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }
    //이미지 업로드가 끝났을경우에만 데이터베이스에 게시글을 추가하도록합니다.
    public void endUpload(){
        if(isEnded1 && isEnded2 && !imGoingtoDo){
            pushVeluetoDatabase();
        }
    }
    //게시글을 추가합니다.
    public void pushVeluetoDatabase(){
        imGoingtoDo=true;
        mDatabaseReference.setValue(post, new DatabaseReference.CompletionListener() {
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
    }
    //현재 정보 버튼을 눌렀을 경우 화면을 이동합니다
    public void currentInfo(View view){
        Intent intent=new Intent(this, main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    //게시글보기를 눌렀을경우 화면을 이동합니다.
    public void pushPostViewButton(View view) {
        Intent intent=new Intent(this, postsActivity.class);
        startActivity(intent);
        finish();
    }
    //내가 찾은 보물을 눌렀을 경우 화면을 이동합니다
    public void myInfoButton(View view){
        Intent intent=new Intent(this, findPostsActivity.class);
        startActivity(intent);
        finish();
    }
    //현재 인터넷이 접속되어있는지 확인합니다.
    private NetworkInfo getNetworkInfo(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }
}
