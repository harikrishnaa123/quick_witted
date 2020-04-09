package com.example.quick_witted;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

public class signup extends AppCompatActivity {

    EditText email, password, cellno, licenceno, ambno, confirm;
    Button signup;

    String mail, pass, cell_no, lice_no, amb_no, confirm_password;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.signup_username);
        password = findViewById(R.id.signup_password);
        cellno = findViewById(R.id.cellno);
        licenceno = findViewById(R.id.licence_no);
        ambno = findViewById(R.id.amb_no);
        signup = findViewById(R.id.signup);
        confirm = findViewById(R.id.confirm_pass);

        mAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mail = email.getText().toString();
                pass = password.getText().toString();
                cell_no = cellno.getText().toString();
                lice_no = licenceno.getText().toString();
                amb_no = ambno.getText().toString();
                confirm_password = confirm.getText().toString();

                if(mail.isEmpty() || pass.isEmpty() || cell_no.isEmpty() || lice_no.isEmpty() || amb_no.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please fill all details", Toast.LENGTH_SHORT).show();
                }else if(pass.length() < 8){
                    Toast.makeText(getApplicationContext(), "Password length is too short", Toast.LENGTH_SHORT).show();
                }else if(cell_no.length() < 10 || cell_no.length() > 10){
                    Toast.makeText(getApplicationContext(), "Inavlid mobile number", Toast.LENGTH_SHORT).show();
                }
                else if(!mail.contains("@")){
                    Toast.makeText(getApplicationContext(), "invalid email", Toast.LENGTH_SHORT).show();
                }else if(!pass.equals(confirm_password)){
                    Toast.makeText(getApplicationContext(), "password and confirm password not identical", Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(getApplicationContext(), "registration successful", Toast.LENGTH_SHORT).show();

                            SharedPreferences sp =getSharedPreferences("user_details", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();

                            editor.putString("mail", mail);
                            editor.putString("password", pass);
                            editor.putString("cell_no", cell_no);
                            editor.putString("license no", lice_no);
                            editor.apply();

                            Log.d("email", mail);
                            Log.d("pass", pass);
                            Log.d("cell_no", cell_no);
                            Log.d("license", lice_no);

                            Intent mIntent = new Intent(getApplicationContext(), MapsActivity.class);
                            startActivity(mIntent);

                            if(!task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
