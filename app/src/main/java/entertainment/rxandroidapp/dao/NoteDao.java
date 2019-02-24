package entertainment.rxandroidapp.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import entertainment.rxandroidapp.Constants;
import entertainment.rxandroidapp.NoteDetail;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM  "+ Constants.TABLE_NAME_NOTE)
    List<NoteDetail> getAll();


    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    long insert(NoteDetail note);

    /*
     * update the object in database
     * @param note, object to be updated
     */
    @Update
    void update(NoteDetail repos);

    /*
     * delete the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(NoteDetail note);

}
