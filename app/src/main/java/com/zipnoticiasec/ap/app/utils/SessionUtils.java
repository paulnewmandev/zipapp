package com.zipnoticiasec.ap.app.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zipnoticiasec.ap.app.models.CategoryModel;
import com.zipnoticiasec.ap.app.models.NoticeModel;
import com.zipnoticiasec.ap.app.models.UserModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.alexrs.prefs.lib.Prefs;

/**
 * Created by Andres on 5/6/2019.
 */

public class SessionUtils {

    public enum PREFS  {user_data, categories_data, boletines_data}

    public enum PARAMS  {category, is_call_fragment_report, notice}


    public static UserModel getUser(Context mContext){
        String sData = Prefs.with(mContext).getString(PREFS.user_data.name(), "");
        Log.d("User Info", sData);
        //showToast(mContext, sData);
        Type mDataType = new TypeToken<UserModel>() {}.getType();
        return new Gson().fromJson(sData, mDataType);
    }

    public static List<CategoryModel> getCategories(Context mContext){
        String sData = Prefs.with(mContext).getString(PREFS.categories_data.name(), "");
        if(sData.isEmpty()) return new ArrayList<>();
        Type mDataType = new TypeToken<List<CategoryModel>>() {}.getType();
        return new Gson().fromJson(sData, mDataType);
    }

    public static List<NoticeModel> getBoletines(Context mContext){
        String sData = Prefs.with(mContext).getString(PREFS.boletines_data.name(), "");
        Log.d("Boletines Data", sData);
        if(sData.isEmpty()) return new ArrayList<>();
        Type mDataType = new TypeToken<List<NoticeModel>>() {}.getType();
        return new Gson().fromJson(sData, mDataType);
    }
}
