package com.example.all100.login;

import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.all100.Home;
import com.example.all100.R;
import com.google.android.material.textfield.TextInputLayout;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
public class Login_main extends AppCompatActivity {

    private TextInputLayout idtextfield, pdtextfield;
    private String id_text,pd_text;
    private TextView gologin, forgetID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.so_login_main);

        idtextfield = findViewById(R.id.idtextfield);
        pdtextfield = findViewById(R.id.pdtextfield);

        id_text = idtextfield.getEditText().getText().toString();
        pd_text = pdtextfield.getEditText().getText().toString();

        gologin = findViewById(R.id.gologin);
        forgetID = findViewById(R.id.forgetID);
        gologin.setOnClickListener(onClickListener);
        forgetID.setOnClickListener(onClickListener);

        AppCenter.start(getApplication(), "0d9285f4-ef83-425a-a3c4-0b86a14a0b88",
                Analytics.class, Crashes.class);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.gologin:
                    login();
                    break;
                case R.id.forgetID:
                    myStartActivity(FInd_id.class);
                    break;
            }
        }
    };

    private void login(){
        idtextfield = findViewById(R.id.idtextfield);
        pdtextfield = findViewById(R.id.pdtextfield);

        id_text = idtextfield.getEditText().getText().toString();
        pd_text = pdtextfield.getEditText().getText().toString();

        if(id_text.length()>0 && pd_text.length()>0){
            //여기서 db와 비교 전체 돌아가면서 get해야 함

            //만약 success했다면
            //myStartActivity(Home.class);

            //만약 실패했다면
            //startToast("아이디와 비밀번호를 다시 확인해주세요.");

        }else{
            startToast("아이디 혹은 비밀번호를 입력해주세요!");
        }
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
