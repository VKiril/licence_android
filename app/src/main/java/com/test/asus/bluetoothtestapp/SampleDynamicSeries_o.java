package com.test.asus.bluetoothtestapp;

import com.androidplot.xy.XYSeries;


public class SampleDynamicSeries_o implements XYSeries {
        private SampleDynamicXYDatasource_o datasource;
        private int seriesIndex;
        private String title;

        public SampleDynamicSeries_o(SampleDynamicXYDatasource_o datasource, int seriesIndex, String title) {
            this.datasource = datasource;
            this.seriesIndex = seriesIndex;
            this.title = title;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public int size() {
            return datasource.getItemCount(seriesIndex);
        }

        @Override
        public Number getX(int index) {
            return datasource.getX(seriesIndex, index);
        }

        @Override
        public Number getY(int index) {
            return datasource.getY(seriesIndex, index);
        }



}

