package com.vikas.trillo.listeners;

import com.vikas.trillo.model.ProductInfoModel;

import java.util.List;

/**
 * Created by OFFICE on 5/18/2017.
 */

public interface ProductGitListener {
    public void onProductReceived(List<ProductInfoModel> productInfoModels);
    public void onProductError();
}
