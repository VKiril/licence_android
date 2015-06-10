package com.test.asus.bluetoothtestapp;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import java.text.DecimalFormat;


public class PlotGraphController_o extends Activity {

    private XYPlot dynamicPlot;
    private PlotUpdater_o plotUpdater;
    SampleDynamicXYDatasource_o data;
    private Thread myThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // android boilerplate stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plot_activity);

        // get handles to our View defined in layout.xml:
        dynamicPlot = (XYPlot) findViewById(R.id.dynamicXYPlot);
        dynamicPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        dynamicPlot.getGraphWidget().getBackgroundPaint().setColor(Color.BLUE);
        dynamicPlot.getGraphWidget().getCursorLabelBackgroundPaint().setColor(Color.RED);

        plotUpdater = new PlotUpdater_o(dynamicPlot);

        // only display whole numbers in domain labels
        dynamicPlot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));

        // getInstance and position datasets:
        data = new SampleDynamicXYDatasource_o();
        SampleDynamicSeries_o sine1Series = new SampleDynamicSeries_o(data, 0, "Sine 1");
        SampleDynamicSeries_o sine2Series = new SampleDynamicSeries_o(data, 1, "Sine 2");

        LineAndPointFormatter formatter1 = new LineAndPointFormatter(
                Color.rgb(0, 0, 0), null, null, null);
       // formatter1.getLinePaint().setStrokeJoin(Paint.Join.ROUND);
        //formatter1.getLinePaint().setStrokeWidth(5);
        dynamicPlot.addSeries(sine1Series,
                formatter1);



        LineAndPointFormatter formatter2 =
                new LineAndPointFormatter(Color.rgb(0, 0, 200), null, null, null);

        //formatter2.getLinePaint().setStrokeWidth(1);
        //formatter2.getLinePaint().setStrokeJoin(Paint.Join.BEVEL);

        //formatter2.getFillPaint().setAlpha(220);
        dynamicPlot.addSeries(sine2Series, formatter2);

        // hook up the plotUpdater to the data model:
        data.addObserver(plotUpdater);

        // thin out domain tick labels so they dont overlap each other:
        dynamicPlot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
        dynamicPlot.setDomainStepValue(1);

        dynamicPlot.setRangeStepMode(XYStepMode.INCREMENT_BY_VAL);
        dynamicPlot.setRangeStepValue(10);

        dynamicPlot.setRangeValueFormat(new DecimalFormat("###.#"));

        // uncomment this line to freeze the range boundaries:
        dynamicPlot.setRangeBoundaries(-100, 100, BoundaryMode.FIXED);

        // create a dash effect for domain and range grid lines:
        DashPathEffect dashFx = new DashPathEffect(
                new float[] {PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        dynamicPlot.getGraphWidget().getDomainGridLinePaint().setPathEffect(dashFx);
        dynamicPlot.getGraphWidget().getRangeGridLinePaint().setPathEffect(dashFx);


    }

    @Override
    public void onResume() {
        // kick off the data generating thread:
        myThread = new Thread(data);
        myThread.start();
        super.onResume();
    }

    @Override
    public void onPause() {
        data.stopThread();
        super.onPause();
    }
}
