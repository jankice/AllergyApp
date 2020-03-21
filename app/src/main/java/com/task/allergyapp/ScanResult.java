package com.task.allergyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.task.allergyapp.data.BarcodeService;
import com.task.allergyapp.data.NetworkClient;
import com.task.allergyapp.data.model.ProductList;
import com.task.allergyapp.ui.AllergyCheck.scan.ScanFragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScanResult extends AppCompatActivity {

    private static final CharSequence BUTTON_POSITIVE = "OK" ;
    TextView scanResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        scanResult = findViewById(R.id.scanResult);

        String barcode = getIntent().getStringExtra(ScanFragment.BARCODE_VALUE);
        Log.d("FETCHING", barcode);


        fetchBarcodeDetails(barcode);
    }

    private void fetchBarcodeDetails(String barcode) {

        Retrofit retrofit = NetworkClient.getRetrofitClient();

        BarcodeService barcodeService = retrofit.create(BarcodeService.class);

        Call<ProductList> call = barcodeService.getProductListForBarcode(barcode,  getString(R.string.barcode_api_key));

        call.enqueue(new Callback<ProductList>() {
            @Override
            public void onResponse(Call<ProductList> call, Response<ProductList> response) {
                /*This is the success callback. Though the response type is JSON, with Retrofit we get the response in the form of WResponse POJO class
                 */
                if (response.body() != null) {
                    ProductList wResponse = response.body();
                    if(wResponse.getProducts().length >0){
                        String ingredient = wResponse.getProducts()[0].getIngredient();
                        Log.d("INGREDIENT", ingredient);

                        matchIngredient(ingredient);
                    }
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {

                showAlertDialog();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    private void showAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(
                ScanResult.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Alert Dialog");
        // Setting Dialog Message
        alertDialog.setMessage("Could not connect!! Try Again");
        // Showing Alert Message
        alertDialog.show();
    }

    public ArrayList<String> loadArray() {

        ArrayList<String> allergenList = new ArrayList<>();
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(this);
        int size = mSharedPreference1.getInt("Status_size", 0);
        for (int i = 0; i < size; i++) {
            allergenList.add(mSharedPreference1.getString("Status_" + i, null));
        }

        return allergenList;
    }
    private void matchIngredient(String theIngre) {
        ListView list = findViewById(R.id.list);

        ArrayList<String> tempList = new ArrayList<>();
        ArrayList<String> allergenListData = loadArray();

        if(allergenListData != null && allergenListData.size() > 0){

            for(String s: allergenListData){
                if(theIngre.toLowerCase().contains(s.toLowerCase())){
                        tempList.add(s);
                }

            }

        }

        scanResult.setText("No allergen found. :)");
        if(tempList.size() > 0){
            scanResult.setText("One or more allergen found!!:(");

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, tempList);
            list.setAdapter(arrayAdapter);

        }
    }
}
