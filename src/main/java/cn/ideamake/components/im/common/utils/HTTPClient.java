package cn.ideamake.components.im.common.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

@Slf4j
public class HTTPClient {
    private static HTTPClient ourInstance = new HTTPClient();

    public static HTTPClient getInstance() {
        return ourInstance;
    }

    private OkHttpClient okHttpClient = new OkHttpClient();


    private HTTPClient() {
    }

    public String get(String url){
        Request request = new Request.Builder().get().url(url).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                log.debug("response.code()=="+response.code());
                log.debug("response.message()=="+response.message());
                log.debug("res=="+response.body().string());
                return response.body().toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
        return "error";
    }

//    public String post(String url,)



}
