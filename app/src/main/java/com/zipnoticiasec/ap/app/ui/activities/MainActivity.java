package com.zipnoticiasec.ap.app.ui.activities;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zipnoticiasec.ap.app.R;
import com.zipnoticiasec.ap.app.ui.fragments.FragmentCitizenReport;
import com.zipnoticiasec.ap.app.ui.fragments.FragmentDiscover;
import com.zipnoticiasec.ap.app.ui.fragments.FragmentGenerateReport;
import com.zipnoticiasec.ap.app.ui.fragments.FragmentNewsletter;
import com.zipnoticiasec.ap.app.ui.fragments.FragmentProfile;
import com.zipnoticiasec.ap.app.utils.SessionUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private final float MIN_DISTANCE = 100;
    private float x1, x2;

    public ImageView mDiscover, mReport, mUpload;
    private TextView mTitle, mBoletin, mAlMomento;
    private CircleImageView mProfileImage;
    private Context mContext;
    private View mResaltBoletin, mResaltAlMomento;
    private FragmentCitizenReport mFragmentReport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initViews();
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event)
//    {
//        switch(event.getAction())
//        {
//            case MotionEvent.ACTION_DOWN:
//                x1 = event.getX();
//                break;
//            case MotionEvent.ACTION_UP:
//                x2 = event.getX();
//                float deltaX = x2 - x1;
//                Log.d("Viendo la distancia", String.valueOf(deltaX));
//                if (Math.abs(deltaX) > MIN_DISTANCE) {
//                    // Left to Right swipe action
//                    if (x2 > x1) {
//                        Toast.makeText(this, "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show ();
//                    }
//
//                    // Right to left swipe action
//                    else {
//                        Toast.makeText(this, "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
//                    }
//
//                }
//                break;
//        }
//        return super.onTouchEvent(event);
//    }

    private void initViews(){
        mDiscover = findViewById(R.id.mainDiscover);
        mReport = findViewById(R.id.mainReport);
        mUpload = findViewById(R.id.mainUpload);
        mTitle = findViewById(R.id.mainTitle);
        mBoletin = findViewById(R.id.mainBoletin);
        mAlMomento = findViewById(R.id.mainAlMomento);
        mProfileImage = findViewById(R.id.mainImgProfile);
        mResaltBoletin = findViewById(R.id.mainResaltBoletin);
        mResaltAlMomento = findViewById(R.id.mainResaltAlMomento);
        mFragmentReport = null;
        initActivity();
    }

    private void initActivity(){

        mDiscover.setOnClickListener(v -> {
            setFragment(getString(R.string.title_discover), new FragmentDiscover());
            mReport.setVisibility(View.VISIBLE);
            mUpload.setVisibility(View.GONE);
            mResaltBoletin.setVisibility(View.GONE);
            mResaltAlMomento.setVisibility(View.GONE);
            mFragmentReport = null;
        });

        mProfileImage.setOnClickListener(v -> {
            setFragment(getString(R.string.title_profile), new FragmentProfile());
            mReport.setVisibility(View.VISIBLE);
            mUpload.setVisibility(View.GONE);
            mResaltBoletin.setVisibility(View.GONE);
            mResaltAlMomento.setVisibility(View.GONE);
            mFragmentReport = null;
        });

        mBoletin.setOnClickListener(v -> {
            setFragment(getString(R.string.title_newsletter), new FragmentNewsletter());
            mReport.setVisibility(View.VISIBLE);
            mUpload.setVisibility(View.GONE);
            mResaltBoletin.setVisibility(View.VISIBLE);
            mResaltAlMomento.setVisibility(View.GONE);
            mFragmentReport = null;
        });

        mReport.setOnClickListener(v -> {
            mFragmentReport = new FragmentCitizenReport();
            Bundle args = new Bundle();
            args.putBoolean(SessionUtils.PARAMS.is_call_fragment_report.name(), true);
            mFragmentReport.setArguments(args);
            setFragment(getString(R.string.title_citizen_report), mFragmentReport);
            mReport.setVisibility(View.GONE);
            mUpload.setVisibility(View.VISIBLE);
            mResaltBoletin.setVisibility(View.GONE);
            mResaltAlMomento.setVisibility(View.GONE);
        });

        mUpload.setOnClickListener(v -> {
            mReport.setVisibility(View.VISIBLE);
            mUpload.setVisibility(View.GONE);
            setFragment(getString(R.string.title_citizen_report), new FragmentGenerateReport());
            mResaltBoletin.setVisibility(View.GONE);
            mResaltAlMomento.setVisibility(View.GONE);
            mFragmentReport = null;
        });

        mAlMomento.setOnClickListener(v -> {
            mFragmentReport = new FragmentCitizenReport();
            Bundle args = new Bundle();
            args.putBoolean(SessionUtils.PARAMS.is_call_fragment_report.name(), false);
            mFragmentReport.setArguments(args);
            setFragment(getString(R.string.title_at_the_time), mFragmentReport);
            mReport.setVisibility(View.VISIBLE);
            mUpload.setVisibility(View.GONE);
            mResaltBoletin.setVisibility(View.GONE);
            mResaltAlMomento.setVisibility(View.VISIBLE);
        });

        if(SessionUtils.getUser(mContext).imagen != null){
            if(!SessionUtils.getUser(mContext).imagen.isEmpty()){
                Picasso.get().load(SessionUtils.getUser(mContext).imagen).error(R.drawable.logo_color).into(mProfileImage);
            }
        }

        setFragment(getString(R.string.title_newsletter), new FragmentNewsletter());
    }

    public void setFragment(String title, final Fragment mFragment) {
        Handler mHandler = new Handler();
        mHandler.postDelayed(() -> {
            FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
            mTransaction.replace(R.id.containerMain, mFragment);
            mTransaction.commit();

        }, 1);
        mTitle.setText(title);
    }
}
