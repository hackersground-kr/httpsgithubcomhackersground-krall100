package com.example.all100;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    private static final String API_URL = "https://all100.azurewebsites.net"; // Azure 웹 앱의 URL로 변경해주세요
    private static final String SAVE_DATA_ENDPOINT = "/saveData"; // 저장 엔드포인트 경로로 변경해주세요

    private EditText etSql;
    private TextView tvSql;

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
        tvSql = findViewById(R.id.sql_text);

        Button saveButton = findViewById(R.id.sql_btn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etSql.getText().toString();

                // AsyncTask를 통해 서버로 데이터 저장 요청을 보냅니다.
                SaveDataAsyncTask saveTask = new SaveDataAsyncTask();
                saveTask.execute(name);
            }
        });

        Button loadButton = findViewById(R.id.btn_load);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AsyncTask를 통해 서버로부터 데이터 불러오기 요청을 보냅니다.
                LoadDataAsyncTask loadTask = new LoadDataAsyncTask();
                loadTask.execute();
            }
        });
    }

    private class SaveDataAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String result = "";

            try {
                // 서버로 데이터를 전송하기 위한 JSON 객체 생성
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("name", name);

                // 서버에 POST 요청을 보냄
                URL url = new URL(API_URL + SAVE_DATA_ENDPOINT + "?table=test");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(jsonParam.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                // 응답 코드 확인
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 요청이 성공적으로 처리됨
                    result = "Data saved successfully";
                } else {
                    // 요청이 실패함
                    result = "Error saving data: " + responseCode;
                }

                conn.disconnect();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
                result = "Error saving data: " + e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            try {
                // 서버로부터 데이터를 불러오기 위한 GET 요청 전송
                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");

                // 응답 읽기
                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                inputStream.close();

                // JSON 응답 파싱
                JSONObject jsonResponse = new JSONObject(response.toString());
                String name = jsonResponse.getString("name");

                // 결과값 설정
                result = "Name: " + name;

                conn.disconnect();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
                result = "Error loading data: " + e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            tvSql.setText(result);
        }
    }
}
