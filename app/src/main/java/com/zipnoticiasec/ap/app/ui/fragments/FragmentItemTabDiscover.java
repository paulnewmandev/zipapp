package com.zipnoticiasec.ap.app.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.zipnoticiasec.ap.app.R;
import com.zipnoticiasec.ap.app.models.CategoryModel;
import com.zipnoticiasec.ap.app.models.NoticeModel;
import com.zipnoticiasec.ap.app.ui.adapters.DiscoverAdapter;
import com.zipnoticiasec.ap.app.utils.SessionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.zipnoticiasec.ap.app.utils.ApiUtils.ApiUrl;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.DATA;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.GET_DISCOVER;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.MESSAGE;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.STATUS;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.SUCCESS;
import static com.zipnoticiasec.ap.app.utils.AppUtils.showToast;

/**
 * Created by Andres on 16/6/2019.
 */

public class FragmentItemTabDiscover extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private ProgressBar mProgress;
    private List<NoticeModel> mListData;
    private CategoryModel mCategory;
    private DiscoverAdapter madapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.item_fragment_discover, container, false);
        initViews();
        return view;
    }

    private void initViews(){
        recyclerView = view.findViewById(R.id.simpleRecyclerView);
        mProgress = view.findViewById(R.id.progressBar2);
        mCategory = (CategoryModel) getArguments().getSerializable(SessionUtils.PARAMS.category.name());
        getDiscoverData();
    }

    private void getDiscoverData(){
        mProgress.setVisibility(View.VISIBLE);
        String mUrl = ApiUrl+GET_DISCOVER+"?id_categoria="+mCategory.id;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mProgress.setVisibility(View.GONE);
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    if(mJson.getString(STATUS).contentEquals(SUCCESS)){
                        String sData = mJson.getString(DATA);
                        Type mDataType = new TypeToken<List<NoticeModel>>() {}.getType();
                        mListData = new Gson().fromJson(sData, mDataType);
                        initFragment();
                    }else {
                        showToast(getContext(), mJson.getString(MESSAGE));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(getContext(), getString(R.string.error_json));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                mProgress.setVisibility(View.GONE);
                showToast(getContext(), "Error al obtener información de "+mCategory.nombre);
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void initFragment(){
        madapter = new DiscoverAdapter(getContext(), mListData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(madapter);
    }

}
