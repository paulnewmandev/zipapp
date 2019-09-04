package com.zipnoticiasec.ap.app.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zipnoticiasec.ap.app.R;
import com.zipnoticiasec.ap.app.models.CommentModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zipnoticiasec.ap.app.utils.FormUtils.dateToStringddMMMyyyy;
import static com.zipnoticiasec.ap.app.utils.FormUtils.yyyyMMddTHHmmssToDate;

/**
 * Created by Andres on 18/6/2019.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

    private Context mContext;
    private List<CommentModel> mListData;

    public CommentsAdapter(Context mContext, List<CommentModel> mListData) {
        this.mContext = mContext;
        this.mListData = mListData;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView mImage;
        private TextView mAuthor, mComment, mDate;

        public MyViewHolder(View v) {
            super(v);
            mImage = v.findViewById(R.id.commentImgProfile);
            mAuthor = v.findViewById(R.id.commentAuthor);
            mComment = v.findViewById(R.id.commentContent);
            mDate = v.findViewById(R.id.commentTimeAgo);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_comments, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CommentModel mModel = mListData.get(position);
        Picasso.get().load(mModel.imagen).into(holder.mImage);
        holder.mAuthor.setText(mModel.nombres);
        holder.mComment.setText(mModel.comentario);
        holder.mDate.setText(dateToStringddMMMyyyy(yyyyMMddTHHmmssToDate(mModel.fecha)));
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }
}
