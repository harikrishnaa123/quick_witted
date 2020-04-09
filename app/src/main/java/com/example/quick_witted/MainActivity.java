package com.example.quick_witted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    EditText username, password;
    Button login;

    String email, pass;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.login_name);
        password = findViewById(R.id.login_password);
        login = findViewById(R.id.login_btn);
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
        String mailid, pass_word;
        mailid = sp.getString("mail", "null");
        pass_word = sp.getString("password", "null");
        if(!mailid.equals("null") && !mailid.isEmpty()){
            username.setText(mailid);
        }
        if(!pass_word.equals("null") && !pass_word.isEmpty()){
            password.setText(pass_word);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = username.getText().toString();
                pass = password.getText().toString();

                if(email.isEmpty() || pass.isEmpty()){
                    Toast.makeText(getApplicationContext(), "details not filled", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(getApplicationContext(), "Sign in complete", Toast.LENGTH_SHORT).show();
                            Intent mIntent = new Intent(getApplicationContext(), MapsActivity.class);
                            startActivity(mIntent);
                        }
                    });
                }
            }
        });
    }

    public void gotoSignup(View view){
        Intent mIntent = new Intent(getApplicationContext(), signup.class);
        startActivity(mIntent);
    }
}
