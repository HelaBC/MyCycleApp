package tn.rnu.isi.mycycle;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;


public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "cycle.db";

    public DBHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE cycle_entries(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date TEXT," +
                "flow TEXT," +
                "mood TEXT," +
                "symptoms TEXT," +
                "notes TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS cycle_entries");
        onCreate(db);
    }

    public boolean insertEntry(String date, String flow, String mood, String symptoms, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("flow", flow);
        values.put("mood", mood);
        values.put("symptoms", symptoms);
        values.put("notes", notes);
        long result = db.insert("cycle_entries", null, values);
        return result != -1;
    }
}
