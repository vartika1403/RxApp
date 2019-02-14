package entertainment.rxandroidapp.worker;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileNotFoundException;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import entertainment.rxandroidapp.Constants;

public class BlurWorker extends Worker {
    private static final String LOG_TAG = BlurWorker.class.getSimpleName();

    public BlurWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();

        // Makes a notification when the work starts and slows down the work so that it's easier to
        // see each WorkRequest start, even on emulated devices
        WorkUtils.makeStatusNotification("Blurring image", applicationContext);
        WorkUtils.sleep();

        String resourceUri = getInputData().getString(Constants.KEY_IMAGE_URI);
        try {
            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(LOG_TAG, "Invalid input uri");
                throw new IllegalArgumentException("Invalid input uri");
            }

            ContentResolver resolver = applicationContext.getContentResolver();

            // Create a bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)));

            // Blur the bitmap
            Bitmap output = WorkUtils.blurBitmap(bitmap, applicationContext);

            // Write bitmap to a temp file
            Uri outputUri = WorkUtils.writeBitmapToFile(applicationContext, output);

            // Return the output for the temp file
            Data outputData = new Data.Builder().putString(
                    Constants.KEY_IMAGE_URI, outputUri.toString()).build();

            // If there were no errors, return SUCCESS
            return Result.success(outputData);
        } catch (FileNotFoundException fileNotFoundException) {
            Log.e(LOG_TAG, "Failed to decode input stream", fileNotFoundException);
            throw new RuntimeException("Failed to decode input stream", fileNotFoundException);

        } catch (Throwable throwable) {

            // If there were errors, return FAILURE
            Log.e(LOG_TAG, "Error applying blur", throwable);
            return Result.failure();
        }
    }
}
