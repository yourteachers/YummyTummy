package com.example.yummytummy_v01;

import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AccountEdit extends Fragment implements DatePickerDialog.OnDateSetListener {
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
    ImageButton edit;

    Connection connection;
    //for choosing dates and validating
    Integer year;
    Integer day;
    Integer month;
    Button saveChanges;
    User user;
    private View root;
    Main2Activity mainact;


    private AccountEditViewModel mViewModel;

    public static AccountEdit newInstance() {
        return new AccountEdit();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.account_edit_fragment, container, false);
        passwordEditText=root.findViewById(R.id.passwordEditText);
        usernameEditText=root.findViewById(R.id.usernameEditText);
        emailEditText=root.findViewById(R.id.emailEditText);
        dateEditText=root.findViewById(R.id.dateEditText);
        confirmpasswordEditText=root.findViewById(R.id.confirmPasswordText);

        usernameInputLayout=root.findViewById(R.id.usernametextInputLayout);
        passwordInputLayout=root.findViewById(R.id.passwordtextInputLayout);
        emailInputLayout=root.findViewById(R.id.emailtextInputLayout);
        dateInputLayout=root.findViewById(R.id.datetextInputLayout);
        confirmpasswordInputLayout=root.findViewById(R.id.confirmPasswordtextInputLayout);
        saveChanges=root.findViewById(R.id.savechanges);
        edit=root.findViewById(R.id.edit);
        year=null;
        day=null;
        month=null;
        mainact=(Main2Activity)getActivity();
        user = mainact.user;
        init();
        connection = connectionClass.establish_Connection();
        usernameEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        dateEditText.setEnabled(false);
        confirmpasswordEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        saveChanges.setEnabled(false);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameEditText.isEnabled()){
                    usernameEditText.setEnabled(false);
                    emailEditText.setEnabled(false);
                    dateEditText.setEnabled(false);
                    confirmpasswordEditText.setEnabled(false);
                    passwordEditText.setEnabled(false);
                }
                else{
                    usernameEditText.setEnabled(true);
                    emailEditText.setEnabled(true);
                    dateEditText.setEnabled(true);
                    confirmpasswordEditText.setEnabled(true);
                    passwordEditText.setEnabled(true);
                    saveChanges.setEnabled(true);
                }
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                if (connection == null) {
                    Toast.makeText(getActivity(), "Connection Problem.", Toast.LENGTH_SHORT).show();

                    return;
                }

                if(!validateSignUpDetails(username,password,confirmPassword,email,birthday)){
                    return;
                }





            }
        });
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AccountEditViewModel.class);
        // TODO: Use the ViewModel
    }
    private void init(){
        if(user!=null){
            usernameEditText.setText(user.getUsername());
            passwordEditText.setText(user.getPassword());
            emailEditText.setText(user.getEmail());
            try{
                SimpleDateFormat mdyFormat = new SimpleDateFormat("MM/dd/yyyy");
                dateEditText.setText(mdyFormat.format(user.getBirthday()));
            }
            catch (Exception e){

            }


        }

    }

    //this method is called with the year/month/date that were selected when the datePickerDialog got closed
    @Override
    public void onDateSet(DatePicker View, int year, int month, int dayOfMonth){
        this.day = dayOfMonth;
        this.month = month+1;
        this.year=year;
        dateEditText.setText(dayOfMonth+"/"+(month+1)+"/"+year);
    }
    private void showDatePickerDialog(){
        DatePickerDialog dialog = new DatePickerDialog(getActivity(),this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
    private boolean validateSignUpDetails(String username,String password,String confirmpass,String email,String birthdate){
        if(username.length()==0 || username.length()<5 || username.length()>255){
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


        else if(year!=null) {
            if (year <= Calendar.getInstance().get(Calendar.YEAR) && Calendar.getInstance().get(Calendar.YEAR) - year < 18) {
                dateInputLayout.setError("MUST BE ATLEAST 18 YEARS OLD");
                usernameInputLayout.setError(null);
                passwordInputLayout.setError(null);
                confirmpasswordInputLayout.setError(null);
                emailInputLayout.setError(null);

                return false;
            } else if (year > Calendar.getInstance().get(Calendar.YEAR)) {
                dateInputLayout.setError("Please enter a valid birth date");
                usernameInputLayout.setError(null);
                passwordInputLayout.setError(null);
                confirmpasswordInputLayout.setError(null);
                emailInputLayout.setError(null);

                return false;
            }
        }

        else{
            usernameInputLayout.setError(null);
            passwordInputLayout.setError(null);
            confirmpasswordInputLayout.setError(null);
            emailInputLayout.setError(null);
            dateInputLayout.setError(null);
        }
        if(!username.equals(user.getUsername())){
            try{
                String user_query = "SELECT * FROM [dbo].[Users] where username='"+username+"'";
                Statement user_stat = connection.createStatement();
                ResultSet user_result = user_stat.executeQuery(user_query);

                if(user_result.next()) {
                    usernameInputLayout.setError("Username already exists.");
                    return false;

                }

            }
            catch(Exception e){
                Toast.makeText(getActivity(), "Connection Problem."+e.getMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(!email.equals(user.getEmail())){
            try{
                String email_query = "SELECT * FROM [dbo].[Users] where email='"+email+"'";
                Statement email_stat = connection.createStatement();
                ResultSet email_result = email_stat.executeQuery(email_query);

                if(email_result.next()){
                    emailInputLayout.setError("Email already in use");
                    return false;
                }

            }
            catch(Exception e){
                Toast.makeText(getActivity(), "Connection Problem."+e.getMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        //validated everything now time to update
        if(updateUserDetails(username,password,email,birthdate)){

            Toast.makeText(getActivity(), "Changes Saved Succesfuly", Toast.LENGTH_LONG).show();
            /*
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getActivity(),R.style.AlertDialog); //can change styles however i want

            dlgAlert.setMessage("Changes Saved successfully!");

            dlgAlert.setTitle("Success...");
            dlgAlert.setPositiveButton("Continue",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                   /* usernameEditText.setText("");
                    passwordEditText.setText("");
                    confirmpasswordEditText.setText("");
                    emailEditText.setText("");
                    dateEditText.setText("");
                }
            });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();*/


        }
        return true;
    }
    private boolean updateUserDetails(String username,String password,String email,String birthdate){
        if(connection==null){
            Toast.makeText(getActivity(), "Connection Problem.", Toast.LENGTH_SHORT).show();
            return false;
        }
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = formatter.parse(birthdate);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            String user_query = "UPDATE [dbo].[Users] SET username = '"+username+"',password = '"+password+"',email = '"+email+"',birthdate = '"+sqlDate+"' WHERE ID = '"+user.getUID()+"';";
            Statement user_stat = connection.createStatement();
            user_stat.executeUpdate(user_query);
            user.setBirthday(sqlDate);
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);

        }
        catch(Exception e){
            Log.e("error in updating: ",e.getMessage());
            Toast.makeText(getActivity(), "Problem in updating"+e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
         return true;
    }
    public boolean isEmailValid(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
