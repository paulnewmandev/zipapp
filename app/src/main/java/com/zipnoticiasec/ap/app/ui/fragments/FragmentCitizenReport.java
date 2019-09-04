package com.zipnoticiasec.ap.app.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zipnoticiasec.ap.app.R;
import com.zipnoticiasec.ap.app.models.CircleImageModel;
import com.zipnoticiasec.ap.app.models.ReportModel;
import com.zipnoticiasec.ap.app.ui.adapters.CircleImageAdapter;
import com.zipnoticiasec.ap.app.utils.DialogUtils;
import com.zipnoticiasec.ap.app.utils.SessionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.zipnoticiasec.ap.app.utils.ApiUtils.ApiUrl;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.DATA;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.GET_MOMENT;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.GET_REPORT;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.MESSAGE;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.STATUS;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.SUCCESS;
import static com.zipnoticiasec.ap.app.utils.AppUtils.showToast;

/**
 * Created by Andres on 16/6/2019.
 */

public class FragmentCitizenReport extends Fragment {

    private static int TIME_NOTICE = 20;

    private View view;
    private static RecyclerView recyclerImages;

    private ProgressBar mProgress;
    private List<ReportModel> mListData;
    private List<CircleImageModel> mListImages = new ArrayList<>();
    private Handler handler;
    private Runnable runnable;
    //private ViewPager viewPager;
    //public static VideoView mVideoView;

    public CircleImageAdapter mAdapter;

    public int mCurrentItem;

    //private SectionsPagerAdapter mSectionsPagerAdapter;


    //Views del contenido de la noticia
    private VideoView videoView;
    private ImageView mImageNotice, mPlayAction;
    private ProgressBar mProgressCircle;
    private TextView mDate, mContent, mAuthor, mCity;
    private CircleImageView mAuthorImage;
    private View mTapLeft, mTapRight;
    private boolean isCurrentVideo, isPause;
    private CountDownTimer mCount;
    private TextView mShowVideoProgress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_citizen_report, container, false);
        initViews();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        handler = null;
    }

    private void initViews(){
        recyclerImages = view.findViewById(R.id.frgCitizenRecyclerView);
        mProgress = view.findViewById(R.id.progressBar3);
        mImageNotice = view.findViewById(R.id.frgCitizenImage);
        videoView = view.findViewById(R.id.frgCitizenVideo);
        mDate = view.findViewById(R.id.frgCitizenDate);
        mContent = view.findViewById(R.id.frgCitizenContent);
        mAuthor = view.findViewById(R.id.frgCitizenNameUser);
        mCity = view.findViewById(R.id.frgCitizenCity);
        mAuthorImage = view.findViewById(R.id.frgCitizenImageUser);
        mProgressCircle = view.findViewById(R.id.progressBar4);
        mTapLeft = view.findViewById(R.id.frgCitizenTapLeft);
        mTapRight = view.findViewById(R.id.frgCitizenTapRight);
        mPlayAction = view.findViewById(R.id.frgCitizenPlayAction);
        mShowVideoProgress = view.findViewById(R.id.frgCitizenVideoProgress);
        if(!getArguments().getBoolean(SessionUtils.PARAMS.is_call_fragment_report.name())){
            mAuthor.setVisibility(View.GONE);
            mAuthorImage.setVisibility(View.GONE);
            mCity.setVisibility(View.GONE);
        }
        mCurrentItem = 0;
        getReportData();
    }

    private void getReportData(){
        MaterialDialog mDialog = DialogUtils.showProgress(getContext(), getString(R.string.title_get_data), getString(R.string.content_get_data));
        String mUrl;
        if(getArguments().getBoolean(SessionUtils.PARAMS.is_call_fragment_report.name())){
            mUrl = ApiUrl+GET_REPORT;
        }else{
            mUrl = ApiUrl+GET_MOMENT;
        }

        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mDialog.dismiss();
                Log.d("RESPONSE DATA", rawJsonResponse);
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    if(mJson.getString(STATUS).contentEquals(SUCCESS)){
                        String sData = mJson.getString(DATA);
                        Type mDataType = new TypeToken<List<ReportModel>>() {}.getType();
                        mListData = new Gson().fromJson(sData, mDataType);
                        initFragment();
                    }else{
                        showToast(getContext(), mJson.getString(MESSAGE));
                        mProgress.setVisibility(View.GONE);
                        mProgressCircle.setVisibility(View.GONE);
                        mAuthor.setText(mJson.getString(MESSAGE));
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
                mProgress.setVisibility(View.GONE);
                mProgressCircle.setVisibility(View.GONE);
                mAuthor.setText("Error Servidor");
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void initFragment(){
        mListImages.clear();
        if(mListData.size() > 0){
            for(ReportModel mModel : mListData){
                mListImages.add(new CircleImageModel(mModel.thumb, false));
            }
            mListImages.get(0).isSelected = true;
            recyclerImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            mAdapter = new CircleImageAdapter(getContext(), mListImages, this);
            recyclerImages.setAdapter(mAdapter);
            mCurrentItem = 0;

            mTapLeft.setOnClickListener(v -> {
                mAdapter.showPrevItem(mCurrentItem);
                if(isPause){
                    isPause = false;
                }
            });

            mTapRight.setOnClickListener(v -> {
                mAdapter.showNextItem(mCurrentItem);
                if(isPause){
                    isPause = false;
                }
            });

            mPlayAction.setOnClickListener(v -> {
                if(isPause){
                    mPlayAction.setImageResource(R.drawable.pause);
                    isPause = false;
                    mAdapter.showNextItem(mCurrentItem);
                }else{
                    mPlayAction.setImageResource(R.drawable.play_button);
                    isPause = true;
                }
            });

            initAnimation();

            showDataItemSelected();
        }
    }

    private void initAnimation(){
        Animation slideRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_right);
        Animation slideLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left);
        slideRight.setDuration(1000);
        slideLeft.setDuration(1000);
        recyclerImages.startAnimation(slideRight);
    }

    private void initTransation(){
        if(handler != null) handler.removeCallbacks(runnable);
        showProgress();
        handler = new Handler();
        runnable = () -> {
            if(!isPause){
                mAdapter.showNextItem(mCurrentItem);
            }

        };
        handler.postDelayed(runnable, TimeUnit.SECONDS.toMillis(TIME_NOTICE));
    }

    private void showProgress(){
        mProgress.setProgress(0);
        if(mCount != null){
            mCount.cancel();
            mCount.start();
        }else{
            mCount = new CountDownTimer(TimeUnit.SECONDS.toMillis(TIME_NOTICE), 200) {
                public void onTick(long millisUntilFinished) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if(!isPause){
                            mProgress.setProgress(mProgress.getProgress()+1, true);
                        }
                    }else{
                        if(!isPause){
                            mProgress.setProgress(mProgress.getProgress()+1);
                        }
                    }
                }

                public void onFinish() {
                    // DO something when 1 minute is up
                }
            }.start();
        }
    }

    public void showDataItemSelected(){
        mShowVideoProgress.setVisibility(View.GONE);
        mProgress.setProgress(0);
        recyclerImages.scrollToPosition(mCurrentItem);
        mProgressCircle.setVisibility(View.VISIBLE);
        if(videoView.isPlaying()){
            videoView.stopPlayback();
        }
        //Verifico si se trata de un video
        if(mListData.get(mCurrentItem).imagen.contains(".mp4")){
            mImageNotice.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            isCurrentVideo = true;
            if(mListData.get(mCurrentItem).video_file != null){
                mProgressCircle.setVisibility(View.GONE);
                Uri videoUri = Uri.parse(mListData.get(mCurrentItem).video_file.getAbsolutePath());
                videoView.setVideoURI(videoUri);
                videoView.start();
                initTransation();
                videoView.setOnCompletionListener(mp -> mAdapter.showNextItem(mCurrentItem));

            }else{
                getVideo(mListData.get(mCurrentItem).imagen, mCurrentItem);
            }
        }else{
            mImageNotice.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            isCurrentVideo = false;
            Picasso.get().load(mListData.get(mCurrentItem).imagen).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mImageNotice.setImageBitmap(bitmap);
                    mProgressCircle.setVisibility(View.GONE);
                    initTransation();
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    mProgressCircle.setVisibility(View.GONE);
                    showToast(getContext(), "Error al cargar imagen");
                    mAdapter.showNextItem(mCurrentItem);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }

        if(getArguments().getBoolean(SessionUtils.PARAMS.is_call_fragment_report.name())){
            mAuthor.setText(mListData.get(mCurrentItem).usuario);
            mCity.setText(mListData.get(mCurrentItem).ciudad);
            Picasso.get().load(mListData.get(mCurrentItem).imagen_usuario).into(mAuthorImage);
        }

        mDate.setText(mListData.get(mCurrentItem).fecha);

        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(mListData.get(mCurrentItem).descripcion, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(mListData.get(mCurrentItem).descripcion);
        }
        mContent.setText(result);
        mContent.setMovementMethod(LinkMovementMethod.getInstance());

        //mContent.setText(Html.fromHtml(mListData.get(mCurrentItem).descripcion));

    }

    private void getVideo(String videoUrl, int videoPosition){
        try{
            mShowVideoProgress.setVisibility(View.VISIBLE);
            new AsyncHttpClient().get(videoUrl, new FileAsyncHttpResponseHandler(getContext()) {
                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    mShowVideoProgress.setVisibility(View.GONE);
                    mListData.get(videoPosition).video_file = file;
                    if(isCurrentVideo && videoPosition == mCurrentItem){
                        initTransation();
                        mProgressCircle.setVisibility(View.GONE);
                        Uri videoUri = Uri.parse(file.getAbsolutePath());
                        videoView.setVideoURI(videoUri);
                        videoView.start();
                        videoView.setOnCompletionListener(mp -> {
                            mAdapter.showNextItem(mCurrentItem);
                        });
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    showToast(getContext(), getString(R.string.error_get_file));
                    mShowVideoProgress.setVisibility(View.GONE);
                    mAdapter.showNextItem(mCurrentItem);
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                    double mProgressValue = ( (double) bytesWritten/totalSize)*100;
                    mProgress.setProgress((int) mProgressValue);
                    mShowVideoProgress.setText((int) mProgressValue+"%");
                }
            });
        }catch (AssertionError e){
            e.printStackTrace();
        }

    }
}
