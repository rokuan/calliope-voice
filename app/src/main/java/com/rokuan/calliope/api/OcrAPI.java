package com.rokuan.calliope.api;

import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by LEBEAU Christophe on 04/04/2015.
 */
public class OcrAPI {
    private static final String appSID = "edb47fa9-5cf4-4d07-9d88-3d1fb60e170a";
    private static final String appKey = "a58ce099aa581db078eabc4ada7449c9";

    public static String getTextFromFile(File file){
        String requestUrl = "https://api.aspose.com/v1.1/ocr/recognize?appSID=" + appSID;

        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(appKey.getBytes(), "HmacSHA1"));
            mac.update(requestUrl.getBytes());
            String signature = Base64.encodeToString(mac.doFinal(), Base64.NO_PADDING);

            requestUrl += "&signature=" + signature;

            Log.i("onPreExecute", "Signed request URL: " + requestUrl);
        } catch (Exception x) {
            throw new RuntimeException(x);
        }

        HttpURLConnection connection = null;

        try {
            FileInputStream fstream = new FileInputStream(file);
            int fsize = fstream.available();

            connection = (HttpURLConnection) new URL(requestUrl).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Length", String.valueOf(fsize));

            OutputStream upload = connection.getOutputStream();
            byte[] buffer = new byte[10240];
            int len;

            while ((len = fstream.read(buffer)) != -1) {
                upload.write(buffer, 0, len);
            }

            upload.close();
            fstream.close();

            InputStream i = connection.getInputStream();
            String text = new Scanner(i).useDelimiter("\\A").next();
            i.close();

            return text;
        } catch (FileNotFoundException fnfx) {
            InputStream e = connection.getErrorStream();
            String text = new Scanner(e).useDelimiter("\\A").next();

            return text;
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }
}
