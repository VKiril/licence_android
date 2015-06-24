package com.test.asus.bluetoothtestapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.util.PlotStatistics;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;


public class BluetoothController extends Activity implements SensorEventListener {

    public static final int ENABLE_BT__REQUEST = 1;
    private SmoothBluetooth mSmoothBluetooth;
    private Button mScanButton;
    private TextView mStateTv;
    private TextView mDeviceTv;
    private Button mPairedButton;
    private Button mDisconnectButton;
    private LinearLayout mConnectionLayout;
    private EditText mMessageInput;
    private Button mSendButton;
    private CheckBox mCRLFBox;
    private List<Integer> mBuffer = new ArrayList<>();
    private List<String> mResponseBuffer = new ArrayList<>();
    private ArrayAdapter<String> mResponsesAdapter;
    private String path = "/sdcard/com.licence/";
    public double incomingByte = 0 ;
    BufferedWriter bw;
    protected static  boolean isFirstIncomnigByte = true ;

    private static final int HISTORY_SIZE = 30;            // number of points to plot in history
    private SensorManager sensorMgr = null;
    private Sensor orSensor = null;

    private XYPlot aprLevelsPlot = null;
    private XYPlot aprHistoryPlot = null;

    //plotter fields

    private CheckBox hwAcceleratedCb;
    private CheckBox showFpsCb;
    private SimpleXYSeries aprLevelsSeries = null;
    private SimpleXYSeries azimuthHistorySeries = null;
    private SimpleXYSeries pitchHistorySeries = null;
    private SimpleXYSeries rollHistorySeries = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_buffer);


        Intent intent = getIntent();
        String derivation = intent.getStringExtra("derivation");
        mSmoothBluetooth = new SmoothBluetooth(this);
        mSmoothBluetooth.setListener(mListener);

        ListView responseListView = (ListView) findViewById(R.id.responses);
        mResponsesAdapter = new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, mResponseBuffer);
        responseListView.setAdapter(mResponsesAdapter);

        mCRLFBox = (CheckBox) findViewById(R.id.carrage);
        mMessageInput = (EditText) findViewById(R.id.message);

        mSendButton = (Button) findViewById(R.id.send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmoothBluetooth.send(mMessageInput.getText().toString(), mCRLFBox.isChecked());
                mMessageInput.setText("");
            }
        });


        mDisconnectButton = (Button) findViewById(R.id.disconnect);
        mDisconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmoothBluetooth.disconnect();
                mResponseBuffer.clear();
                mResponsesAdapter.notifyDataSetChanged();
            }
        });

        mConnectionLayout = (LinearLayout) findViewById(R.id.connection);

        mScanButton = (Button) findViewById(R.id.scan);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmoothBluetooth.doDiscovery();
            }
        });

        mPairedButton = (Button) findViewById(R.id.paired);
        mPairedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmoothBluetooth.tryConnection();
            }
        });
        mStateTv = (TextView) findViewById(R.id.state);
        mStateTv.setText("Disconnected");
        mDeviceTv = (TextView) findViewById(R.id.device);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateTime = sdf.format(new Date());
        String fpath = path + "record_" + dateTime + ".txt";
        File file = new File(fpath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(file.getAbsoluteFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        bw = new BufferedWriter(fw);

        TextView show_derivation = (TextView)findViewById(R.id.show_derivation);
        show_derivation.setText("Derivation " + derivation);

        plot();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSmoothBluetooth.stop();
        try {
            bw.close();
            System.out.println("onDestroy()");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ENABLE_BT__REQUEST) {
            if(resultCode == RESULT_OK) {
                mSmoothBluetooth.tryConnection();
            }
        }
    }

    private SmoothBluetooth.Listener mListener = new SmoothBluetooth.Listener() {
        @Override
        public void onBluetoothNotSupported() {
            Toast.makeText(BluetoothController.this, "Bluetooth not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onBluetoothNotEnabled() {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, ENABLE_BT__REQUEST);
        }

        @Override
        public void onConnecting(Device device) {
            mStateTv.setText("Connecting to");
            mDeviceTv.setText(device.getName());
        }

        @Override
        public void onConnected(Device device) {
            mStateTv.setText("Connected to");
            mDeviceTv.setText(device.getName());
            mConnectionLayout.setVisibility(View.GONE);
            mDisconnectButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onDisconnected() {
            mStateTv.setText("Disconnected");
            mDeviceTv.setText("");
            mDisconnectButton.setVisibility(View.GONE);
            mConnectionLayout.setVisibility(View.VISIBLE);
        }


        @Override
        public void onConnectionFailed(Device device) {
            mStateTv.setText("Disconnected");
            mDeviceTv.setText("");
            Toast.makeText(BluetoothController.this, "Failed to connect to " + device.getName(), Toast.LENGTH_SHORT).show();
            if (device.isPaired()) {
                mSmoothBluetooth.doDiscovery();
            }
        }

        @Override
        public void onDiscoveryStarted() {
            Toast.makeText(BluetoothController.this, "Searching", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDiscoveryFinished() {

        }

        @Override
        public void onNoDevicesFound() {
            Toast.makeText(BluetoothController.this, "No device found", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDevicesFound(final List<Device> deviceList,
                                   final SmoothBluetooth.ConnectionCallback connectionCallback) {


            DevicesAdapter adapter = new DevicesAdapter(BluetoothController.this, deviceList);


            ListView listView = (ListView)findViewById(R.id.responses);
            listView.setAdapter(adapter);
            if (listView != null) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        connectionCallback.connectTo(deviceList.get(position));

                    }

                });
            }
        }

        @Override
        public void onDataReceived(int data) {

            replot(data);

            int a = (data);
            incomingByte = (double) a ;
            StringBuilder s = new StringBuilder();
            s.append("");
            s.append(data);
            String strI = s.toString();
            Log.d("test", strI) ;
            try {
                if(isFirstIncomnigByte){
                    bw.write(strI);
                    isFirstIncomnigByte = false ;
                } else {
                    bw.write(",");
                    bw.write(strI);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


            mBuffer.add(data);
            if (data == 62 && !mBuffer.isEmpty()) {
                //if (data == 0x0D && !mBuffer.isEmpty() && mBuffer.get(mBuffer.size()-2) == 0xA0) {
                StringBuilder sb = new StringBuilder();
                for (int integer : mBuffer) {
                    sb.append((char)integer);
                }
                mBuffer.clear();
                mResponseBuffer.add(0, sb.toString());
                mResponsesAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void plot(){

        // setup the APR Levels plot:
        aprLevelsPlot = (XYPlot) findViewById(R.id.aprLevelsPlot);

        aprLevelsSeries = new SimpleXYSeries("APR Levels");
        aprLevelsSeries.useImplicitXVals();
        aprLevelsPlot.addSeries(aprLevelsSeries,
                new BarFormatter(Color.argb(100, 0, 200, 0), Color.rgb(0, 80, 0)));
        aprLevelsPlot.setDomainStepValue(3);
        aprLevelsPlot.setTicksPerRangeLabel(3);

        // per the android documentation, the minimum and maximum readings we can get from
        // any of the orientation sensors is -180 and 359 respectively so we will fix our plot's
        // boundaries to those values.  If we did not do this, the plot would auto-range which
        // can be visually confusing in the case of dynamic plots.
        aprLevelsPlot.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);

        // use our custom domain value formatter:
        aprLevelsPlot.setDomainValueFormat(new APRIndexFormat());

        // update our domain and range axis labels:
        aprLevelsPlot.setDomainLabel("Axis");
        aprLevelsPlot.getDomainLabelWidget().pack();
        aprLevelsPlot.setRangeLabel("Angle (Degs)");
        aprLevelsPlot.getRangeLabelWidget().pack();
        aprLevelsPlot.setGridPadding(15, 0, 15, 0);

        // setup the APR History plot:
        aprHistoryPlot = (XYPlot) findViewById(R.id.aprHistoryPlot);

        azimuthHistorySeries = new SimpleXYSeries("");
        azimuthHistorySeries.useImplicitXVals();
        pitchHistorySeries = new SimpleXYSeries("");
        pitchHistorySeries.useImplicitXVals();
        rollHistorySeries = new SimpleXYSeries("");
        rollHistorySeries.useImplicitXVals();

        aprHistoryPlot.setRangeBoundaries(0, 270, BoundaryMode.FIXED);
        aprHistoryPlot.setDomainBoundaries(0, 60, BoundaryMode.FIXED);
        aprHistoryPlot.addSeries(azimuthHistorySeries, new LineAndPointFormatter(Color.rgb(100, 100, 200), Color.BLACK, null, null));
        aprHistoryPlot.addSeries(pitchHistorySeries, new LineAndPointFormatter(Color.rgb(100, 200, 100), Color.BLACK, null ,null));
        aprHistoryPlot.addSeries(rollHistorySeries, new LineAndPointFormatter(Color.rgb(200, 100, 100), Color.BLACK, null, null));
        aprHistoryPlot.setDomainStepValue(31);
        aprHistoryPlot.setTicksPerRangeLabel(1);
        aprHistoryPlot.getGraphWidget().getBackgroundPaint().setColor(Color.BLUE);
        aprHistoryPlot.setDomainLabel("time axis");
        aprHistoryPlot.getDomainLabelWidget().pack();
        aprHistoryPlot.setRangeLabel("Amplitude");
        aprHistoryPlot.getRangeLabelWidget().pack();

        // setup checkboxes:
        hwAcceleratedCb = (CheckBox) findViewById(R.id.hwAccelerationCb);
        final PlotStatistics levelStats = new PlotStatistics(1000, false);
        final PlotStatistics histStats = new PlotStatistics(1000, false);

        aprLevelsPlot.addListener(levelStats);
        aprHistoryPlot.addListener(histStats);

        hwAcceleratedCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    aprLevelsPlot.setLayerType(View.LAYER_TYPE_NONE, null);
                    aprHistoryPlot.setLayerType(View.LAYER_TYPE_NONE, null);
                } else {
                    aprLevelsPlot.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    aprHistoryPlot.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
            }
        });

        showFpsCb = (CheckBox) findViewById(R.id.showFpsCb);
        showFpsCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                levelStats.setAnnotatePlotEnabled(b);
                histStats.setAnnotatePlotEnabled(b);
            }
        });

        // get a ref to the BarRenderer so we can make some changes to it:
        BarRenderer barRenderer = (BarRenderer) aprLevelsPlot.getRenderer(BarRenderer.class);
        if(barRenderer != null) {
            // make our bars a little thicker than the default so they can be seen better:
            barRenderer.setBarWidth(25);
        }

        // register for orientation sensor events:
        sensorMgr = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        for (Sensor sensor : sensorMgr.getSensorList(Sensor.TYPE_ORIENTATION)) {
            if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
                orSensor = sensor;
            }
        }

        // if we can't access the orientation sensor then exit:
        if (orSensor == null) {
            System.out.println("Failed to attach to orSensor.");
            cleanup();
        }

        sensorMgr.registerListener(this, orSensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void replot(int data){
        // update instantaneous data:
        Number[] series1Numbers = {data};
        aprLevelsSeries.setModel(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

        // get rid the oldest sample in history:
        if (rollHistorySeries.size() > HISTORY_SIZE) {
            rollHistorySeries.removeFirst();
            pitchHistorySeries.removeFirst();
            azimuthHistorySeries.removeFirst();
        }

        // add the latest history sample:
        azimuthHistorySeries.addLast(null, data);
            /*pitchHistorySeries.addLast(null, sensorEvent.values[1]);
            rollHistorySeries.addLast(null, sensorEvent.values[2]);*/

        // redraw the Plots:
        aprLevelsPlot.redraw();
        aprHistoryPlot.redraw();

    }

    private void cleanup() {
        // aunregister with the orientation sensor before exiting:
        sensorMgr.unregisterListener(this);
        finish();
    }

    // Called whenever a new orSensor reading is taken.
    @Override
    public synchronized void onSensorChanged(SensorEvent sensorEvent) {

        /*// update instantaneous data:
        Number[] series1Numbers = {sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]};
        aprLevelsSeries.setModel(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

        // get rid the oldest sample in history:
        if (rollHistorySeries.size() > HISTORY_SIZE) {
            rollHistorySeries.removeFirst();
            pitchHistorySeries.removeFirst();
            azimuthHistorySeries.removeFirst();
        }

        // add the latest history sample:
        azimuthHistorySeries.addLast(null, sensorEvent.values[0]);
        pitchHistorySeries.addLast(null, sensorEvent.values[1]);
        rollHistorySeries.addLast(null, sensorEvent.values[2]);

        // redraw the Plots:
        aprLevelsPlot.redraw();
        aprHistoryPlot.redraw();*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Not interested in this event
    }


}
