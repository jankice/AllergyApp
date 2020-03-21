package com.task.allergyapp.data;

import com.task.allergyapp.data.model.ProductList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BarcodeService {

    @GET("/v2/products")
    Call<ProductList> getProductListForBarcode(@Query("barcode") String barcode, @Query("key") String apiKey);
}
