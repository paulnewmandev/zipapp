package com.zipnoticiasec.ap.app.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.zipnoticiasec.ap.app.R;

public class DialogUtils {

    public static MaterialDialog showProgress(Context mCon, String mTitle, String mContent){
        return new MaterialDialog.Builder(mCon)
                .title(mTitle)
                .content(mContent)
                .cancelable(false)
                .autoDismiss(false)
                .progress(true, 0)
                .show();
    }

    public static MaterialDialog showContentDialog(Context mCon, String mTitle, String mContent){
        return new MaterialDialog.Builder(mCon)
                .title(mTitle)
                .content(mContent)
                .cancelable(false)
                .autoDismiss(false)
                .contentColorRes(R.color.colorBlack)
                .positiveText(R.string.accept)
                .show();
    }

    public static MaterialDialog showCustomAcceptCancelDialog(Context mCon, String mTitle, int mIdLayout){
        return new MaterialDialog.Builder(mCon)
                .title(mTitle)
                .customView(mIdLayout, false)
                .contentColorRes(R.color.colorBlack)
                .positiveText(R.string.accept)
                .negativeText(R.string.cancel)
                .cancelable(false)
                .autoDismiss(false)
                .positiveColorRes(R.color.primaryZip)
                .negativeColorRes(R.color.primaryZip)
                .onNegative((dialog, which) -> dialog.dismiss()).show();
    }

    public static MaterialDialog showCustomDialog(Context mContext, int mIdLayout){
        return new MaterialDialog.Builder(mContext)
                .title(null)
                .autoDismiss(false)
                .customView(mIdLayout, false)
                .show();
    }

    public static MaterialDialog showListDialog(String mTitle, int mArray, Context mContext){
        return new MaterialDialog.Builder(mContext)
                .title(mTitle)
                .items(mArray)
                .show();
    }

}
