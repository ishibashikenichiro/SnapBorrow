package com.bookkos.bircle;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by yk.Leonier on 2015/05/27.
 */
public class BircleTools {
    static final private String FILE_SERVER_HOST = "server_host_name.txt";

//    static final String BircleHome="http://130.158.80.42:80";
    static public String BircleHome = "";

    public static void initServerHost(Context context){
        String retvalue = readFromFile(context, FILE_SERVER_HOST);
        if(!retvalue.equals("")){
            BircleHome = retvalue;
        }
    }

    public static void setServerHost(Context context, String hostName){
        String value = "";

        if(!hostName.equals("")){
            value = hostName;

            if(!value.startsWith("http")){
                value = "http://" + value;
            }

            if(value.endsWith("/")){
                value = value.substring(0, value.length() - 2);
            }

            BircleHome = value;
            writeToFile(context, FILE_SERVER_HOST, value);
        }
    }

    private static void writeToFile(Context context, String file, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(file, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readFromFile(Context context, String file) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

}
