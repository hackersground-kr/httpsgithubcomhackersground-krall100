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


public class FInd_id extends AppCompatActivity {

    private TextInputLayout namefield, birthfield;
    private String name_text,birth_text, findid;
    private TextView nextgo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.so_find_id);

        namefield = findViewById(R.id.namefield);
        birthfield = findViewById(R.id.birthfield);

        name_text = namefield.getEditText().getText().toString();
        birth_text = birthfield.getEditText().getText().toString();

        nextgo = findViewById(R.id.nextgo);
        nextgo.setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.nextgo:
                    whatisid();
                    break;

            }
        }
    };

    private void whatisid(){
        namefield = findViewById(R.id.namefield);
        birthfield = findViewById(R.id.birthfield);

        name_text = namefield.getEditText().getText().toString();
        birth_text = birthfield.getEditText().getText().toString();

        if(name_text.length()>0 && birth_text.length()>0){
            //여기서 db와 비교 전체 돌아가면서 get해야 함

            //만약 success했다면
            //findid = 찾은 id집어넣기
            myStartActivity(This_id.class, findid);

            //만약 실패했다면
            //startToast("이름과 생년월일을 다시 확인해주세요.");

        }else{
            startToast("이름 혹은 생년월일을 입력해주세요!");
        }
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void myStartActivity(Class c, String s){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("user_id", s);
        startActivity(intent);
    }
}
