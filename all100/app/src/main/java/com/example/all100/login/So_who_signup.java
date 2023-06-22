package com.example.all100.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

public class So_who_signup extends AppCompatActivity {
    private TextView guardgo, sickgo;
    private ConstraintLayout nextpage;
    private int whochoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.so_who_signup);

        guardgo = findViewById(R.id.guardgo);
        sickgo = findViewById(R.id.sickgo);
        nextpage = findViewById(R.id.nextpage_who);

        guardgo.setOnClickListener(onClickListener);
        sickgo.setOnClickListener(onClickListener);
        nextpage.setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.guardgo:
                    guardgo.setBackgroundColor(Color.parseColor("#98BBEB"));
                    sickgo.setBackgroundColor(Color.WHITE);
                    whochoose = 0;
                    break;
                case R.id.who2:
                    sickgo.setBackgroundColor(Color.parseColor("#98BBEB"));
                    guardgo.setBackgroundColor(Color.WHITE);
                    whochoose = 1;
                    break;
                case R.id.nextpage:
                    myStartActivity(Guard_reg.class, whochoose);
                    break;

            }
        }
    };
    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void myStartActivity(Class c, int i){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("whochoose", i);
        startActivity(intent);
    }
}
