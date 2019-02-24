package entertainment.rxandroidapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import java.lang.ref.WeakReference;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNoteActivity extends AppCompatActivity {
    private static final String LOG_TAG = AddNoteActivity.class.getSimpleName();
    private NoteDatabase noteDatabase;
    private NoteDetail noteDetail;
    private boolean update;

    @BindView(R.id.et_title)
    TextInputEditText editTitleText;
    @BindView(R.id.et_content)
    TextInputEditText editContentText;
    @BindView(R.id.but_save)
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        ButterKnife.bind(this);

        noteDatabase = NoteDatabase.getInstance(this);
        if ( (noteDetail = (NoteDetail) getIntent().getSerializableExtra("note"))!=null ){
            getSupportActionBar().setTitle("Update Note");
            update = true;
            saveButton.setText("Update");
            editTitleText.setText(noteDetail.getTitle());
            editContentText.setText(noteDetail.getContent());
        }

    }

    @OnClick(R.id.but_save)
    public void saveDetail() {
        if (update){
            noteDetail.setContent(editContentText.getText().toString());
            noteDetail.setTitle(editTitleText.getText().toString());
            noteDatabase.getNoteDao().update(noteDetail);
            setResult(noteDetail,2);
        }else {
            noteDetail = new NoteDetail(editContentText.getText().toString(), editTitleText.getText().toString());
            new InsertTask(AddNoteActivity.this,noteDetail).execute();
        }
    }

    private static class InsertTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<AddNoteActivity> activityReference;
        private NoteDetail noteDetail;

        InsertTask(AddNoteActivity context, NoteDetail noteDetail) {
            activityReference = new WeakReference<>(context);
            this.noteDetail = noteDetail;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            // retrieve auto incremented note id
            int j = (int) activityReference.get().noteDatabase.getNoteDao().insert(noteDetail);
            noteDetail.setNote_id(j);
            Log.i("ID ", "doInBackground: "+j );
            return true;
        }

        // onPostExecute runs on main thread
        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool){
                activityReference.get().setResult(noteDetail,1);
                activityReference.get().finish();
            }
        }
    }

    private void setResult(NoteDetail note, int flag){
        setResult(flag,new Intent().putExtra("noteDetail",noteDetail));
        finish();
    }
}
