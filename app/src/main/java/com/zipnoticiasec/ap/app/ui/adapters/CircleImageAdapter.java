package com.zipnoticiasec.ap.app.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.zipnoticiasec.ap.app.R;
import com.zipnoticiasec.ap.app.models.CircleImageModel;
import com.zipnoticiasec.ap.app.ui.fragments.FragmentCitizenReport;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Andres on 16/6/2019.
 */

public class CircleImageAdapter extends RecyclerView.Adapter<CircleImageAdapter.MyViewHolder> {

    private Context mContext;
    private List<CircleImageModel> mListData;
    private FragmentCitizenReport mFragment;

    public CircleImageAdapter(Context mContext, List<CircleImageModel> mListData, FragmentCitizenReport mFragment) {
        this.mContext = mContext;
        this.mListData = mListData;
        this.mFragment = mFragment;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView mImage;
        private View mMainView, mOpacityView;

        public MyViewHolder(View v) {
            super(v);
            mImage = v.findViewById(R.id.itmListCircleImage);
            mMainView = v.findViewById(R.id.itmListCircleImageMainView);
            mOpacityView = v.findViewById(R.id.itmListCircleImageOpacity);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_circle_image_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CircleImageModel mModel = mListData.get(position);
        Picasso.get().load(mModel.url_image).error(R.drawable.logo_color).into(holder.mImage);
        if(mModel.isSelected){
            holder.mOpacityView.setVisibility(View.GONE);
        }

        if(!mModel.isSelected){
            holder.mOpacityView.setVisibility(View.VISIBLE);
        }

        holder.mMainView.setOnClickListener(v -> {
            for(int i = 0; i<mListData.size(); i++){
                if(position != i){
                    mListData.get(i).isSelected = false;
                }else{
                    mListData.get(i).isSelected = true;
                }
            }
            notifyDataSetChanged();
            mFragment.mCurrentItem = position;
            mFragment.showDataItemSelected();
        });
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public void showNextItem(int currentItem){
        if(mListData.size()-1 == currentItem){
            mFragment.mCurrentItem = 0;
        }else{
            mFragment.mCurrentItem = currentItem + 1;
        }
        for(int i = 0; i<mListData.size(); i++){
            if(mFragment.mCurrentItem != i){
                mListData.get(i).isSelected = false;
            }else{
                mListData.get(i).isSelected = true;
            }
        }
        notifyDataSetChanged();
        mFragment.showDataItemSelected();
    }

    public void showPrevItem(int currentItem){
        if(currentItem == 0){
            mFragment.mCurrentItem = mListData.size()-1;
        }else{
            mFragment.mCurrentItem = currentItem - 1;
        }
        for(int i = 0; i<mListData.size(); i++){
            if(mFragment.mCurrentItem != i){
                mListData.get(i).isSelected = false;
            }else{
                mListData.get(i).isSelected = true;
            }
        }
        notifyDataSetChanged();
        mFragment.showDataItemSelected();
    }
}
