package il.ac.huji.todolist.todolist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/***
 * This class defines the database of the todoItems
 */

public class DBHelper extends SQLiteOpenHelper {

    /* ================================================================================= */

    // Database name and fields
    public final static String DB_NAME              = "todo_db";
    public final static String TABLE_NAME           = "todo_items";
    public final static String KEY_COL_NAME         = "_id";
    public final static String TITLE_COL_NAME       = "title";
    public final static String DUE_DATE_COL_NAME    = "due";

    // Database queries
    private final static String DROP_STRING         = "drop table if exists " + TABLE_NAME + ";";
    private final static String CREATE_STRING       = "create table " + TABLE_NAME + " ( " +
            KEY_COL_NAME + " integer primary key autoincrement, " +
            TITLE_COL_NAME + " string, " +
            DUE_DATE_COL_NAME + " long);";

    /* ================================================================================= */

    // Constructor
    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    /* ================================================================================= */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STRING);
    }

    /* ================================================================================= */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL(DROP_STRING);
        onCreate(db);
    }

    /* ================================================================================= */

    /**
     * Returns a cursor for the items in the given database
     * @param db the database whose items should be returned
     * @return a cursor for the items in the given database
     */
    public Cursor getCursor(SQLiteDatabase db) {
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}