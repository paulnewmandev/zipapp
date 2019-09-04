package com.zipnoticiasec.ap.app.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.zipnoticiasec.ap.app.R;
import com.zipnoticiasec.ap.app.models.CategoryModel;
import com.zipnoticiasec.ap.app.utils.DialogUtils;
import com.zipnoticiasec.ap.app.utils.SessionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.zipnoticiasec.ap.app.utils.ApiUtils.ApiUrl;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.DATA;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.GET_CATEGORIES;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.MESSAGE;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.STATUS;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.SUCCESS;
import static com.zipnoticiasec.ap.app.utils.AppUtils.showToast;

/**
 * Created by Andres on 15/6/2019.
 */

public class FragmentDiscover extends Fragment {


    private View view;
    private TabLayout mTabs;
    private ViewPager mViewPager;
    private List<CategoryModel> mListCategories;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_discover, container, false);
        initViews();
        return view;
    }

    private void initViews(){
        mTabs = view.findViewById(R.id.frgDiscoverTabLayout);
        mViewPager = view.findViewById(R.id.frgDiscoverViewPager);
        getCategories();
    }

    private void getCategories(){
        MaterialDialog mDialog = DialogUtils.showProgress(getContext(), getString(R.string.title_get_data), getString(R.string.content_get_data));
        String mUrl = ApiUrl+GET_CATEGORIES;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mDialog.dismiss();
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    if(mJson.getString(STATUS).contentEquals(SUCCESS)){
                        String sData = mJson.getString(DATA);
                        Type mDataType = new TypeToken<List<CategoryModel>>() {}.getType();
                        mListCategories = new Gson().fromJson(sData, mDataType);
                        initFragment();
                    }else{
                        showToast(getContext(), mJson.getString(MESSAGE));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(getContext(), getString(R.string.error_json));
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

    private void initFragment(){
        Adapter mViewPagerAdapter = new Adapter(getActivity().getSupportFragmentManager());
        mTabs.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(mViewPagerAdapter);
        for(CategoryModel mModel : mListCategories){
            FragmentItemTabDiscover mFragment = new FragmentItemTabDiscover();
            Bundle args = new Bundle();
            args.putSerializable(SessionUtils.PARAMS.category.name(), mModel);
            mFragment.setArguments(args);
            mViewPagerAdapter.addFragment(mFragment, mModel.nombre);
            mViewPagerAdapter.notifyDataSetChanged();
        }
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }


}
