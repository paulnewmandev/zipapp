package com.zipnoticiasec.ap.app.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zipnoticiasec.ap.app.R;
import com.zipnoticiasec.ap.app.ui.activities.BrowserActivity;
import com.zipnoticiasec.ap.app.ui.activities.LoginActivity;
import com.zipnoticiasec.ap.app.utils.SessionUtils;

import de.hdodenhof.circleimageview.CircleImageView;
import me.alexrs.prefs.lib.Prefs;

import static com.zipnoticiasec.ap.app.models.UserModel.SOCIAL_GOOGLE;

/**
 * Created by Andres on 15/6/2019.
 */

public class FragmentProfile extends Fragment {

    private View view;
    private CircleImageView mProfile;
    private TextView mUserName, mPreferences, mContact, mConditions, mExitApp;
    private ImageView mSocialImage, mFacebook, mInstagram, mTwitter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews();
        return view;
    }

    private void initViews(){
        mProfile = view.findViewById(R.id.frgProfileImage);
        mUserName = view.findViewById(R.id.frgProfileNameUser);
        mPreferences = view.findViewById(R.id.frgProfilePreferences);
        mContact = view.findViewById(R.id.frgProfileContact);
        mConditions = view.findViewById(R.id.frgProfileConditions);
        mExitApp = view.findViewById(R.id.frgProfileExitApp);
        mSocialImage = view.findViewById(R.id.frgProfileSocial);
        mFacebook = view.findViewById(R.id.frgProfileSocialFacebook);
        mInstagram = view.findViewById(R.id.frgProfileSocialInstagram);
        mTwitter = view.findViewById(R.id.frgProfileSocialTwitter);
        initFragment();
    }

    private void initFragment(){
        Picasso.get().load(SessionUtils.getUser(getContext()).imagen).error(R.drawable.logo_color).into(mProfile);

        mUserName.setText(SessionUtils.getUser(getContext()).nombres);

        if(SessionUtils.getUser(getContext()).social == SOCIAL_GOOGLE){
            mSocialImage.setImageDrawable(getResources().getDrawable(R.drawable.google_login));
        }

        mPreferences.setOnClickListener(v -> {});

        mFacebook.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra("source", "Facebook");
            intent.putExtra("url", "https://www.facebook.com/ZipNoticiasEc");
            startActivity(intent);
        });

        mInstagram.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra("source", "Instagram");
            intent.putExtra("url", "https://www.instagram.com/zipnoticiasec");
            startActivity(intent);
        });

        mTwitter.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), BrowserActivity.class);
            intent.putExtra("source", "Twitter");
            intent.putExtra("url", "https://twitter.com/ZipNoticiasEc");
            startActivity(intent);
        });

        mContact.setOnClickListener(v -> {
            String mailto = "mailto:contacto@zipnoticias.ec" +
                    "?cc=" + SessionUtils.getUser(getContext()).email +
                    "&subject=" + Uri.encode("Contacto ZIP") +
                    "&body=" + Uri.encode("Escribe los detalles \n");
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailto));
            try {
                startActivity(emailIntent);
            } catch (ActivityNotFoundException e) {
                //TODO: Handle case where no email app is available
            }
        });

        mConditions.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://meinpros.com/zipnoticiasec/terminos.php"))));

        mExitApp.setOnClickListener(v -> {
            Prefs.with(getContext()).removeAll();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        });
    }
}
