package care.example.careevv.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import care.example.careevv.preferences.LoggedInUser;

public class ParseJasonLang {
    Context context;
    String value;
    LoggedInUser loggedInUser;
    String parseObj;

    public  ParseJasonLang(Context context){
        this.context=context;
        loggedInUser= new LoggedInUser(context);

    }

    public String getJsonToString(String json){

        try {
            JSONObject jsonObject = new JSONObject(loggedInUser.getLanguage_response());

            value =jsonObject.getString(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return value;
    }
}
