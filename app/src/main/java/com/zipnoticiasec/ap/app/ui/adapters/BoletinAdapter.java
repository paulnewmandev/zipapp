package com.zipnoticiasec.ap.app.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zipnoticiasec.ap.app.R;
import com.zipnoticiasec.ap.app.models.NoticeModel;
import com.zipnoticiasec.ap.app.ui.activities.ViewNoticeActivity;
import com.zipnoticiasec.ap.app.utils.SessionUtils;

import java.util.List;

/**
 * Created by Andres on 16/6/2019.
 */

public class BoletinAdapter extends RecyclerView.Adapter<BoletinAdapter.MyViewHolder> {

    private Context mContext;
    private List<NoticeModel> mListData;

    public BoletinAdapter(Context mContext, List<NoticeModel> mListData) {
        this.mContext = mContext;
        this.mListData = mListData;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView mImage;
        private TextView mTitle, mContent, mReadMore;
        private View mMainView;

        public MyViewHolder(View v) {
            super(v);
            mMainView = v.findViewById(R.id.itmBoletinMainView);
            mImage = v.findViewById(R.id.itmBoletinImage);
            mTitle = v.findViewById(R.id.itmBoletinTitle);
            mContent = v.findViewById(R.id.itmBoletinContent);
            mReadMore = v.findViewById(R.id.itmBoletinReadMore);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_boletin, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NoticeModel mModel = mListData.get(position);
        Picasso.get().load(mModel.imagen).into(holder.mImage);
        holder.mTitle.setText(mModel.titulo);
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(mModel.intro, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(mModel.intro);
        }
        holder.mContent.setText(result);
        holder.mContent.setMovementMethod(LinkMovementMethod.getInstance());
//        holder.mReadMore.setOnClickListener(v -> {
//            Intent intent = new Intent(mContext, ViewNoticeActivity.class);
//            intent.putExtra(SessionUtils.PARAMS.notice.name(), position);
//            mContext.startActivity(intent);
//        });
        holder.mMainView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ViewNoticeActivity.class);
            intent.putExtra(SessionUtils.PARAMS.notice.name(), position);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }
}
