package com.beaverson.translator.translators;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.Iterator;

/**
 * Implements calls to the Yandex translation API
 * http://api.yandex.com/translate/
 */
public class Yandex extends TranslatorService {
    // members
    // TODO move these to settings??
    private static final String getLangsUrl = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=%s&ui=en";
    private static final String translateUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=%s&lang=%s&text=%s";

    // constructor
    public Yandex(){
        // TODO move this to settings
        setApiKey("trnsl.1.1.20141028T073718Z.556201ba5e972fc4.aff2579ee8fe26c5de5f45970ade89bdcafe10d7");
    }

    // abstract implementations
    @Override
    public String getName() {
        return resources.getString("yandex_name");
    }

    @Override
    public String translate(String text) throws Exception {
        JSONObject json = getJsonObject(String.format(translateUrl, apiKey,
                // empty sourceLang means auto detect source language
                TextUtils.isEmpty(sourceLang) ? targetLang : sourceLang + "-" + targetLang,
                text));
        if(json.isNull("code")) return resources.getString("translator_unavailable_message");
        // 200 = success
        else if (json.getInt("code") != 200) {
            return String.format(resources.getString("translation_error_message"),
                    json.getString("code") + " " +
                    json.optString("message", resources.getString("unknown_error_reason")));
        }
        return json.optString("text", null);
    }

    @Override
    protected void fetchLangs() throws Exception {
        JSONObject json = getJsonObject(String.format(getLangsUrl, apiKey));
        if(!json.isNull("code")) {
            // TODO error happened; throw
        }
        else if(json.isNull("langs")) {
            // TODO error happened; throw
        }
        JSONObject jsLangs = json.getJSONObject("langs");
        Iterator<String> i = jsLangs.keys();
        String k;
        while(i.hasNext()) {
            k = i.next();
            langs.put(k,jsLangs.getString(k));
        }
        // for automatic detection
        langs.put("", resources.getString("automatic_detection"));
    }

    @Override
    public boolean canAutoDetectSourceLang() {
        return true;
    }
}
