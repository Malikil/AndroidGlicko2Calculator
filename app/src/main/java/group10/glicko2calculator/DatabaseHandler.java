package group10.glicko2calculator;
import android.app.Activity;
import android.content.ContentValues;
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
        String query = "SELECT * FROM Players " +
                "ORDER BY uID ASC;";
        return db.rawQuery(query, null);
    }

    static boolean playerExists(String uID)
    {
        Cursor result = db.rawQuery(
                "SELECT uID FROM Players " +
                        "WHERE uID = '" + uID + "';",
                null);
        if (result != null && result.getCount() > 0)
        {
            result.close();
            return true;
        }
        else
            return false;
    }

    static long addPlayer(String uID, float rating, float deviation, double volatility)
    {
        ContentValues cv = new ContentValues();
        cv.put("uID", uID);
        cv.put("rating", rating);
        cv.put("deviation", deviation);
        cv.put("volatility", volatility);

        return db.insert("Players", null, cv);
    }

    static Cursor getGames()
    {
        String query = "SELECT * FROM Games;";
        return db.rawQuery(query, null);
    }

    static long addGame(String winner, String loser, boolean isDraw)
    {
        ContentValues cv = new ContentValues();
        cv.put("winner", winner);
        cv.put("loser", loser);
        cv.put("isDraw", isDraw);

        return db.insert("Games", null, cv);
    }
}
