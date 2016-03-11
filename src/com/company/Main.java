package com.company;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class Main {
    private class WriterThread implements Runnable {
        String file;
        String content;

        public WriterThread(String file, String content) {
            this.file = file;
            this.content = content;
        }

        @Override
        public void run() {
            try {
                FileWriter fw = new FileWriter(file, true);
                fw.write(content);
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getRequest() {

        String url = "https://graph.api.smartthings.com/api/smartapps/installations/a51fe2e1-270c-46f6-958d-05ac1a8afa16/listAll?access_token=ee7e23fc-0f85-4484-a30b-0f2bb8990a4c";
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        JSONTokener tokener = null;

        DateFormat dirDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH");
        DateFormat contentDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String filePrefix = "YK_SmartThings_log_";
        StringBuilder stringBuilder = new StringBuilder();


        long currentTime = System.currentTimeMillis();
        long preTime = currentTime;
        int count = 0;
        File test = new File(dirDateFormat.format(preTime));
        test.mkdirs();
        WriterThread te = new WriterThread(dirDateFormat.format(preTime) + File.separator
                + filePrefix + fileDateFormat.format(preTime), stringBuilder.toString());
        new Thread(te).start();
        while (true) {
            try {

                JSONObject jsonObject = null;
                try {
                    tokener = new JSONTokener(uri.toURL().openStream());
                    jsonObject = new JSONObject(tokener);
                } catch (JSONException e) {
                    continue;
                }

                currentTime = System.currentTimeMillis();
                /*if (!dirDateFormat.format(preTime).equals(dirDateFormat.format(currentTime))) {
                    File file = new File(dirDateFormat.format(preTime));
                    file.mkdirs();
                    WriterThread writerThread = new WriterThread(dirDateFormat.format(preTime) + File.separator
                            + filePrefix + fileDateFormat.format(preTime), stringBuilder.toString());
                    new Thread(writerThread).start();
                    preTime = currentTime;
                    stringBuilder.setLength(0);
                } else*/ if (!fileDateFormat.format(preTime).equals(fileDateFormat.format(currentTime))) {
                    WriterThread writerThread = new WriterThread(dirDateFormat.format(preTime) + File.separator
                            + filePrefix + fileDateFormat.format(preTime), stringBuilder.toString());
                    new Thread(writerThread).start();
                    preTime = currentTime;
                    stringBuilder.setLength(0);
                }
                if (count == 10) {
                    System.out.println(contentDateFormat.format(currentTime));
                    count = 0;
                }
                stringBuilder.append("###\n" + contentDateFormat.format(currentTime) + "\n");
                stringBuilder.append(jsonObject + "\n");
                count++;
                Thread.sleep(1000);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // write your code here
        new Main().getRequest();
    }
}
