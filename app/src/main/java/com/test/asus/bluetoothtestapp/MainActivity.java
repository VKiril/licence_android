package com.test.asus.bluetoothtestapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity  implements OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.main);

        Button loginBtn = (Button) findViewById(R.id.login);
        loginBtn.setOnClickListener(this);

        Button signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

        Button fbLoginBtn = (Button)findViewById(R.id.fbLogin);
        fbLoginBtn.setOnClickListener(this);

        Button skipBtn = (Button) findViewById(R.id.skip);
        skipBtn.setOnClickListener(this);

        Button plot = (Button) findViewById(R.id.dinamic_plot);
        plot.setOnClickListener(this);

    }

    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.skip:
                intent = new Intent(MainActivity.this, SkipController.class);
                startActivity(intent);
                break;
            case R.id.fbLogin:
                intent = new Intent(MainActivity.this, FacebookLoginController.class);
                startActivity(intent);
                break;
            case R.id.signIn:
                intent = new Intent(MainActivity.this, RegisterController.class);
                startActivity(intent);

                break;
            case R.id.login:
                intent = new Intent(MainActivity.this, LoginController.class);
                startActivity(intent);
                break;
            case R.id.dinamic_plot:
                intent = new Intent(MainActivity.this, DinamycPlot.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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