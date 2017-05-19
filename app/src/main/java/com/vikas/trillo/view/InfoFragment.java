package com.vikas.trillo.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vikas.trillo.R;
import com.vikas.trillo.listeners.ProductGitListener;
import com.vikas.trillo.model.ProductInfoModel;
import com.vikas.trillo.network.ProductInfoTrillBit;
import com.vikas.trillo.presentor.ProductInfoAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by OFFICE on 5/17/2017.
 */

public class InfoFragment extends Fragment implements ProductGitListener {

    private final String TAG = InfoFragment.class.getCanonicalName();
    @BindView(R.id.rvProductInfo)
    RecyclerView rvProductInfo;
    @BindView(R.id.pbProductInfo)
    ProgressBar pbProductInfo;
    Unbinder unbinder;
    @BindView(R.id.tvProductError)
    TextView tvProductError;
    @BindView(R.id.fabShuffle)
    FloatingActionButton fabShuffle;
    private List<ProductInfoModel> productInfoModelList = new ArrayList<>();

    public static InfoFragment getInstance() {
        return new InfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupRecyclerView(false);
        initializeMembers();
        getProductFromServer();
        return view;
    }

    private void getProductFromServer() {
        //here get the product from server
        pbProductInfo.setVisibility(View.VISIBLE);
        tvProductError.setVisibility(View.GONE);
        ProductInfoTrillBit productInfoTrillBit = new ProductInfoTrillBit(this);
        productInfoTrillBit.getProductInfo();
    }

    private void initializeMembers() {
        //here initialize the members
    }

    private void setupRecyclerView(boolean isGridEnabled) {
        ProductInfoAdapter productInfoAdapter = new ProductInfoAdapter(productInfoModelList, getContext());
        productInfoAdapter.setGridEnabled(isGridEnabled);
        if (isGridEnabled) {
            rvProductInfo.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
            rvProductInfo.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            rvProductInfo.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            rvProductInfo.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        rvProductInfo.setItemAnimator(new DefaultItemAnimator());
        rvProductInfo.setHasFixedSize(true);
        rvProductInfo.setAdapter(productInfoAdapter);
        Log.d(TAG, "is grid enabled " + isGridEnabled);
    }

    @Override
    public void onProductReceived(List<ProductInfoModel> productInfoModels) {
        if (productInfoModels.size() > 0) {
            pbProductInfo.setVisibility(View.GONE);
            productInfoModelList.clear();
            productInfoModelList.addAll(productInfoModels);
            setupRecyclerView(false);
        } else {
            tvProductError.setVisibility(View.VISIBLE);
            makeToast(getString(R.string.product_success));
        }
    }

    @Override
    public void onProductError() {
        //product failure
        tvProductError.setVisibility(View.VISIBLE);
        makeToast(getString(R.string.error_product));
    }

    private void makeToast(String msg) {
        pbProductInfo.setVisibility(View.GONE);
        Snackbar.make(rvProductInfo, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tvProductError, R.id.fabShuffle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvProductError:
                getProductFromServer();
                break;
            case R.id.fabShuffle:
                if (productInfoModelList.size() > 0) {
                    shuffleProduct();
                } else {
                    makeToast(getString(R.string.product_success));
                }
                break;
        }
    }

    private void shuffleProduct() {
        if (rvProductInfo.getLayoutManager() instanceof GridLayoutManager) {
            //make linear here
            setupRecyclerView(false);
        } else {
            //make grid here
            setupRecyclerView(true);
        }
    }

}
