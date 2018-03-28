package com.movies.chris.trendingmovies.data.tmdb.remote;

/**
 * Created by chris on 3/2/18.
 */
import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.movies.chris.trendingmovies.BuildConfig;

public class RetrofitClient {

    private static final String TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    private static final String PARAM_API_KEY = "api_key";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl){
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            HttpUrl newHttpUrl = original.url()
                                    .newBuilder()
                                    .addQueryParameter(PARAM_API_KEY, TMDB_API_KEY)
                                    .build();
                            return chain.proceed(original.newBuilder().url(newHttpUrl).build());

                        }
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
