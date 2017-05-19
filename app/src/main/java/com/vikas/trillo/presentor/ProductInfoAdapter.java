package com.vikas.trillo.presentor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vikas.trillo.R;
import com.vikas.trillo.model.ProductInfoModel;

import java.util.List;

/**
 * Created by OFFICE on 5/18/2017.
 */

public class ProductInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ProductInfoModel> productInfoModelList;
    private Context context;
    private boolean isGridEnabled = false;
    private final int GRID_TYPE = 10;
    private final int SIMPLE_TYPE = 11;

    public ProductInfoAdapter(List<ProductInfoModel> productInfoModelList, Context context) {
        this.productInfoModelList = productInfoModelList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case GRID_TYPE:
                View v1 = inflater.inflate(R.layout.item_product_row_grid, parent, false);
                viewHolder = new ViewHolderGrid(v1);
                break;
            case SIMPLE_TYPE:
                View v2 = inflater.inflate(R.layout.item_product_row, parent, false);
                viewHolder = new ViewHolder(v2);
                break;
            default:
                View v3 = inflater.inflate(R.layout.item_product_row, parent, false);
                viewHolder = new ViewHolder(v3);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProductInfoModel productInfoModel = productInfoModelList.get(position);
        if (holder instanceof ViewHolderGrid) {
            ViewHolderGrid viewHolder = (ViewHolderGrid) holder;
            viewHolder.date.setText(productInfoModel.getAdded());
            viewHolder.title.setText(productInfoModel.getProductName());
            viewHolder.description.setText(productInfoModel.getDiscountInfo());
            viewHolder.coupon.setText(productInfoModel.getCoupon());
            Picasso.with(context).load(productInfoModel.getProductImage()).placeholder(R.color.windowColor).into(viewHolder.thumbnail);
        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.date.setText(productInfoModel.getAdded());
            viewHolder.title.setText(productInfoModel.getProductName());
            viewHolder.description.setText(productInfoModel.getDiscountInfo());
            viewHolder.coupon.setText(productInfoModel.getCoupon());
            Picasso.with(context).load(productInfoModel.getProductImage()).placeholder(R.color.windowColor).into(viewHolder.thumbnail);
        }

    }

    @Override
    public int getItemCount() {
        return productInfoModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView date, coupon, description, title;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            title = (TextView) itemView.findViewById(R.id.productTitle);
            date = (TextView) itemView.findViewById(R.id.productDate);
            coupon = (TextView) itemView.findViewById(R.id.productCoupon);
            description = (TextView) itemView.findViewById(R.id.productDescription);
        }
    }

    public class ViewHolderGrid extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView date, coupon, description, title;

        public ViewHolderGrid(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            title = (TextView) itemView.findViewById(R.id.productTitle);
            date = (TextView) itemView.findViewById(R.id.productDate);
            coupon = (TextView) itemView.findViewById(R.id.productCoupon);
            description = (TextView) itemView.findViewById(R.id.productDescription);
        }
    }

    public void setGridEnabled(boolean isGridEnabled) {
        this.isGridEnabled = isGridEnabled;
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(ProductInfoAdapter.class.getCanonicalName(), "" + isGridEnabled);
        if (isGridEnabled) {
            return GRID_TYPE;
        } else {
            return SIMPLE_TYPE;
        }
    }
}
