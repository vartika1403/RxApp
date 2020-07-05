package entertainment.rxandroidapp.dagger;



import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import java.util.stream.DoubleStream;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import entertainment.rxandroidapp.R;

public class DaggerActivity extends Activity {
    private static final String LOG_TAG = DaggerActivity.class.getSimpleName();
    @BindView(R.id.inUsername)
    EditText userName;
    @BindView(R.id.inNumber)
    EditText numberText;
    private MyComponent myComponent;
    @Inject
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dagger);
        ButterKnife.bind(this);
        myComponent = DaggerMyComponent.builder().sharedPrefModule(new SharedPrefModule(this)).build();
        myComponent.inject(this);
    }
}
