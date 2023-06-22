package com.example.all100.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
public class Login_main extends AppCompatActivity {

    private TextInputLayout idtextfield, pdtextfield;
    private String id_text,pd_text;
    private TextView gologin, forgetID;
    private FirebaseAuth mAuth;
    private ConstraintLayout login_main_2;

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
        login_main_2 = findViewById(R.id.login_main_2);

        Intent intent = getIntent();
        int whochoose = intent.getExtras().getInt("whochoose");

        AppCenter.start(getApplication(), "0d9285f4-ef83-425a-a3c4-0b86a14a0b88",
                Analytics.class, Crashes.class);

        mAuth = FirebaseAuth.getInstance();
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
                case R.id.login_main_2:
                    myStartActivity(So_who_signup.class);
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
            Intent intent = getIntent();
            int whochoose = intent.getExtras().getInt("whochoose");
            //여기서 db와 비교 전체 돌아가면서 get해야 함

            //만약 success했다면
            //myStartActivity(Home.class);

            //만약 실패했다면
            //startToast("아이디와 비밀번호를 다시 확인해주세요.");
            mAuth.signInWithEmailAndPassword(id_text, pd_text)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                if(whochoose==0){//guard!!!
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference document = db.collection("guard")
                                            .document(user.getUid());
                                    document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        //null
                                                        myStartActivity(Home.class);
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    startToast("아이디와 비밀번호를 다시 확인해주세요.");
                                                }
                                            });
                                }else if(whochoose==1){//sick!!!
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference document = db.collection("sick")
                                            .document(user.getUid());
                                    document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        //null
                                                        myStartActivity(Home.class);
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    startToast("아이디와 비밀번호를 다시 확인해주세요.");
                                                }
                                            });
                                }

                            } else {
                                    if (task.getException() != null) {
                                        startToast(task.getException().toString());
                                    }
                                }
                                }
                    });

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
