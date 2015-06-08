package com.test.asus.bluetoothtestapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.test.asus.bluetoothtestapp.Entities.User;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements OnClickListener {
    Context context ;
    SharedPreferences sharedpreferences;
    String SPrefName = "test";

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
        Button test = (Button)findViewById(R.id.test_id);
        test.setOnClickListener(this);

        Button graphBtn = (Button) findViewById(R.id.btn_graph);
        graphBtn.setOnClickListener(this);

       /* String FILENAME = "hello_file";
        String string = "hello world! /n  /n";

        FileOperations file = new FileOperations();
        file.write(FILENAME, string);

        String buffer = file.read(FILENAME);
        Log.d("buffer", buffer);*/

        sharedpreferences = getSharedPreferences(SPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("key","test1212");
        editor.commit();

        String text = sharedpreferences.getString("key", "");
        //Log.d("buffer", text);






    }

    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.skip:
                intent = new Intent(MainActivity.this, GraphicActivity.class);
                startActivity(intent);
                break;
            case R.id.fbLogin:
                intent = new Intent(MainActivity.this, FacebookLogin.class);
                startActivity(intent);
                //List<Test> tests = Test.listAll(Test.class);
                break;
            case R.id.signIn:
                intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);

                break;
            case R.id.login:
                intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                List<User> test = User.listAll(User.class);
//                Iterator<User> i = test.iterator();
//                while(i.hasNext()){
//
//                }

//                for(Test item : test){
//                    item.delete();
//                }

                /*List<User> userList = User.listAll(User.class);
                Log.d("test", userList.toString());*/
                break;
            case R.id.test_id:
                intent = new Intent(MainActivity.this, BufferActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_graph:
                intent = new Intent(MainActivity.this, TestGraph.class);
                startActivity(intent);

        }
    }

    private void createFile(String Text){

        FileOutputStream fos=null;
        try {
            fos=openFileOutput("mynote.txt", MODE_PRIVATE);
            fos.write(Text.getBytes());
            Toast.makeText(getApplicationContext(), "File created succesfully", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.e("CreateFile", e.getLocalizedMessage());
        }
        catch (IOException e) {
            Log.e("CreateFile", e.getLocalizedMessage());
        }

        finally{
            if(fos!=null){
                try {
                    // drain the stream
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
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

