package com.zipnoticiasec.ap.app.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Andres on 15/6/2019.
 */

public class AppUtils {

    public static void showToast(Context mContext, String mMessage){
        Toast.makeText(mContext, mMessage, Toast.LENGTH_LONG).show();
    }
}
