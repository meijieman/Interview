package com.major.interview.api;

import java.io.IOException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @desc: TODO
 * @author: Major
 * @since: 2018/4/6 22:08
 */
public class ApiFactory {

    public static final String USER_AGENT =
            "Mozilla/5.0  (iPhone;  CPU  iPhone  OS  10_3  like Mac  OS  X)  AppleWebKit/603.1.30  (KHTML,  like  Gecko) Version/10.3 Mobile/14E277 Safari/603.1.30";

    private static ApiFactory sApiFactory;

    public static ApiFactory getInstance() {
        if (sApiFactory == null) {
            synchronized (ApiFactory.class) {
                if (sApiFactory == null) {
                    sApiFactory = new ApiFactory();
                }
            }
        }
        return sApiFactory;
    }

    private Retrofit mRetrofit;

    private ApiFactory() {
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
                .baseUrl("https://github.com")
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    public ApiService getApiService() {
        return mRetrofit.create(ApiService.class);
    }

    public interface ApiService {

        @GET
        Observable<String> request(@Url String url);

    }
}
