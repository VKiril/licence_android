package com.test.asus.bluetoothtestapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperations {
    String path = "/sdcard/com.licence/";
    public FileOperations() {

    }

    public String delete(String fname){
        String fpath = path + fname + ".txt";
        File file = new File(fpath);
        if(!file.exists()){
            return "this file do not exist" ;
        }
        file.delete();
        return "file was deleted";
    }

    public Boolean write(String fname, String fcontent){
        try {

            String fpath = path + fname + ".txt";

            File file = new File(fpath);

            // If file does not exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fcontent);
            bw.close();

            Log.d("Suceess", "Sucess");
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public String read(String fname){

        BufferedReader br = null;
        String response = null;

        try {

            StringBuffer output = new StringBuffer();
            String fpath = path + fname + ".txt";

            br = new BufferedReader(new FileReader(fpath));
            String line = "";
            while ((line = br.readLine()) != null) {
                output.append(line +"n");
            }
            response = output.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }

        return response.substring(0,response.length()-1);
    }
}
