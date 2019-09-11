package com.zipnoticiasec.ap.app.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.zipnoticiasec.ap.app.R;
import com.zipnoticiasec.ap.app.models.NoticeModel;
import com.zipnoticiasec.ap.app.ui.activities.BrowserActivity;
import com.zipnoticiasec.ap.app.ui.activities.ViewNoticeActivity;
import com.zipnoticiasec.ap.app.utils.SessionUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.alexrs.prefs.lib.Prefs;

/**
 * Created by Andres on 16/6/2019.
 */

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.MyViewHolder> {

    private Context mContext;
    private List<NoticeModel> mListData;

    public DiscoverAdapter(Context mContext, List<NoticeModel> mListData) {
        this.mContext = mContext;
        this.mListData = mListData;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        private View mMainView;
        private ImageView mImage;
        private TextView mTitle, mSource, mContent, mDate;
        private CircleImageView mImageSource;

        public MyViewHolder(View v) {
            super(v);
            mMainView = v.findViewById(R.id.itmDiscoverMainView);
            mImage = v.findViewById(R.id.itmDiscoverImage);
            mTitle = v.findViewById(R.id.itmDiscoverTitle);
            mSource = v.findViewById(R.id.itmDiscoverSource);
            mContent = v.findViewById(R.id.itmDiscoverContent);
            mDate = v.findViewById(R.id.itmDiscoverDate);
            mImageSource = v.findViewById(R.id.itmDiscoverLogoSource);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_discover, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NoticeModel mModel = mListData.get(position);
        Picasso.get().load(mModel.imagen).into(holder.mImage);
        if(mModel.logo_fuente != null){
            if(!mModel.logo_fuente.isEmpty()){
                Picasso.get().load(mModel.logo_fuente).into(holder.mImageSource);
            }else{
                holder.mImageSource.setVisibility(View.GONE);
            }
        }else{
            holder.mImageSource.setVisibility(View.GONE);
        }
        holder.mTitle.setText(mModel.titulo_noticia);
        holder.mSource.setText(mModel.titulo);
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(mModel.intro, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(mModel.intro);
        }
        holder.mContent.setText(result);
        holder.mContent.setMovementMethod(LinkMovementMethod.getInstance());
        holder.mContent.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, BrowserActivity.class);
            intent.putExtra("source", mModel.fuente);
            intent.putExtra("url", mModel.fuente);
            mContext.startActivity(intent);
        });
        holder.mDate.setText(mModel.fecha);
        holder.mMainView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, BrowserActivity.class);
            intent.putExtra("source", mModel.fuente);
            intent.putExtra("url", mModel.fuente);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }
}
