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

        db.execSQL("CREATE TABLE IF NOT EXISTS Players (" +
                "uID VARCHAR(32) PRIMARY KEY, " +
                "rating REAL NOT NULL, " +
                "deviation REAL NOT NULL," +
                "volatility FLOAT NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS Games (" +
                "gameID INTEGER PRIMARY KEY, " +
                "winner VARCHAR(32) NOT NULL, " +
                "loser VARCHAR(32) NOT NULL, " +
                "isDraw INTEGER NOT NULL,\n" +
                "FOREIGN KEY (winner) REFERENCES Players (uID),\n" +
                "FOREIGN KEY (loser) REFERENCES Players (uID));");
    }

    static Cursor getPlayers()
    {
        String query = "SELECT uID, rating, deviation FROM Players;";
        return db.rawQuery(query, null);
    }

    static Cursor getGames()
    {
        String query = "SELECT * FROM Games;";
        return db.rawQuery(query, null);
    }
}
