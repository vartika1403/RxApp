package entertainment.rxandroidapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.work.Data;
import androidx.work.WorkInfo;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BlurActivity extends AppCompatActivity {
    private static final String LOG_TAG = BlurActivity.class.getSimpleName();

    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.go_button)
    Button goButton;
    @BindView(R.id.see_file_button)
    Button seeFileButton;
    @BindView(R.id.cancel_button)
    Button cancelButton;

    private BlurViewModel blurViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);
        ButterKnife.bind(this);
        blurViewModel = ViewModelProviders.of(this).get(BlurViewModel.class);
        Intent intent = getIntent();
        String imageUriExtra = intent.getStringExtra(Constants.KEY_IMAGE_URI);
        blurViewModel.setImageUri(imageUriExtra);
        if (blurViewModel.getImageUri() != null) {
            Glide.with(this).load(blurViewModel.getImageUri()).into(imageView);
        }

        blurViewModel.getOutputWorkInfo().observe(this, workInfos -> {
            if (workInfos == null || workInfos.isEmpty()) {
                return;
            }

            // We only care about the one output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            WorkInfo workInfo = workInfos.get(0);

            boolean finished = workInfo.getState().isFinished();
            if (!finished) {
                showWorkInProgress();
            } else {
                showWorkFinished();

                // Normally this processing, which is not directly related to drawing views on
                // screen would be in the ViewModel. For simplicity we are keeping it here.
                Data outputData = workInfo.getOutputData();

                String outputImageUri =
                        outputData.getString(Constants.KEY_IMAGE_URI);

                // If there is an output file show "See File" button
                if (!TextUtils.isEmpty(outputImageUri)) {
                    blurViewModel.setOutputUri(outputImageUri);
                    seeFileButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnClick(R.id.go_button)
    public void applyBlur() {
        blurViewModel.applyBlur(getBlurLevel());
    }

    @OnClick(R.id.see_file_button)
    public void viewOutputImage() {
        Uri currentUri = blurViewModel.getOutputUri();
        if (currentUri != null) {
            Intent actionView = new Intent(Intent.ACTION_VIEW, currentUri);
            if (actionView.resolveActivity(getPackageManager()) != null) {
                startActivity(actionView);
            }
        }
    }

    @OnClick(R.id.cancel_button)
    public void cancelWork() {
        blurViewModel.cancelWork();
    }

    private void showWorkInProgress() {
        progressBar.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        goButton.setVisibility(View.GONE);
        seeFileButton.setVisibility(View.GONE);
    }

    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private void showWorkFinished() {
        progressBar.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        goButton.setVisibility(View.VISIBLE);
    }

    /**
     * Get the blur level from the radio button as an integer
     * @return Integer representing the amount of times to blur the image
     */
    private int getBlurLevel() {
        RadioGroup radioGroup = findViewById(R.id.radio_blur_group);

        switch(radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_blur_lv_1:
                return 1;
            case R.id.radio_blur_lv_2:
                return 2;
            case R.id.radio_blur_lv_3:
                return 3;
        }

        return 1;
    }

}
