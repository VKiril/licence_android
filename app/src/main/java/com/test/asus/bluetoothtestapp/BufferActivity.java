package com.test.asus.bluetoothtestapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;


public class BufferActivity extends AppCompatActivity {

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

    private View mChart;
    private String[] mMonth = new String[] {
            "Jan", "Feb" , "Mar", "Apr", "May", "Jun",
            "Jul", "Aug" , "Sep", "Oct", "Nov", "Dec"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_buffer);

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSmoothBluetooth.stop();
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
            Toast.makeText(BufferActivity.this, "Bluetooth not found", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(BufferActivity.this, "Failed to connect to " + device.getName(), Toast.LENGTH_SHORT).show();
            if (device.isPaired()) {
                mSmoothBluetooth.doDiscovery();
            }
        }

        @Override
        public void onDiscoveryStarted() {
            Toast.makeText(BufferActivity.this, "Searching", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDiscoveryFinished() {

        }

        @Override
        public void onNoDevicesFound() {
            Toast.makeText(BufferActivity.this, "No device found", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDevicesFound(final List<Device> deviceList,
                                   final SmoothBluetooth.ConnectionCallback connectionCallback) {


            DevicesAdapter adapter = new DevicesAdapter(BufferActivity.this, deviceList);


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

            StringBuilder s = new StringBuilder();
            s.append("");
            s.append(data);
            String strI = s.toString();
            Log.d("test", strI) ;

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


    private void openChart(){
        int[] x = { 0,1,2,3,4,5,6,7, 8, 9, 10, 11 };
        int[] income = { 2000,2500,2700,3000,2800,3500,3700,3800, 0,0,0,0};
        int[] expense = {2200, 2700, 2900, 2800, 2600, 3000, 3300, 3400, 0, 0, 0, 0 };

// Creating an XYSeries for Income
        XYSeries incomeSeries = new XYSeries("Income");
// Creating an XYSeries for Expense
        XYSeries expenseSeries = new XYSeries("Expense");
// Adding data to Income and Expense Series
        for(int i=0;i<x.length;i++){
            incomeSeries.add(i,income[i]);
            expenseSeries.add(i,expense[i]);
        }

// Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
// Adding Income Series to the dataset
        dataset.addSeries(incomeSeries);
// Adding Expense Series to dataset
        dataset.addSeries(expenseSeries);

// Creating XYSeriesRenderer to customize incomeSeries
        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(Color.CYAN); //color of the graph set to cyan
        incomeRenderer.setFillPoints(true);
        incomeRenderer.setLineWidth(2f);
        incomeRenderer.setDisplayChartValues(true);
//setting chart value distance
        incomeRenderer.setDisplayChartValuesDistance(10);
//setting line graph point style to circle
        incomeRenderer.setPointStyle(PointStyle.CIRCLE);
//setting stroke of the line chart to solid
        incomeRenderer.setStroke(BasicStroke.SOLID);

// Creating XYSeriesRenderer to customize expenseSeries
        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
        expenseRenderer.setColor(Color.GREEN);
        expenseRenderer.setFillPoints(true);
        expenseRenderer.setLineWidth(2f);
        expenseRenderer.setDisplayChartValues(true);
//setting line graph point style to circle
        expenseRenderer.setPointStyle(PointStyle.SQUARE);
//setting stroke of the line chart to solid
        expenseRenderer.setStroke(BasicStroke.SOLID);

// Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle("Income vs Expense Chart");
        multiRenderer.setXTitle("Year 2014");
        multiRenderer.setYTitle("Amount in Dollars");

/***
 * Customizing graphs
 */
//setting text size of the title
        multiRenderer.setChartTitleTextSize(28);
//setting text size of the axis title
        multiRenderer.setAxisTitleTextSize(24);
//setting text size of the graph lable
        multiRenderer.setLabelsTextSize(24);
//setting zoom buttons visiblity
        multiRenderer.setZoomButtonsVisible(false);
//setting pan enablity which uses graph to move on both axis
        multiRenderer.setPanEnabled(false, false);
//setting click false on graph
        multiRenderer.setClickEnabled(false);
//setting zoom to false on both axis
        multiRenderer.setZoomEnabled(false, false);
//setting lines to display on y axis
        multiRenderer.setShowGridY(true);
//setting lines to display on x axis
        multiRenderer.setShowGridX(true);
//setting legend to fit the screen size
        multiRenderer.setFitLegend(true);
//setting displaying line on grid
        multiRenderer.setShowGrid(true);
//setting zoom to false
        multiRenderer.setZoomEnabled(false);
//setting external zoom functions to false
        multiRenderer.setExternalZoomEnabled(false);
//setting displaying lines on graph to be formatted(like using graphics)
        multiRenderer.setAntialiasing(true);
//setting to in scroll to false
        multiRenderer.setInScroll(false);
//setting to set legend height of the graph
        multiRenderer.setLegendHeight(30);
//setting x axis label align
        multiRenderer.setXLabelsAlign(Paint.Align.CENTER);
//setting y axis label to align
        multiRenderer.setYLabelsAlign(Paint.Align.LEFT);
//setting text style
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
//setting no of values to display in y axis
        multiRenderer.setYLabels(10);
// setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to 4000.
// if you use dynamic values then get the max y value and set here
        multiRenderer.setYAxisMax(4000);
//setting used to move the graph on xaxiz to .5 to the right
        multiRenderer.setXAxisMin(-0.5);
//setting used to move the graph on xaxiz to .5 to the right
        multiRenderer.setXAxisMax(11);
//setting bar size or space between two bars
//multiRenderer.setBarSpacing(0.5);
//Setting background color of the graph to transparent
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);
//Setting margin color of the graph to transparent
        multiRenderer.setMarginsColor(getResources().getColor(R.color.transparent_background));
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setScale(2f);
//setting x axis point size
        multiRenderer.setPointSize(4f);
//setting the margin size for the graph in the order top, left, bottom, right
        multiRenderer.setMargins(new int[]{30, 30, 30, 30});

        for(int i=0; i< x.length;i++){
            multiRenderer.addXTextLabel(i, mMonth[i]);
        }

// Adding incomeRenderer and expenseRenderer to multipleRenderer
// Note: The order of adding dataseries to dataset and renderers to multipleRenderer
// should be same
        multiRenderer.addSeriesRenderer(incomeRenderer);
        multiRenderer.addSeriesRenderer(expenseRenderer);
//this part is used to display graph on the xml
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart);
//remove any views before u paint the chart
        chartContainer.removeAllViews();
//drawing bar chart
        mChart = ChartFactory.getLineChartView(BufferActivity.this, dataset, multiRenderer);
//adding the view to the linearlayout
        chartContainer.addView(mChart);

    }
}
