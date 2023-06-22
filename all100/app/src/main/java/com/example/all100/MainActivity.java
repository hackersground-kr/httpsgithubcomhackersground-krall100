package com.example.all100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(signature.toByteArray());
                String base64Hash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("AppLog", "Base64 SHA1: " + base64Hash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}