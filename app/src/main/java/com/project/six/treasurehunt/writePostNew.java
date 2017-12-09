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
import android.os.Handler;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.GregorianCalendar;

//새로운 게시글을 작성하는 화면입니다.
public class writePostNew extends AppCompatActivity {

    //이미지 업로드를 위한 경로 storage
    String Storage_Path="All_Image_Uploads/";
    //이미지 업로드하기 버튼
    Button ChooseButton1, ChooseButton2;
    // Creating ImageView.
    ImageView SelectImage1, SelectImage2;
    // Creating URI. 업로드할 파일의 경로를 얻습니다.
    Uri FilePathUri1;
    Uri FilePathUri2;

    //데이터베이스에 저장될 url 입니다.
    String image1URL;
    String image2URL;
    //이미지가 업로드 성공했는지 아닌지 확인하기위한 boolean변수들입니다
    boolean isEnded1=false;
    boolean isEnded2=false;
    boolean imGoingtoDo=false;

    //업로드할 게시글의 정보를 post에 담습니다.
    postContext post;
    //이미지를 업로드하기 위해 firestorage사용
    StorageReference storageReference;
    // onActivityResult() 에서 결과값을 받기 위해 사용합니다
    int Image_Request_Code1 = 7;
    int Image_Request_Code2 = 8;
    //이미지 업로드중이라는것을 보여주는 창
    ProgressDialog progressDialog ;
    ProgressDialog progressDialog2;

    //시간을 계산하기위함.
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

    //views
    public EditText titleET;
    public EditText context1;
    public EditText context2;

    //게시글을 작성하는 위치입니다.
    double latitude;
    double longitude;
    //firebase를 사용하기위한 변수입니다.
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    //activity시작시 뷰를 초기화하고 firebase를 초기화합니다.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post_new);
        Intent intent=getIntent();
        latitude=intent.getDoubleExtra("latitude",-9999);
        longitude=intent.getDoubleExtra("longitude",-9999);
        post = new postContext();
        initView();
        initFirebaseDatabase();

    }
    //view를 초기화합니다.
    public void initView(){

        SelectImage1=(ImageView)findViewById(R.id.desImage);
        SelectImage2=(ImageView)findViewById(R.id.rewordImage);
        progressDialog=new ProgressDialog(this);
        progressDialog2=new ProgressDialog(this);

        ChooseButton1=(Button)findViewById(R.id.ChooseButton1);
        ChooseButton2=(Button)findViewById(R.id.ChooseButton2);

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
    //이미지 추가 버튼을 눌렀을경우 저장장치에서 image를 찾습니다.
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
    //이미지 추가에 대한 결과를 확인합니다. image_request_code1은 힌트에 사용될 이미지에 대한 결과입니다. 2는 보상내용에 사용될 이미지입니다.
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
    //시작시간과 종료시간을 view에 나타냅니다.
    void startTimeDateUpdate(){

        mStartDate.setText(String.format("%d/%d/%d", sYear, sMonth + 1, sDay));
        mStartTime.setText(String.format("%d:%d", sHour, sMinute));

    }
    void endTimeDateUpdate(){

        mEndDate.setText(String.format("%d/%d/%d", eYear, eMonth + 1, eDay));
        mEndTime.setText(String.format("%d:%d", eHour, eMinute));

    }
    //firebase를 초기화합니다.
    private void initFirebaseDatabase(){
        storageReference= FirebaseStorage.getInstance().getReference();
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("posts");
    }
    //시작시간과 종료시간을 사용자가 설정하도록 합니다.
    public void onClickStartTime(View view){
        new DatePickerDialog(writePostNew.this, sDateListener, sYear,
                sMonth, sDay).show();
        new TimePickerDialog(writePostNew.this,sTimeListener,sHour,sMinute,true).show();
    }
    public void onClickEndTime(View view){
        new DatePickerDialog(writePostNew.this, eDateListener, eYear,
                eMonth, eDay).show();
        new TimePickerDialog(writePostNew.this,eTimeListener,eHour,eMinute,true).show();
    }
    //글 작성을 눌렀을경우 작성하기 적합한지 확인합니다.
    //현재 시각보다 게시글을 탐색가능한 시각이 늦어야하고,
    //게시글 탐색 종료시간이 시작시간보다 늦어야합니다.
    //또한 인터넷에 연결되어있어야합니다.
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
                                Toast.makeText(getApplicationContext(), "이미지가 성공적으로 업로드되었습니다.", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(writePostNew.this, exception.getMessage(), Toast.LENGTH_LONG).show();
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
                                Toast.makeText(getApplicationContext(), "이미지가 성공적으로 업로드 되었습니다.", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(writePostNew.this, exception.getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();

        }
    }
    //업로드를 끝마쳤는지 확인합니다.
    public void endUpload(){
        if(isEnded1 && isEnded2 && !imGoingtoDo){
            pushVeluetoDatabase();
        }
    }
    //데이터베이스에 게시글을 저장하도록합니다.
    public void pushVeluetoDatabase(){
        imGoingtoDo=true;
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
    }
    //현재 정보를 눌렀을 경우 화면을 이동합니다
    public void currentInfo(View view){
        Intent intent=new Intent(this, main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    //게시글보기 버튼을 눌렀을 경우 화면을 이동합니다.
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
    //네트워크가 접속되어있는지 확인합니다.
    private NetworkInfo getNetworkInfo(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }
}
