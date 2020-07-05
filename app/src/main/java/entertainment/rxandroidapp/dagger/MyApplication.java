package entertainment.rxandroidapp.dagger;

import android.app.Application;

import java.util.stream.DoubleStream;

public class MyApplication extends Application {
    private MyComponent mMyComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mMyComponent = createMyComponent();
    }

    MyComponent getMyComponent() {
        return mMyComponent;
    }

    private MyComponent createMyComponent() {
        return DaggerMyComponent
                .builder()
                .myModule(new MyModule())
                .build();
    }
}
