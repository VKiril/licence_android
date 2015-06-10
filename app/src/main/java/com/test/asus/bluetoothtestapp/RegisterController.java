package com.test.asus.bluetoothtestapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.test.asus.bluetoothtestapp.Entities.User;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class RegisterController extends Activity implements OnClickListener {

    public EditText  et_name;
    public EditText  et_surname;
    public EditText  et_nickName;
    public EditText  et_body_mass ;
    public EditText  et_height ;
    public EditText  et_password ;
    public EditText  et_confirm_password ;
    public EditText  et_email ;
    public Button    btn_register ;
    public TextView  tvDate;
    public RadioGroup radioGroup;

    int DIALOG_DATE = 1;
    public static int myYear ;
    public static int thisYear ;
    public static int myMonth ;
    public static int myDay ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_name = (EditText)findViewById(R.id.et_first_name)      ;
        et_surname          = (EditText)findViewById(R.id.et_last_name)       ;
        et_nickName         = (EditText)findViewById(R.id.et_user_name)       ;
        et_body_mass        = (EditText)findViewById(R.id.et_body_mass)       ;
        et_height           = (EditText)findViewById(R.id.et_height)          ;
        et_password         = (EditText)findViewById(R.id.et_password)        ;
        et_confirm_password = (EditText)findViewById(R.id.et_confirm_password);
        et_email            = (EditText)findViewById(R.id.et_email)           ;
        tvDate              = (TextView)findViewById(R.id.tvDate)             ;
        btn_register        = (Button)  findViewById(R.id.btn_register)       ;
        radioGroup          = (RadioGroup) findViewById(R.id.gender_radio_group);



        btn_register.setOnClickListener(this);

        initDate();
    }

    @Override
    public void onClick(View v) {
        //Map array = new HashMap<>();
        switch (v.getId()){
            case R.id.btn_register:
                StringBuffer strBuff  = new StringBuffer();
                String name = et_name.getText().toString().replace("\n", "");
                String surname = et_surname.getText().toString().replace("\n","");
                String nickNmae = et_nickName.getText().toString().replace("\n","");


                String bodyMass = et_body_mass.getText().toString().replace("\n","");
                if(!isDigit(bodyMass)){
                    Toast.makeText(getApplicationContext(), "Body mass must be digits not letters", Toast.LENGTH_LONG).show();
                    return;
                }

                String height = et_height.getText().toString().replace("\n","");
                if(!isDigit(height)){
                    Toast.makeText(getApplicationContext(), "Height must be digits not letters", Toast.LENGTH_LONG).show();
                    return;
                }

                String email = et_email.getText().toString().replace("\n","");
                if(!isValidEmail(email)){
                    Toast.makeText(RegisterController.this, "invalid email", Toast.LENGTH_SHORT).show();
                    return;
                }
                strBuff.append(myDay);
                strBuff.append("/");
                strBuff.append(myMonth);
                strBuff.append("/");
                strBuff.append(myYear);
                //Log.d("test", strBuff.toString());
                String date = strBuff.toString();


                String passwordBuff = et_password.getText().toString().replace("\n","");
                MessageDigest digest;
                String password ="";
                if(passwordBuff != null && passwordBuff.length()>=4){
                    try
                    {
                        digest = MessageDigest.getInstance("MD5");
                        digest.update(passwordBuff.getBytes(), 0, passwordBuff.length());
                        password = new BigInteger(1, digest.digest()).toString(16);
                    }
                    catch (NoSuchAlgorithmException e)
                    {
                        e.printStackTrace();
                    }
                } else{
                    Toast.makeText(RegisterController.this, "password must have four or more characters", Toast.LENGTH_SHORT).show();
                }

                final String gender = ((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId() )).getText().toString();




                User user = new User(RegisterController.this);

                List<User> userExist = user.find(User.class, "name = ?  and surname = ?", name, surname);




                user.setName(name);
                user.setSurname(surname);
                user.setNickName(nickNmae);
                user.setBirthDay(date);
                user.setEmail(email);
                user.setHeight(height);
                user.setBody_mass(bodyMass);
                user.setPassword(password);
                user.setGender(gender);
                user.save();






                /*User test = new User(this);
                test.setName("test1122112121");
                test.save();*/
                Toast.makeText(getApplicationContext(), "Inserted into database",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(getApplicationContext(), "Inserted into database",Toast.LENGTH_SHORT).show();
                    }
                },2000);
                break;


        }
    }

    public void onclick(View view) {
        showDialog(DIALOG_DATE);
    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            if(year >= thisYear){
                Toast.makeText(RegisterController.this, "wrong year, you enter current year, this is impossible "
                        , Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("test", year + "   :   " + monthOfYear + "    :    " +dayOfMonth );

            myYear = year;
            myMonth = monthOfYear;
            myMonth ++ ;
            myDay = dayOfMonth;
            tvDate.setText( myDay + " / " + myMonth + " / " + myYear);
        }
    };

    public static void initDate(){
        Calendar cal= Calendar.getInstance();

        SimpleDateFormat day = new SimpleDateFormat("dd");
        String day_number = day.format(cal.getTime());
        try {
            myDay = Integer.parseInt(day_number);
        } catch(NumberFormatException nfe) {
            Log.d("error", nfe.getMessage());
        }

        SimpleDateFormat month_date1 = new SimpleDateFormat("MM");
        String month_name1 = month_date1.format(cal.getTime());
        try {
            myMonth = Integer.parseInt(month_name1);
            myMonth++;
        } catch(NumberFormatException nfe) {
            Log.d("error", nfe.getMessage());
        }

        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        String year_number = year.format(cal.getTime());
        try {
            myYear = Integer.parseInt(year_number);
            thisYear = myYear ;
        } catch(NumberFormatException nfe) {
            Log.d("error", nfe.getMessage());
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    protected boolean isDigit(String string){
        boolean isDigit = true;
        if(string != null && !string.isEmpty()){
            for(char c : string.toCharArray()){
                if(!(Character.isDigit(c))){
                    isDigit = false;
                }
            }
        }
        return  isDigit;
    }

}
