package com.zipnoticiasec.ap.app.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.zipnoticiasec.ap.app.R;
import com.zipnoticiasec.ap.app.models.BannerModel;
import com.zipnoticiasec.ap.app.models.NoticeModel;
import com.zipnoticiasec.ap.app.ui.adapters.BoletinAdapter;
import com.zipnoticiasec.ap.app.utils.DialogUtils;
import com.zipnoticiasec.ap.app.utils.SessionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import me.alexrs.prefs.lib.Prefs;

import static com.zipnoticiasec.ap.app.utils.ApiUtils.ApiUrl;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.DATA;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.GET_BANNERS;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.GET_BOLETIN;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.MESSAGE;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.STATUS;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.SUCCESS;
import static com.zipnoticiasec.ap.app.utils.AppUtils.showToast;
import static com.zipnoticiasec.ap.app.utils.FormUtils.currentDate;
import static com.zipnoticiasec.ap.app.utils.FormUtils.dateToStringLongDate;
import static com.zipnoticiasec.ap.app.utils.FormUtils.dateToStringyyyyMMdd;
import static com.zipnoticiasec.ap.app.utils.FormUtils.yyyyMMddToDate;

/**
 * Created by Andres on 8/6/2019.
 */

public class FragmentNewsletter extends Fragment {

    private View view;
    private ImageView mPrev, mNext, mBanner;
    private TextView mDay, mMonth;
    private RecyclerView recyclerView;
    private String mDate = "";
    private List<NoticeModel> mListData;
    private List<BannerModel> mListBanners;
    private BoletinAdapter mAdapter;
    private Date mDateObject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_newsletter, container, false);
        initViews();
        return view;
    }

    private void initViews(){
        mPrev = view.findViewById(R.id.newsLetterPrev);
        mNext = view.findViewById(R.id.newsLetterNext);
        mBanner = view.findViewById(R.id.newsLetterBanner);
        mDay = view.findViewById(R.id.newsLetterDay);
        mMonth = view.findViewById(R.id.newsLetterMonthYear);
        recyclerView = view.findViewById(R.id.simpleRecyclerView);
        mDateObject = new Date();
        mDate = currentDate();
        getBannersData();
        getBoletinData();
    }

    private void getBoletinData(){
        MaterialDialog mDialog = DialogUtils.showProgress(getContext(), getString(R.string.title_get_data), getString(R.string.content_get_data));
        String mUrl = ApiUrl+GET_BOLETIN+"?fecha="+mDate;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mDialog.dismiss();
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    if(mJson.getString(STATUS).contentEquals(SUCCESS)){
                        String sData = mJson.getString(DATA);
                        Type mDataType = new TypeToken<List<NoticeModel>>() {}.getType();
                        mListData = new Gson().fromJson(sData, mDataType);
                        Prefs.with(getContext()).save(SessionUtils.PREFS.boletines_data.name(), sData);
                    }else{
                        showToast(getContext(), mJson.getString(MESSAGE));
                        mListData = new ArrayList<>();
                    }
                    initFragment();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                mDialog.dismiss();
                showToast(getContext(), getString(R.string.error_server));
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void getBannersData(){
        String mUrl = ApiUrl+GET_BANNERS;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    if(mJson.getString(STATUS).contentEquals(SUCCESS)){
                        String sData = mJson.getString(DATA);
                        Type mDataType = new TypeToken<List<BannerModel>>() {}.getType();
                        mListBanners = new Gson().fromJson(sData, mDataType);
                        showBanner();
                    }else{
                        showToast(getContext(), mJson.getString(MESSAGE));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {

            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });

    }

    private void showBanner(){
        int posBanner = (int) (Math.random() * mListBanners.size());
        if(posBanner == mListBanners.size()) posBanner--;
        Picasso.get().load(mListBanners.get(posBanner).imagen).into(mBanner);
    }

    private void initFragment(){
        String mLongDate = dateToStringLongDate(mDateObject);
        String[] mFragmentDate = mLongDate.split(" ");
        String mStrDate = mFragmentDate[0].substring(0, 1).toUpperCase()+mFragmentDate[0].substring(1)+" "+mFragmentDate[1];
        String mStrDate2 = mFragmentDate[2]+"\n"+mFragmentDate[3];
        mDay.setText(mStrDate);
        mMonth.setText(mStrDate2);

        mPrev.setOnClickListener(v -> {
            mDateObject.setTime(mDateObject.getTime() - TimeUnit.DAYS.toMillis(1));
            mDate = dateToStringyyyyMMdd(mDateObject);
            getBoletinData();
        });

        mNext.setOnClickListener(v -> {
            mDateObject.setTime(mDateObject.getTime() + TimeUnit.DAYS.toMillis(1));
            mDate = dateToStringyyyyMMdd(mDateObject);
            getBoletinData();
        });

        mDay.setOnClickListener(v -> dialogGetDate());

        mMonth.setOnClickListener(v -> dialogGetDate());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mAdapter = new BoletinAdapter(getContext(), mListData);

        recyclerView.setAdapter(mAdapter);
    }

    private void dialogGetDate(){
        MaterialDialog mDialog = DialogUtils.showCustomAcceptCancelDialog(getContext(), "Indique la fecha a consultar", R.layout.dialog_calendar);
        View v = mDialog.getCustomView();
        DatePicker mPicker = v.findViewById(R.id.datePicker);
        mDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v1 -> {
            int month = mPicker.getMonth();
            month++;
            String sMonth = String.valueOf(month);
            String sDay = String.valueOf(mPicker.getDayOfMonth());

            if(month < 10) sMonth = "0"+month;

            if(mPicker.getDayOfMonth() < 10) sDay = "0"+mPicker.getDayOfMonth();
            mDate = mPicker.getYear()+"-"+sMonth+"-"+sDay;
            mDateObject = yyyyMMddToDate(mDate);
            getBoletinData();
            mDialog.dismiss();
        });
    }
}
