package id.ac.umn.studentchecker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    TextInputLayout un,pw;
    EditText eUsername,ePassword;
    Button btnLogin;
    boolean loggedin=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // databaseHelper = new DatabaseHelper(LoginActivity.this);

        SharedPreferences sh = getSharedPreferences("MySharedPref",0);
        boolean checking = sh.getBoolean("loggedin",loggedin);

        if(checking){
            Intent i2 = new Intent(LoginActivity.this,MainActivity2.class);
            startActivity(i2);
            finish();
        }
        un = findViewById(R.id.username);
        pw = findViewById(R.id.password);

        eUsername = un.getEditText();
        ePassword = pw.getEditText();

        btnLogin = findViewById(R.id.loginBtn);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean isExist = databaseHelper.checkUserExist(eUsername.getText().toString(), ePassword.getText().toString());
//
//                if(isExist){
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    intent.putExtra("username", eUsername.getText().toString());
//                    startActivity(intent);
//                } else {
//                    ePassword.setText(null);
//                    Toast.makeText(LoginActivity.this, "Login failed. Invalid username or password.", Toast.LENGTH_SHORT).show();
//                }

                if(eUsername.getText().toString().equals("user") && ePassword.getText().toString().equals("useruser")){
                    loggedin = true;
                    Intent i = new Intent(LoginActivity.this,MainActivity2.class);
                    startActivity(i);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, "Username or Password is incorrect :(", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        SharedPreferences sharedPreferences
                = getSharedPreferences("MySharedPref",
                MODE_PRIVATE);
        SharedPreferences.Editor myEdit
                = sharedPreferences.edit();
        myEdit.putBoolean("loggedin",
                loggedin);
        myEdit.commit();
    }
}
