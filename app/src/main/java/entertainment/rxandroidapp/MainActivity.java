package entertainment.rxandroidapp;

import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public String url = "https://reqres.in/api/users/2";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Observer subscription;
    private RetrofitApi retrofitService;
    private int idValue;
    @BindView(R.id.start_async_task_button)
    Button startAsyncTaskButton;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.result_text)
    TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //observable
        Observable<String> animalObservable = getAnimalObservable();
        //observer
        DisposableObserver<String> animalObserver = getAnimalObserver();
        DisposableObserver<String> animalObserverAllCaps = getAnimalsAllCapsObserver();

        final String[] listFirst = {"A", "B", "C", "D"};
        final String[] listSecond = {"1", "2", "3"};

        final Observable<String> observableFirst = Observable.fromArray(listFirst);
        final Observable<String> observableSecond = Observable.fromArray(listSecond);

        Observable.concat(observableFirst, observableSecond).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.i(LOG_TAG, "concat string value, " + s);
                Toast.makeText(MainActivity.this, "Concat String value " + s,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, "error, " + e.getLocalizedMessage());
            }

            @Override
            public void onComplete() {

            }
        });



        compositeDisposable.add(getNotesObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).map(new Function<Note, Note>() {
                    @Override
                    public Note apply(Note note) throws Exception {
                         note.setNote(note.getNote().toUpperCase());
                         return note;
                    }
                }).subscribeWith(getNotesObserver()));
    }

    private Observable<Note> getNotesObservable() {
        final List<Note> notes = prepareNotes();

        return Observable.create(new ObservableOnSubscribe<Note>() {
            @Override
            public void subscribe(ObservableEmitter<Note> emitter) throws Exception {
                for (Note note : notes) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(note);
                    }
                }

                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }

    private List<Note> prepareNotes() {
        List<Note> notes = new ArrayList<>();
        notes.add(new Note(1, "buy tooth paste!"));
        notes.add(new Note(2, "call brother!"));
        notes.add(new Note(3, "watch narcos tonight!"));
        notes.add(new Note(4, "pay power bill!"));

        return notes;
    }

    public void getGistObservable(Gist gist) throws IOException {
        if (gist != null) {
            Observable.just(gist);
        }
    }

    public void callObserver(Gist gist) {
        if (gist == null) {
            Log.i(LOG_TAG, "gist observer," + gist);
        }

        Observable.just(gist).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new Observer<Gist>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Gist gist) {
                Log.i(LOG_TAG, "gist object, " + gist.id);
                idValue = gist.id;
                updateText(idValue);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(LOG_TAG, "error, " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
            });
    }

    public void updateText(int id) {
        resultText.setText(" " + id);
    }

    @Nullable
    private Gist getGist() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Gist gist;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(LOG_TAG, "response, " + response);
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Log.i(LOG_TAG, "response body, " + result);
                    try {
                        JSONObject json = new JSONObject(result);
                        JSONObject str_value = json.getJSONObject("data");
                        Log.i(LOG_TAG, "data " + str_value);
                        int id = str_value.getInt("id");
                        Log.i(LOG_TAG, "id, " + id);
                        Gist gist = new Gist(id);
                        Log.i(LOG_TAG, "gist, " + gist);
                        callObserver(gist);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return null;
    }

    @OnClick(R.id.start_async_task_button)
    public void startAsyncTask() {
        final OkHttpClient client = new OkHttpClient();
        retrofitService = RetrofitService.createGithubService("github_auth_token");
        String str = "ok";
        Observable<User>  observable = Observable.just(str).map(new Function<String, User>() {
            @Override
            public User apply(String s) throws Exception {
                return retrofitService.getUser(str);
            }
        });
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(User user) {
                   Log.i(LOG_TAG,"got user values, " + user.email + user.name);
            }

            @Override
            public void onError(Throwable e) {
               Log.e(LOG_TAG, "error, " +  e.getLocalizedMessage());
            }

            @Override
            public void onComplete() {
              Log.i(LOG_TAG, "onComplete");
            }
        });
    }

    private DisposableObserver<String> getAnimalsAllCapsObserver() {
        return new DisposableObserver<String>() {

            @Override
            public void onNext(String s) {
                Log.d(LOG_TAG, "Name: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(LOG_TAG, "All items are emitted!");
            }
        };
    }

    private DisposableObserver<String> getAnimalObserver() {
        return new DisposableObserver<String>() {

            @Override
            public void onNext(String s) {
                Log.d(LOG_TAG, "Name: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(LOG_TAG, "onError, " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(LOG_TAG, "All items are emitted");
            }
        };
    }

    private DisposableObserver<Note> getNotesObserver() {
        return new DisposableObserver<Note>() {

            @Override
            public void onNext(Note note) {
                Log.d(LOG_TAG, "Note: " + note.getNote());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(LOG_TAG, "All notes are emitted!");
            }
        };
    }

    private Observable<String> getAnimalObservable() {
        return Observable.fromArray(
                "Ant", "Ape",
                "Bat", "Bee", "Bear", "Butterfly",
                "Cat", "Crab", "Cod",
                "Dog", "Dove",
                "Fox", "Frog");
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
