package com.test.asus.bluetoothtestapp;

import java.util.Observer;



public class SampleDynamicXYDatasource_o implements Runnable {




        private static final double FREQUENCY = 5; // larger is lower frequency
        private static final int MAX_AMP_SEED = 100;
        private static final int MIN_AMP_SEED = 10;
        private static final int AMP_STEP = 1;
        public static final int SINE1 = 0;
        public static final int SINE2 = 1;
        private static final int SAMPLE_SIZE = 30;
        private int phase = 0;
        private int sinAmp = 1;
        private Observable_o notifier;
        private boolean keepRunning = false;
        public double data ;


        {
            notifier = new Observable_o();
        }
        public SampleDynamicXYDatasource_o(double data){
            this.data = data ;
        }

        public void stopThread() {
            keepRunning = false;
        }

        @Override
        public void run() {
            try {
                keepRunning = true;
                boolean isRising = true;
                while (keepRunning) {

                    Thread.sleep(10); // decrease or remove to speed up the refresh rate.
                    phase++;
                    if (sinAmp >= MAX_AMP_SEED) {
                        isRising = false;
                    } else if (sinAmp <= MIN_AMP_SEED) {
                        isRising = true;
                    }

                    if (isRising) {
                        sinAmp += AMP_STEP;
                    } else {
                        sinAmp -= AMP_STEP;
                    }
                    notifier.notifyObservers();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public int getItemCount(int series) {
            return SAMPLE_SIZE;
        }

        public Number getX(int series, int index) {
            if (index >= SAMPLE_SIZE) {
                throw new IllegalArgumentException();
            }
            return index;
        }

        public Number getY(int series, int index) {
            if (index >= SAMPLE_SIZE) {
                throw new IllegalArgumentException();
            }

            double angle = (index + (phase))/FREQUENCY;
            double amp = sinAmp * Math.sin(angle);
            switch (series) {
                case SINE1:
                    //System.out.println(-amp);
                    return -amp;
                case SINE2:

                    return this.data;
                default:
                    throw new IllegalArgumentException();
            }
        }

        public void addObserver(Observer observer) {
            notifier.addObserver(observer);
        }

        public void removeObserver(Observer observer) {
            notifier.deleteObserver(observer);
        }

        public void setSineAmp(int sine){
            this.sinAmp = sine ;
        }

    }


