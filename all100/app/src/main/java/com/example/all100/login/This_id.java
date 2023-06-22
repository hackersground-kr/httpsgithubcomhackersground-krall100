package com.example.all100.login;

import androidx.appcompat.app.AppCompatActivity;
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


public class This_id extends AppCompatActivity {
    private TextView yourid2;
    private ImageView back_btn_thisisid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.so_this_id);

        back_btn_thisisid = findViewById(R.id.back_btn_thisisid);
        yourid2 = findViewById(R.id.yourid2);

        back_btn_thisisid.setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.back_btn_thisisid:
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
