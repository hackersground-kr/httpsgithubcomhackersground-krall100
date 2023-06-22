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


public class Sick_reg extends AppCompatActivity {
    private TextView who1, who2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.so_originwho);

        who1 = findViewById(R.id.who1);
        who2 = findViewById(R.id.who2);

        who1.setOnClickListener(onClickListener);
        who2.setOnClickListener(onClickListener);

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
