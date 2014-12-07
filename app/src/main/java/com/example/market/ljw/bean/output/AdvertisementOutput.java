package com.example.market.ljw.bean.output;

import com.example.market.ljw.bean.Entity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GYH on 2014/11/9.
 */
public class AdvertisementOutput extends Entity{
    Gson gson = new Gson();
    List<Advertisement> advertisements = new ArrayList<Advertisement>();
    public void setContent(String result){
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(result);
        JsonObject jsonObject= jsonElement.getAsJsonObject();
        boolean success = jsonObject.get("success").getAsBoolean();
        if(success){
            JsonArray data = jsonObject.get("data").getAsJsonArray();
            Type listType = new TypeToken<ArrayList<Advertisement>>() {}.getType();
            ArrayList<Advertisement> advertisementArrayList = gson.fromJson(data, listType);
            setAdvertisements(advertisementArrayList);
            setSuccess(true);
        }else {
            String error_message = jsonObject.get("error_message").getAsString();
            setErrmsg(error_message);
            setSuccess(false);
        }
    }

    public List<Advertisement> getAdvertisements() {
        return advertisements;
    }

    public void setAdvertisements(List<Advertisement> advertisements) {
        this.advertisements = advertisements;
    }
}
