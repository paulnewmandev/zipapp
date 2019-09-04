package com.zipnoticiasec.ap.app.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.zipnoticiasec.ap.app.R;
import com.zipnoticiasec.ap.app.models.CategoryModel;
import com.zipnoticiasec.ap.app.ui.fragments.FragmentGenerateReport;

import java.util.List;

/**
 * Created by Andres on 17/6/2019.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>{

    private Context mContext;
    private FragmentGenerateReport mFragment;
    private List<CategoryModel> mListData;
    private MaterialDialog mDialog;

    public CategoryAdapter(Context mContext, FragmentGenerateReport mFragment, List<CategoryModel> mListData, MaterialDialog mDialog) {
        this.mContext = mContext;
        this.mFragment = mFragment;
        this.mListData = mListData;
        this.mDialog = mDialog;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView mNameCategory;

        public MyViewHolder(View v) {
            super(v);
            mNameCategory = v.findViewById(R.id.itemListCategory);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_category, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mNameCategory.setText(mListData.get(position).nombre);
        holder.mNameCategory.setOnClickListener(v -> {
            mDialog.dismiss();
            mFragment.mCategorySelected = position;
            mFragment.mSelectCategory.setText(mListData.get(position).nombre);
        });

    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }
}
