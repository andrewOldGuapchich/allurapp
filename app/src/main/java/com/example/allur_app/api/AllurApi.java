package com.example.allur_app.api;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.allur_app.model.box.Box;
import com.example.allur_app.model.box.BoxInformEntity;
import com.example.allur_app.model.http.ApiResponse;
import com.example.allur_app.model.product.ProductInform;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AllurApi {
    public Observable<ApiResponse<?>> getBoxInform(String id){
        long requestId;
        if(id.toCharArray()[0] == 'Y')
            requestId = Long.parseLong(id.substring(1));
        else
            requestId = Long.parseLong(id);

        return Observable.create(emitter -> {
            OkHttpClient okHttpClient = new OkHttpClient();
            String answer = "";

            Request request = new Request.Builder()
                    .url("http://94.241.139.199:7078/v1/box/inform/" + requestId)
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()){
                if(response.isSuccessful()){
                    assert response.body() != null;
                    answer = response.body().string();

                    ObjectMapper boxObjectMapper = new ObjectMapper();
                    List<BoxInformEntity> resultList = boxObjectMapper.readValue(answer, new TypeReference<List<BoxInformEntity>>() {});
                    emitter.onNext(new ApiResponse<>(response.code(), resultList));
                    emitter.onComplete();
                } else {
                    emitter.onNext(new ApiResponse<>(response.code(), response.message()));
                }
            } catch (Exception e){
                e.getMessage();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Observable<ApiResponse<?>> getBox(String boxId){
        long requestId;
        if(boxId.toCharArray()[0] == 'Y')
            requestId = Long.parseLong(boxId.substring(1));
        else
            requestId = Long.parseLong(boxId);
        return Observable.create(emitter -> {
            OkHttpClient httpClient = new OkHttpClient();
            String answer = "";
            Request request = new Request.Builder()
                    .url("http://94.241.139.199:7078/v1/box/" + requestId)
                    .build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    answer = response.body().string();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Box box = objectMapper.readValue(answer, Box.class);
                    emitter.onNext(new ApiResponse<>(response.code(), box));
                    emitter.onComplete();
                } else {
                    emitter.onNext(new ApiResponse<>(response.code(), response.message()));
                    Log.d("400", response.message());
                }

            } catch (IOException e) {
                emitter.onError(e);
            }
        });
    }

    public Observable<ApiResponse<?>> getProductInform(String productId){
        return Observable.create(emitter -> {
            OkHttpClient httpClient = new OkHttpClient();
            String answer = "";
            Request request = new Request.Builder()
                    .url("http://94.241.139.199:7078/v1/product/" + productId)
                    .build();
            try(Response response = httpClient.newCall(request).execute()){
                if(response.isSuccessful()){
                    assert response.body() != null;
                    answer = response.body().string();
                    ObjectMapper objectMapper = new ObjectMapper();
                    ProductInform result = objectMapper.readValue(answer, ProductInform.class);
                    emitter.onNext(new ApiResponse<>(response.code(), result));
                    emitter.onComplete();
                } else{
                    emitter.onNext(new ApiResponse<>(response.code(), "Товар не найден!"));
                }
            } catch (IOException e){
                emitter.onError(e);
            }
        });
    }

}
