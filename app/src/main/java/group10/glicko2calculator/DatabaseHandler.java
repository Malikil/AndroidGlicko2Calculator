package group10.glicko2calculator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

class DatabaseHandler extends SQLiteOpenHelper
{
    private SQLiteDatabase db = getReadableDatabase();
    private static String DATABASE_NAME = "GlickoDB";
    private static int DATABASE_VERSION = 1;

    DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    Cursor getAllPlayers(String sortCol, boolean ascending)
    {
        String query = "SELECT * FROM Players " +
                "ORDER BY %s %s;";
        return db.rawQuery(
                String.format(query, sortCol, ascending ? "ASC" : "DESC"),
                null //new String[] { sortCol }
        );
    }

    Cursor getPlayer(String uID)
    {
        String query = "SELECT * FROM Players " +
                "WHERE uID = ?;";
        return db.rawQuery(query, new String[] { uID });
    }

    boolean playerExists(String uID)
    {
        Cursor result = db.rawQuery(
                "SELECT uID FROM Players " +
                        "WHERE uID = ?;",
                new String[] { uID });
        if (result != null && result.getCount() > 0)
        {
            result.close();
            return true;
        }
        else
            return false;
    }

    long addPlayer(String uID, float rating, float deviation, double volatility)
    {
        ContentValues cv = new ContentValues();
        cv.put("uID", uID);
        cv.put("rating", rating);
        cv.put("deviation", deviation);
        cv.put("volatility", volatility);

        return db.insert("Players", null, cv);
    }

    long addPlayerWithDefaults(String uID, Activity context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return addPlayer(
                uID,
                preferences.getFloat("Default Rating", 1500),
                preferences.getFloat("Default Deviation", 350),
                preferences.getFloat("Default Volatility", 0.06F)
        );
    }

    int updatePlayer(String uID, float rating, float deviation, float volatility)
    {
        ContentValues cv = new ContentValues();
        cv.put("rating", rating);
        cv.put("deviation", deviation);
        cv.put("volatility", volatility);

        return db.update("Players", cv, "uID = ?", new String[] { uID });
    }

    Cursor getGames()
    {
        String query = "SELECT * FROM Games;";
        return db.rawQuery(query, null);
    }

    Cursor getGames(String sortCol, boolean ascending)
    {
        String query = "SELECT * FROM Games " +
                "ORDER BY %s %s;";
        return db.rawQuery(
                String.format(query, sortCol, ascending ? "ASC" : "DESC"),
                null //new String[] { sortCol }
        );
    }

    long addGame(String winner, String loser, boolean isDraw)
    {
        ContentValues cv = new ContentValues();
        cv.put("winner", winner);
        cv.put("loser", loser);
        cv.put("isDraw", isDraw);

        return db.insert("Games", null, cv);
    }


    Cursor getGameByID(String gameID)
    {
        String query = "SELECT * FROM Games " +
                "WHERE gameID = ?;";
        Cursor c = db.rawQuery(query, new String[] { gameID });
        c.moveToFirst();
        return c;
    }

    int deleteGame(int gameID)
    {
        return db.delete(
                "Games",
                "gameID = ?",
                new String[] { Integer.toString(gameID) }
        );
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
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

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }
}
