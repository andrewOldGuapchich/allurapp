package com.example.allur_app.api;

import android.util.Log;

import com.example.allur_app.model.BoxInformEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AllurApi {
    public Observable<List<BoxInformEntity>> request(String id){
        long requestId;
        if(id.toCharArray()[0] == 'Y')
            requestId = Long.parseLong(id.substring(1));
        else
            requestId = Long.parseLong(id);

        return Observable.create(emitter -> {
            OkHttpClient okHttpClient = new OkHttpClient();
            String answer = "";

            Request request = new Request.Builder()
                    .url("http://192.168.210.254:7078/v1/box/inform/" + requestId)
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()){
                if(response.isSuccessful()){
                    assert response.body() != null;
                    answer = response.body().string();

                    ObjectMapper boxObjectMapper = new ObjectMapper();
                    List<BoxInformEntity> resultList = boxObjectMapper.readValue(answer, new TypeReference<List<BoxInformEntity>>() {});
                    emitter.onNext(resultList);
                    emitter.onComplete();
                } else {
                    emitter.onError(new Exception("error!"));
                }
            } catch (Exception e){
                e.getMessage();
            }
        });
    }
}
