package com.example.voicenotes.stc;

import java.util.concurrent.TimeUnit;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class SpeechTextClass {

    private final String secret_key = "889cdd024b334d8280b27f5e90df1e94";
    private String task = "";
    private String transcript = "";

    public SpeechTextClass(String filePath) throws Exception {
        HttpURLConnection conn;
        URL endpoint = new URL("https://api.speechtext.ai/recognize?key=" + secret_key +"&language=ru-RU&punctuation=true&format=m4a");
        File file = new File(filePath);
        RandomAccessFile f = new RandomAccessFile(file, "r");
        long sz = f.length();
        byte[] post_body = new byte[(int) sz];
        f.readFully(post_body);
        f.close();
        conn = (HttpURLConnection) endpoint.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/octet-stream");
        conn.setDoOutput(true);
        conn.connect();
        OutputStream os = conn.getOutputStream();
        os.write(post_body);
        os.flush();
        os.close();
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            String result = response.toString();
            JSONObject json = new JSONObject(result);
            task = json.getString("id");
            URL res_endpoint = new URL("https://api.speechtext.ai/results?key=" + secret_key + "&task=" + task + "&summary=true&summary_size=15&highlights=true&max_keywords=15");
            JSONObject results;
            while (true) {
                conn = (HttpURLConnection) res_endpoint.openConnection();
                conn.setRequestMethod("GET");
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                response = new StringBuffer();
                String res;
                while ((res = in.readLine()) != null) {
                    response.append(res);
                }
                in.close();
                results = new JSONObject(response.toString());
                if (results.getString("status").equals("failed")) {
                    throw new Exception("Failed to transcribe!");
                }
                if (results.getString("status").equals("finished")) {
                    transcript = results.getJSONObject("results").getString("transcript");
                    break;
                }
                TimeUnit.SECONDS.sleep(15);
            }
        } else
            throw new Exception("Failed to transcribe!");

    }

    public String getTranscript() {
        return transcript;
    }

}
