package com.project.six.treasurehunt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class main extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mValueEventListener;

    public  EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(android.R.style.Theme_NoTitleBar);

        mEditText=(EditText)findViewById(R.id.testInput);
        initFirebaseDatabase();
    }

    private void initFirebaseDatabase(){
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("message");
    }


    public void pushButton(View view) {
        String message=mEditText.getText().toString();

        if (!TextUtils.isEmpty(message)) {
            mEditText.setText("");
            //child 는 밑에 항목을 만드는것 여기서라면 message 항목 밑에 ggg항목을 만들고
            // 임의의 key인 name을 무작위로 설정한후 그 무작위의 값을 thatit으로논다
            mDatabaseReference.child("ggg").push().setValue("thatit");

            //여기서는 message 밑에 임의의 name으로 key값을 만들고 그밑에 thisisparta라는 key name에 message값을 넣는다.
            mDatabaseReference.push().child("thisIsSparta").setValue(message);
        }
    }

}
