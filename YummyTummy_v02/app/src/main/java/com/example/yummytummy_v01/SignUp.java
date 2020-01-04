package com.example.yummytummy_v01;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignUp extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    TextInputEditText passwordEditText;
    TextInputEditText confirmpasswordEditText;
    TextInputEditText usernameEditText;
    TextInputEditText dateEditText;
    TextInputEditText emailEditText;
    TextInputLayout usernameInputLayout;
    TextInputLayout passwordInputLayout;
    TextInputLayout emailInputLayout;
    TextInputLayout dateInputLayout;
    TextInputLayout confirmpasswordInputLayout;

    Connection connection;
    //for choosing dates and validating
    Integer year;
    Integer day;
    Integer month;
    Button signUp_btn;
    TextView signInLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        passwordEditText=findViewById(R.id.passwordEditText);
        usernameEditText=findViewById(R.id.usernameEditText);
        emailEditText=findViewById(R.id.emailEditText);
        dateEditText=findViewById(R.id.dateEditText);
        confirmpasswordEditText=findViewById(R.id.confirmPasswordText);

        usernameInputLayout=findViewById(R.id.usernametextInputLayout);
        passwordInputLayout=findViewById(R.id.passwordtextInputLayout);
        emailInputLayout=findViewById(R.id.emailtextInputLayout);
        dateInputLayout=findViewById(R.id.datetextInputLayout);
        confirmpasswordInputLayout=findViewById(R.id.confirmPasswordtextInputLayout);

        signInLink=findViewById(R.id.link_signin);
        signUp_btn=findViewById(R.id.savechanges);
        year=null;
        day=null;
        month=null;
        connection = connectionClass.establish_Connection();

        signUp_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String username;
                String password;
                String confirmPassword;
                String email;
                String birthday;
                username=usernameEditText.getText().toString();
                password=passwordEditText.getText().toString();
                confirmPassword=confirmpasswordEditText.getText().toString();
                email=emailEditText.getText().toString();
                birthday=dateEditText.getText().toString();

                if(!validateSignUpDetails(username,password,confirmPassword,email,birthday)){
                    return;
                }



                if (connection == null) {
                    Toast.makeText(getApplicationContext(), "Connection Problem.", Toast.LENGTH_SHORT).show();

                    return;
                }
                if(addUser_toDatabase(username,password,email,birthday)){
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SignUp.this,R.style.AlertDialog); //can change styles however i want

                    dlgAlert.setMessage("Account was created successfully!");

                    dlgAlert.setTitle("Congratulations...");
                    dlgAlert.setPositiveButton("Sign in",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(),MainLogInActivity.class);
                                    // intent.putExtra(EXTRA_MESSAGE, message);
                                    startActivity(intent);
                                }
                            });
                    dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            usernameEditText.setText("");
                            passwordEditText.setText("");
                            confirmpasswordEditText.setText("");
                            emailEditText.setText("");
                            dateEditText.setText("");
                        }
                    });
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();


                }


            }
        });
        dateEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        signInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainLogInActivity.class);
                startActivity(intent);
            }
        });

    }
    private boolean validateSignUpDetails(String user,String password,String confirmpass,String email,String birthdate){
        if(user.length()==0 || user.length()<5 || user.length()>255){
            usernameInputLayout.setError("Please enter a username between 5-255 characters");
            passwordInputLayout.setError(null);
            confirmpasswordInputLayout.setError(null);
            emailInputLayout.setError(null);
            dateInputLayout.setError(null);
            return false;
        }
        else if(!isEmailValid(email)){
            emailInputLayout.setError("Please enter a valid email");
            usernameInputLayout.setError(null);
            passwordInputLayout.setError(null);
            confirmpasswordInputLayout.setError(null);
            dateInputLayout.setError(null);
            return false;
        }
        else if(password.length()==0 || password.length()<5 || password.length()>255){
            passwordInputLayout.setError("Please enter a password between 5-255 characters");
            usernameInputLayout.setError(null);
            confirmpasswordInputLayout.setError(null);
            emailInputLayout.setError(null);
            dateInputLayout.setError(null);
            return false;
        }
        else if(confirmpass.length()==0 ){
            confirmpasswordInputLayout.setError("Please confirm password");
            usernameInputLayout.setError(null);
            passwordInputLayout.setError(null);
            emailInputLayout.setError(null);
            dateInputLayout.setError(null);
            return false;
        }
        else if(!(confirmpass.equals(password))){
            confirmpasswordInputLayout.setError("passwords do not match");
            usernameInputLayout.setError(null);
            passwordInputLayout.setError(null);
            emailInputLayout.setError(null);
            dateInputLayout.setError(null);
            return false;
        }


        else if(year==null){
            dateInputLayout.setError("Please choose a birth date");
            usernameInputLayout.setError(null);
            passwordInputLayout.setError(null);
            confirmpasswordInputLayout.setError(null);
            emailInputLayout.setError(null);

            return false;
        }
        else if(year <= Calendar.getInstance().get(Calendar.YEAR) && Calendar.getInstance().get(Calendar.YEAR)-year<18){
            dateInputLayout.setError("MUST BE ATLEAST 18 YEARS OLD");
            usernameInputLayout.setError(null);
            passwordInputLayout.setError(null);
            confirmpasswordInputLayout.setError(null);
            emailInputLayout.setError(null);

            return false;
        }
        else if (year > Calendar.getInstance().get(Calendar.YEAR)){
            dateInputLayout.setError("Please enter a valid birth date");
            usernameInputLayout.setError(null);
            passwordInputLayout.setError(null);
            confirmpasswordInputLayout.setError(null);
            emailInputLayout.setError(null);

            return false;
        }

        else{
            usernameInputLayout.setError(null);
            passwordInputLayout.setError(null);
            confirmpasswordInputLayout.setError(null);
            emailInputLayout.setError(null);
            dateInputLayout.setError(null);
        }
        try{
            String user_query = "SELECT * FROM [dbo].[Users] where username='"+user+"'";
            Statement user_stat = connection.createStatement();
            ResultSet user_result = user_stat.executeQuery(user_query);

            String email_query = "SELECT * FROM [dbo].[Users] where email='"+email+"'";
            Statement email_stat = connection.createStatement();
            ResultSet email_result = email_stat.executeQuery(email_query);


            if(user_result.next()) {
                usernameInputLayout.setError("Username already exists.");
                return false;

            }
            else if(email_result.next()){
                emailInputLayout.setError("Email already in use");
                return false;
            }

        }
        catch(Exception e){
            Toast.makeText(getApplicationContext(), "Connection Problem."+e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    public boolean addUser_toDatabase(String username,String password,String email,String birthdate){
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = formatter.parse(birthdate);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            String user_query = "INSERT INTO [dbo].[Users] (username, password, email, birthdate) " +
                    "VALUES ('"+username+"', '"+password+"', '"+email+"', '"+ sqlDate+"');";
            Statement user_stat = connection.createStatement();
            user_stat.executeUpdate(user_query);

        }
        catch(Exception e){
            Log.e("error in inserting: ",e.getMessage());
            Toast.makeText(getApplicationContext(), "Connection Problem in inserting"+e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }
    //opens up a date picker dialog in order to choose a date
    private void showDatePickerDialog(){
        DatePickerDialog dialog = new DatePickerDialog(this,this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dialog.show();
     }
     //this method is called with the year/month/date that were selected when the datePickerDialog got closed
    @Override
    public void onDateSet(DatePicker View,int year,int month,int dayOfMonth){
        this.day = dayOfMonth;
        this.month = month+1;
        this.year=year;
        dateEditText.setText(dayOfMonth+"/"+(month+1)+"/"+year);
    }
    /*returns true if the email provided is a valid email pattern*/
    public boolean isEmailValid(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
