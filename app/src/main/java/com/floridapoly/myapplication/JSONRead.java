package com.floridapoly.myapplication;// Java program to read JSON from a file

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class JSONRead
{
    Context mContext;
    public JSONRead(Context context){
        mContext = context;
    }
    public void ReadFromFile() throws Exception
    {
        // parsing file "JSONExample.json"

        //File directroy = new File();
        //String filePath = directroy.getAbsolutePath();

        File directroy = new File(mContext.getFilesDir() + "/NickD");
        File jsonFile = new File(directroy.getAbsolutePath() + "/TheJsonFile.json");

        Object obj = new JSONParser().parse(new FileReader(jsonFile));

        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;

        // getting firstName and lastName
        String firstName = (String) jo.get("firstName");
        String lastName = (String) jo.get("lastName");

        System.out.println(firstName);
        Log.d("JSON", ""+ firstName);

        System.out.println(lastName);
        Log.d("JSON", ""+ lastName);

        // getting age
        long age = (long) jo.get("age");
        System.out.println(age);
        Log.d("JSON", ""+ age);

        // getting address
        Map address = ((Map)jo.get("address"));

        // iterating address Map
        Iterator<Map.Entry> itr1 = address.entrySet().iterator();
        while (itr1.hasNext()) {
            Map.Entry pair = itr1.next();
            System.out.println(pair.getKey() + " : " + pair.getValue());
            Log.d("JSON", "" + pair.getKey() + " : " + pair.getValue() );

        }

        // getting phoneNumbers
        JSONArray ja = (JSONArray) jo.get("phoneNumbers");

        // iterating phoneNumbers
        Iterator itr2 = ja.iterator();

        while (itr2.hasNext())
        {
            itr1 = ((Map) itr2.next()).entrySet().iterator();
            while (itr1.hasNext()) {
                Map.Entry pair = itr1.next();
                System.out.println(pair.getKey() + " : " + pair.getValue());
                Log.d("JSON", "" + pair.getKey() + " : " + pair.getValue() );
            }
        }
    }
}
