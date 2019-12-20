package com.example.yummytummy_v01;


import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainLogInActivity extends AppCompatActivity {
    Button signIn;
    TextInputEditText usernameEditText;
    TextInputEditText passwordEditText;
    TextInputLayout usernameInputLayout;
    TextInputLayout passwordInputLayout;
    TextView signUpLink;
    Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_log_in);
        signIn=findViewById(R.id.login);
        usernameEditText=findViewById(R.id.usernameEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        usernameInputLayout=findViewById(R.id.usernametextInputLayout);
        passwordInputLayout=findViewById(R.id.passwordtextInputLayout);
        signUpLink=findViewById(R.id.link_signup);
        signIn.setEnabled(true);

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    connection = connectionClass.establish_Connection();
                    if (connection == null) {
                        Toast.makeText(getApplicationContext(), "Connection Problem.", Toast.LENGTH_SHORT).show();

                        return;
                    } else {
                        String username;
                        String password;
                        username = usernameEditText.getText().toString();
                        password = passwordEditText.getText().toString();
                        if (username.length() == 0  ) {
                            usernameInputLayout.setError("Please enter a username");
                            passwordInputLayout.setError(null);

                        }
                        else if(password.length() == 0){
                            passwordInputLayout.setError("Please enter a password");
                            usernameInputLayout.setError(null);
                        }
                        else{
                            usernameInputLayout.setError(null);
                            passwordInputLayout.setError(null);
                            CheckLogin login = new CheckLogin();
                            login.execute(username,password);
                        }
                    }

                }
                catch(Exception e){

                }

            }
        });

    }
        @Override
       protected void onDestroy(){
            super.onDestroy();
            try{
                connection.close();

            }
            catch(Exception e){

                Log.e("sql error",e.getMessage());
            }

       }

    public class CheckLogin extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess=false;
        String name1="";
        final ProgressDialog progressDialog = new ProgressDialog(MainLogInActivity.this,
                R.style.Dialog);



        protected void onPreExecute(){
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();

        }

        public Boolean getSuccess() {
            return isSuccess;
        }
        @Override
        public void onPostExecute(String r){

            final Handler handler = new Handler();


            if(isSuccess){

                Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
                intent.putExtra("USERNAME", name1);

                startActivity(intent);
            }


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms

                    progressDialog.dismiss();
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainLogInActivity.this,R.style.AlertDialog); //can change styles however i want

                    dlgAlert.setMessage("wrong password or username");

                    dlgAlert.setTitle("Bad Login Credentials...");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();

                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                }
            }, 2500);

        }
        @Override
        public String doInBackground(String... params){
            try{


                    String query = "SELECT * FROM [dbo].[Users] where username='"+params[0]+"' and password='"+params[1]+"'";
                    Statement stat = connection.createStatement();
                    ResultSet rs = stat.executeQuery(query);

                    if(rs.next()) {

                        name1 = rs.getString("username");
                        z = "Query succesful";
                        isSuccess = true;


                    }
                    else{ // the query doesn't retrieve anything because there is no such username/password
                        z = "wrong username/password.";
                        isSuccess = false;
                    }
                }

            catch(Exception e){
                z = e.getMessage();
                isSuccess=false;
                Log.e("sql error",z);
            }
            return z;
        }
    }

}
