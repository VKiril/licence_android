package com.test.asus.bluetoothtestapp;

import com.androidplot.Plot;

import java.util.Observable;
import java.util.Observer;

public class PlotUpdater_o implements Observer {
    // redraws a plot whenever an update is received:
    Plot plot;

    public PlotUpdater_o(Plot plot) {
        this.plot = plot;
    }

    @Override
    public void update(Observable o, Object arg) {
        plot.redraw();
    }
}
