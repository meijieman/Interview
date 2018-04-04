package com.major.interview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "tag_ma";
    public static final String USER_AGENT =
            "Mozilla/5.0  (iPhone;  CPU  iPhone  OS  10_3  like Mac  OS  X)  AppleWebKit/603.1.30  (KHTML,  like  Gecko) Version/10.3 Mobile/14E277 Safari/603.1.30";

    private Retrofit mRetrofit;
    private EditText mInput;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInput = findViewById(R.id.et_main_input);
        findViewById(R.id.btn_main_parse).setOnClickListener(this);

        // 自定义一个信任所有证书的TrustManager，添加SSLSocketFactory的时候要用到
        final X509TrustManager trustAllCert =
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                };
        final SSLSocketFactory sslSocketFactory = new SSLSocketFactoryCompat(trustAllCert);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                               .newBuilder()
                                               .addHeader("User-Agent", USER_AGENT)
                                               .build();
                        return chain.proceed(request);
                    }
                })
                .sslSocketFactory(sslSocketFactory, trustAllCert)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://github.com")
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_main_parse:
                ApiService apiService = mRetrofit.create(ApiService.class);
                String url = mInput.getText().toString();
                apiService.request(url)
                          .enqueue(new Callback<String>() {
                              @Override
                              public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                                  String body = response.body();
                                  Log.i(TAG, "onResponse: " + body);
                                  Toast.makeText(MainActivity.this, "onResponse " + body, Toast.LENGTH_LONG).show();
                              }

                              @Override
                              public void onFailure(Call<String> call, Throwable t) {
                                  Log.e(TAG, "onFailure: " + t);
                                  Toast.makeText(MainActivity.this, "onFailure " + t, Toast.LENGTH_LONG).show();
                              }
                          });
                break;
            default:
                break;
        }
    }


    interface ApiService {

        @GET
        Call<String> request(@Url String url);
    }
}
