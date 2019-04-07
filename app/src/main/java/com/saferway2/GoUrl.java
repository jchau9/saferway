package com.saferway2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

public class GoUrl implements Runnable {
    private String sfood;
    private String toprint;
    private boolean finished = false;

    public GoUrl(String url){
        this.sfood = url;
    }

    @Override
    public void run() {
        System.out.println("1-=================================-");
        String jsonText;
        try  {
            URL url = new URL(sfood);
            System.out.println("2-=================================-");
            InputStream is = url.openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            System.out.println("3-=================================-");
            StringBuilder sb = new StringBuilder();
            int cp;
            while((cp=rd.read()) != -1) {
                sb.append((char)cp);
            }

            System.out.println("4-=================================-");
            jsonText = sb.toString();
            toprint = jsonText; //+= json.get("list");
            System.out.println(toprint);

            is.close();
            System.out.println("6-=================================-");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("7-=================================-");
        System.out.println(toprint);
        finished = true;

    }

    public String getToprint(){
        return toprint;
    }
    public boolean getfinished(){
        return finished;
    }
}