package entertainment.rxandroidapp;



import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class NoteDetail  implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int note_id;

    @ColumnInfo(name = "note_content") // column name will be "note_content" instead of "content" in table
    private String content;

    private String title;

    public NoteDetail(int note_id, String content, String title) {
        this.note_id = note_id;
        this.content = content;
        this.title = title;
    }

    public NoteDetail(String toString, String toString1) {
        content = toString;
        title = toString1;
    }

    public int getNote_id() {
        return note_id;
    }

    public void setNote_id(int note_id) {
        this.note_id = note_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NoteDetail)) return false;

        NoteDetail note = (NoteDetail) o;

        if (note_id != note.note_id) return false;
        return title != null ? title.equals(note.title) : note.title == null;
    }

    @Override
    public int hashCode() {
        int result = note_id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Note{" +
                "note_id=" + note_id +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
