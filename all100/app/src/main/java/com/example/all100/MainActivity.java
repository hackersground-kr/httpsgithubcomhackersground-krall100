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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MainActivity extends AppCompatActivity {

    private static final String DB_URL = "jdbc:mysql://your-database-url:3306/your-database-name";
    private static final String DB_USER = "your-username";
    private static final String DB_PASSWORD = "your-password";

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

                // AsyncTask를 통해 데이터베이스에 값을 저장하는 작업을 실행합니다.
                SaveDataAsyncTask saveTask = new SaveDataAsyncTask();
                saveTask.execute(name);
            }
        });

        Button loadButton = findViewById(R.id.btn_load);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AsyncTask를 통해 데이터베이스에서 값을 불러오는 작업을 실행합니다.
                LoadDataAsyncTask loadTask = new LoadDataAsyncTask();
                loadTask.execute();
            }
        });
    }

    private class SaveDataAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String name = params[0];
            boolean success = false;

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = connection.prepareStatement("INSERT INTO your-table-name (name) VALUES (?)");
                statement.setString(1, name);
                int rowsAffected = statement.executeUpdate();

                success = (rowsAffected > 0);

                statement.close();
                connection.close();
            } catch (SQLException e) {
                Log.e("SaveDataAsyncTask", "Error executing SQL query: " + e.getMessage());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                Log.e("SaveDataAsyncTask", "Error loading JDBC driver: " + e.getMessage());
                e.printStackTrace();
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(MainActivity.this, "Value saved to database", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Failed to save value to database", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String value = "";

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = connection.prepareStatement("SELECT name FROM your-table-name");
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    value = resultSet.getString("name");
                }

                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                Log.e("LoadDataAsyncTask", "Error executing SQL query: " + e.getMessage());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                Log.e("LoadDataAsyncTask", "Error loading JDBC driver: " + e.getMessage());
                e.printStackTrace();
            }

            return value;
        }

        @Override
        protected void onPostExecute(String value) {
            tvSql.setText(value);

        }
    }
}
