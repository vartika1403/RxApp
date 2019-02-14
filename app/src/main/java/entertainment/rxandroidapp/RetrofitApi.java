package entertainment.rxandroidapp;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitApi {
    /** See https://developer.github.com/v3/users/ */
    String BASE_URL = "https://api.chucknorris.io/jokes/";

    @GET("/users/{user}")
    User getUser(@Path("user") String user);

    @GET("{path}")
    Observable<Jokes> getRandomJokes(@Path("path") String path);

}
