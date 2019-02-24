package entertainment.rxandroidapp.dagger;

import android.app.Activity;
import android.os.Bundle;

import entertainment.rxandroidapp.R;

public class DaggerImplemtation extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dagger_implemtation);
        oneThread c1 = new oneThread();
        oneThread c2 = new oneThread();
        c1.start();

        try {
            c1.join(); // Waiting for c1 to finish
        }
        catch (InterruptedException ie) {
        }

        c2.start();

    }

    public class oneThread extends Thread {
        public void run() {
            System.out.println("geeks ");
            try {
                Thread.sleep(300);
            } catch (InterruptedException ie) {
            }
            System.out.println("forgeeks ");
        }
    }
}
