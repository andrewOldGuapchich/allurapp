package com.example.allur_app.api;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AllurApi {
    public Observable<String> request(){
        return Observable.create(emitter -> {
            OkHttpClient okHttpClient = new OkHttpClient();
            String answer = "";

            Request request = new Request.Builder()
                    .url("http://192.168.208.235:7071/allur/scanner/api/demo")
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()){
                if(response.isSuccessful()){
                    assert response.body() != null;
                    answer = response.body().string();
                    emitter.onNext(answer);
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
