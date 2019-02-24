package entertainment.rxandroidapp.dagger;

import javax.inject.Singleton;

import dagger.Component;
import entertainment.rxandroidapp.MainActivity;

@Singleton
@Component(modules = {SharedPrefModule.class})
public interface MyComponent {
    void inject(MainActivity mainActivity);
}
