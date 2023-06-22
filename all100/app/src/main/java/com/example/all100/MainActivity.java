package com.example.all100;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class MainActivity extends AppCompatActivity {

    EditText etSql;

    //private static String ip = "211.36.136.72";
    //private static String port = "1433";
    private static String Classes = "net.sourceforge.jtds.jdbc.Driver";
    private static String database = "sqldb-hg1001001-python-api";
    private static String username = "all100DG";
    private static String password = "hellohackerground!1";
    private static String url = "jdbc:sqlserver://sqlsvr-hg1001001-python-api.database.windows.net:1433;database=sqldb-hg1001001-python-api;user=all100DG@sqlsvr-hg1001001-python-api;password=hellohackerground!1;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

    private Connection connection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dotest);

        etSql = findViewById(R.id.sql_et);

        // 인터넷 퍼미션을 요구하도록 함
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        // 네트워크 작업을 수행하기 위해 새로운 스레드를 생성
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName(Classes);
                    Log.d("minseok","hello111");
                    connection = DriverManager.getConnection(url, username, password);
                    Log.d("minseok","hello11w");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etSql.setText("SUCCESS");
                        }
                    });
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etSql.setText("ERROR");
                        }
                    });
                }
            }
        });

        networkThread.start();
    }

    public void sqlButton(View view) {
        if (connection != null) {
            Log.d("minseok","hello");
            Thread queryThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Statement statement = null;
                    try {
                        statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery("SELECT userName FROM userTbl WHERE userID = 'kim'");

                        while (resultSet.next()) {
                            final String userName = resultSet.getString(1);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    etSql.setText(userName);
                                }
                            });
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });

            queryThread.start();
        } else {
            etSql.setText("Connection is null");
        }
    }
}
