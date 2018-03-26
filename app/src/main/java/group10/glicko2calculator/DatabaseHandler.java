package group10.glicko2calculator;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Andreja on 2018-03-22.
 */

class DatabaseHandler {

    private static SQLiteDatabase db;
    //static Activity context;

    static void openDB(Activity context, String name)
    {
        db = context.openOrCreateDatabase(name, Activity.MODE_PRIVATE, null);
    }

    static Cursor getPlayers()
    {
        String query = "SELECT uID, rating, deviation FROM Players;";
        return db.rawQuery(query, null);
    }


}
