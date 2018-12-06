package com.floridapoly.myapplication;// Java program for write JSON to a file

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONWrite
{
    Context mContext;
    public JSONWrite(Context context){
        mContext = context;
    }
    public  void WriteToFile() throws FileNotFoundException
    {
        Log.d("JSON", "Started");
        // creating JSONObject
        JSONObject jo = new JSONObject();

        // putting data to JSONObject
        jo.put("lat", "John");
        jo.put("lng", "Smith");
        jo.put("IP", "");



        // writing JSON to file:"JSONExample.json" in cwd

        PrintWriter pw = null;
        try {

            File directroy = new File(mContext.getFilesDir() + "/NickD");
            File jsonFile = new File(directroy.getAbsolutePath() + "/TheJsonFile.json");
            boolean status = directroy.mkdir();

            if(!jsonFile.exists()){
                jsonFile.createNewFile();
            }


            Log.d("JSON", "Status: " + status);

            String filePath = directroy.getAbsolutePath();
            Log.d("JSON", "" + filePath);

            pw = new PrintWriter(filePath + "/TheJsonFile.json");
            pw.write(jo.toJSONString());

            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
