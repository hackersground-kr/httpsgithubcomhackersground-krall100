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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://all100.azurewebsites.net"; // Azure 웹 앱의 URL로 변경해주세요
    private static final String SAVE_DATA_ENDPOINT = "/saveData"; // 저장 엔드포인트 경로로 변경해주세요

    private EditText etSql;
    private TextView tvSql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dotest);

        etSql = findViewById(R.id.sql_et);
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