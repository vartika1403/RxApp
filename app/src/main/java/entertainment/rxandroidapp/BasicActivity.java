package entertainment.rxandroidapp;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import entertainment.rxandroidapp.viewpackage.Circle;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static entertainment.rxandroidapp.RetrofitApi.BASE_URL;

public class BasicActivity extends Activity implements View{
    private static final String LOG_TAG = BasicActivity.class.getSimpleName();
    @BindView(R.id.circle_view)
    public Circle circleView;
    private RetrofitApi retrofitApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        ButterKnife.bind(this);

       // setUpOkhttpAndRetrofit();
    }

    @OnClick(R.id.swap_color_button)
    public void swapColor() {
        circleView.swapColor();
    }
    private void setUpOkhttpAndRetrofit() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        File httpCacheDirectory = new File(getCacheDir(), "offlineCache");

        //10 MB
        Cache cache = new Cache(httpCacheDirectory, 10*1024*1024);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cache(cache)
                                        .addInterceptor(httpLoggingInterceptor)
                                        .addNetworkInterceptor(provideCacheInterceptor())
                                        .addInterceptor(provideOfflineCacheInterceptor())
                                        .build();

        Retrofit retrofit = new Retrofit.Builder()
                               .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                               .addConverterFactory(GsonConverterFactory.create(new Gson()))
                               .client(okHttpClient)
                               .baseUrl(BASE_URL)
                               .build();

        retrofitApi =  retrofit.create(RetrofitApi.class);

    }

    private Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                String cacheControl = response.header("Cache-Control");
                if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                        cacheControl.contains("must-revalidate") || cacheControl.contains("max-stale=0")) {
                    return response.newBuilder()
                            .removeHeader("cache")
                            .header("Cache-Control", "public max-age=" + 5000)
                            .build();
                } else {
                    return response;
                }
            }
        };
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private Interceptor provideOfflineCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!isNetworkAvailable()) {
                    request= request.newBuilder()
                            .removeHeader("cache")
                            .header("Cache-Control", "public, only-if-cached")
                            .build();
                }
                return chain.proceed(request);
            }
        };
    }

   /* @OnClick(R.id.get_jokes_button)
    public void getJokes() {
        getRandomJokesFromApi();
    }
*/
    private void getRandomJokesFromApi() {
        io.reactivex.Observable<Jokes> jokesObservable = retrofitApi.getRandomJokes("random");
        jokesObservable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                 .map(new Function<Jokes, String>() {
                     @Override
                     public String apply(Jokes jokes) throws Exception {
                         return jokes.value;
                     }
                 }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.i(LOG_TAG, "string s , " + s);
            }

            @Override
            public void onError(Throwable e) {
               Log.e(LOG_TAG, "error, " + e.getMessage());
                Toast.makeText(getApplicationContext(), "An error occurred in the Retrofit request. Perhaps no response/cache",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }

}
