package com.example.all100.login;

import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.all100.Home;
import com.example.all100.R;
import com.example.all100.WebViewDemo;
import com.google.android.material.textfield.TextInputLayout;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Originwho extends AppCompatActivity {
    private TextView who1, who2, kimiinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.so_originwho);

        who1 = findViewById(R.id.who1);
        who2 = findViewById(R.id.who2);
        kimiinfo = findViewById(R.id.kimiinfo);

        who1.setOnClickListener(onClickListener);
        who2.setOnClickListener(onClickListener);
        kimiinfo.setOnClickListener(onClickListener);


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.who1:
                    myStartActivity(Login_main.class, 0);
                    break;
                case R.id.who2:
                    myStartActivity(Login_main.class, 1);
                    break;
                case R.id.kimiinfo:
                    myStartActivity(WebViewDemo.class, 2);
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
