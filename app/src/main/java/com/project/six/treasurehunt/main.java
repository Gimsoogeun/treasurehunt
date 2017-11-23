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
            mDatabaseReference.push().setValue(message);
        }
    }

}
