package com.vikas.trillo.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.vikas.trillo.listeners.ProductGitListener;
import com.vikas.trillo.model.ProductInfoModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vikas.trillo.utils.WebConstants.GIT_API_END;

/**
 * Created by OFFICE on 5/18/2017.
 */

public class ProductInfoTrillBit {
    private final ProductGitListener productListener;
    private ApiServiceNetwork apiServiceNetwork;

    public ProductInfoTrillBit(ProductGitListener productGitListener) {
        apiServiceNetwork = new ApiServiceNetwork();
        this.productListener = productGitListener;
    }

    public void getProductInfo() {
        try {
            apiServiceNetwork.getNetworkService(null, GIT_API_END).getRawProductJson().enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.code() == 200) {
                        //here you got the response
                        parseResponse(response.body());
                    } else {
                        //something bad happened
                        productListener.onProductError();
                    }
                }

                private void parseResponse(ResponseBody body) {
                    List<ProductInfoModel> productInfoModels = new ArrayList<ProductInfoModel>();
                    try {
                        StringBuffer stringBuffer = new StringBuffer(body.string());
                        int indexStart = stringBuffer.indexOf("[");
                        int indexStop = stringBuffer.lastIndexOf("]");
                        String newJsonString = stringBuffer.substring(indexStart, indexStop + 1);
                        Log.d(ProductInfoTrillBit.class.getCanonicalName(), newJsonString);
                        JsonElement jsonElement = new JsonParser().parse(newJsonString);
                        Gson gson = new Gson();
                        productInfoModels = gson.fromJson(jsonElement, new TypeToken<List<ProductInfoModel>>() {
                        }.getType());
                        productListener.onProductReceived(productInfoModels);
                    } catch (JsonSyntaxException | IOException e) {
                        e.printStackTrace();
                        productListener.onProductError();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    productListener.onProductError();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            productListener.onProductError();
        }
    }
}
