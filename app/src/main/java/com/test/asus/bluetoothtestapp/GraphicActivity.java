package com.test.asus.bluetoothtestapp;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.test.asus.bluetoothtestapp.Helpers.DemoBase;

public class GraphicActivity extends DemoBase {
    EditText derivation ;
    String deriv ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);
        Button start = (Button)findViewById(R.id.btn_start);
        derivation = (EditText) findViewById(R.id.derivation);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deriv = derivation.getText().toString();
                //Intent intent = new Intent(GraphicActivity.this,  PlotGraphController_o.class);
                //intent.putExtra("derivation", deriv);
                //startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graphic, menu);
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
