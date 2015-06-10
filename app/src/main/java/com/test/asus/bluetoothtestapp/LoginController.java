package com.test.asus.bluetoothtestapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.test.asus.bluetoothtestapp.Entities.User;

import java.util.List;


public class LoginController extends Activity implements OnClickListener {
     EditText login_username;
     EditText login_password;
     Button loginBtn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_password = (EditText)findViewById(R.id.et_password);
        login_username = (EditText)findViewById(R.id.et_login);
        loginBtn = (Button) findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(this);

        SharedPreferences set = getSharedPreferences("test", Context.MODE_PRIVATE);
        String text = set.getString("key", "");
        Log.d("buffer", text);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                Context context ;
                SharedPreferences sharedpreferences;
                String SPrefName = "test";

                sharedpreferences = getSharedPreferences(SPrefName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                //editor.putString("user","username");

                User user = new User(getApplicationContext());
                String pass = login_password.getText().toString();
                String userName = login_username.getText().toString();
                List<User> result = user.find(User.class, "nickName = ?  and surname = ?", userName, pass);
                if(!result.isEmpty()){
                    for(User u : result ){
                        editor.putString("userId",u.getId().toString());
                        Intent intent = new Intent(LoginController.this, GraphicActivity.class);
                        startActivity(intent);
                    }
                    editor.commit();
                } else {
                    Toast.makeText(getApplicationContext(), "wrong login or password", Toast.LENGTH_SHORT).show();
                    return;
                }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
