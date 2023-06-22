package com.example.all100.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.all100.Home;
import com.example.all100.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;


public class Guard_reg extends AppCompatActivity {
    private TextView who1, who2;
    private FirebaseAuth mAuth;
    private ConstraintLayout finishsignup1;
    private TextInputLayout idfield, pwfield, pw1field, namefield, numfield, birthfield;
    private EditText big_address, small_address;
    private String id_text,pd_text, pd1_text, name_text, num_text, birth_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.so_guard_reg);

        mAuth = FirebaseAuth.getInstance(); //유저를 받아오기 위해서

        idfield = findViewById(R.id.idfield);
        pwfield = findViewById(R.id.pwfield);
        pw1field = findViewById(R.id.pw1field);
        namefield = findViewById(R.id.namefield);
        numfield = findViewById(R.id.numfield);
        birthfield = findViewById(R.id.birthfield);
        small_address = findViewById(R.id.small_address);
        big_address = findViewById(R.id.big_address);

        id_text = idfield.getEditText().getText().toString();
        pd_text = pwfield.getEditText().getText().toString();
        pd1_text = pw1field.getEditText().getText().toString();
        name_text = namefield.getEditText().getText().toString();
        num_text = numfield.getEditText().getText().toString();
        birth_text = birthfield.getEditText().getText().toString();

        finishsignup1 = findViewById(R.id.finishsignup1);
        finishsignup1.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.finishsignup1:
                    myStartActivity(Login_main.class);
                    break;

            }
        }
    };
    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
