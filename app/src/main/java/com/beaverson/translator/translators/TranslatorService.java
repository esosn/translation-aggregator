package com.beaverson.translator.translators;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * TranslatorService abstracts interactions with
 * external APIs (Yandex, Yahoo, Baidu, etc) that
 * perform automated text translations
 */
public abstract class TranslatorService {
    // class members
    String sourceLang;
    String targetLang;
    HashMap<String,String> langs = new HashMap<String, String>();
    ResourceBundle resources;
    String apiKey;

    // constructor
    TranslatorService() {
        // create resources object
        try {
            resources = ResourceBundle.getBundle("strings");
        }
        catch(MissingResourceException e) {
            resources = ResourceBundle.getBundle("strings", Locale.ENGLISH);
        }

        // fetch and load supported languages
        try {
            fetchLangs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // abstract members

    // Gets the name of the service
    public abstract String getName();
    // Translate a block of text from sourceLang to targetLang
    public abstract String translate(String text) throws Exception;
    // Fetch and load supported languages into langs Dictionary
    protected abstract void fetchLangs() throws Exception;

    // accessors/mutators

    // Indicates whether or not the service supports automated source language detection
    // Override this if the concrete service supports such functionality
    public boolean canAutoDetectSourceLang() { return false; }
    // Gets the a list of supported languages
    public HashMap<String,String> getLanguages() { return langs; }
    // Sets the source language
    public void setSourceLang(String lang) { sourceLang = lang; }
    // Sets the target language
    public void setTargetLang(String lang) { targetLang = lang; }
    // Sets the source language
    public String getSourceLang() { return sourceLang; }
    // Sets the source language
    public String getTargetLang() { return targetLang; }
    // Sets the API key
    public void setApiKey(String key) { apiKey = key; }

    // package private methods

    // fetch a JSONObject from an HTTP service via POST
    static JSONObject getJsonObject(String url) throws Exception {
        return new JSONObject(getJsonString(url));
    }

    // fetch a JSONArray from an HTTP service via POST
    static JSONArray getJsonArray(String url) throws Exception {
        return new JSONArray(getJsonString(url));
    }

    // fetch a string representation of a JSON response from an HTTP service via POST
    static String getJsonString(String url) throws Exception {
        // thanks to http://stackoverflow.com/a/9606629/1046207
        DefaultHttpClient client = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("Content-type", "application/json");
        InputStream inputStream = null;
        String result = null;
        try {
            HttpResponse response = client.execute(httppost);
            if(response.getStatusLine().getStatusCode() != 200) {
                // TODO error happened; throw
            }
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } catch (Exception e) {
            // TODO error happened; throw
        }
        finally {
            try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
        }
        return result;
    }
}
