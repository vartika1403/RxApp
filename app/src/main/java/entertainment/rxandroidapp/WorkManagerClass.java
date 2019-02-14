package entertainment.rxandroidapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkManagerClass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_manager);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.start_work_manager)
    public void startRequest() {
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(MyWorker.class).build();
        WorkManager.getInstance().enqueue(oneTimeWorkRequest);
    }
}
