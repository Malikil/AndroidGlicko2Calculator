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
    private static int DATABASE_VERSION = 2;

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
                null
        );
    }

    Cursor getPlayer(String uID)
    {
        String query = "SELECT * FROM Players " +
                "WHERE uID = ?;";
        return db.rawQuery(query, new String[] { uID });
    }

    Cursor getPlayerWithGameCount(String uID)
    {
        String query = "SELECT Players.*, SUM(CASE WHEN winner = uID AND isDraw = 0 THEN 1 ELSE 0 END) AS gamesWon, " +
                "COUNT(gameID) AS totalGames " +
                "FROM Games, Players " +
                "WHERE (uID = winner OR uID = loser) " +
                "AND uID = ?" +
                "GROUP BY uID, rating, deviation, volatility;";
        Cursor results = db.rawQuery(query, new String[] { uID });
        if (results.getCount() == 0)
        {
            results.close();
            query = "SELECT Players.*, 0 AS gamesWon, 0 AS totalGames " +
                    "FROM Players " +
                    "WHERE uID = ?;";
            return db.rawQuery(query, new String[] { uID });
        }
        else
            return results;
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

    long addPlayer(String uID, double rating, double deviation, double volatility)
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
                Double.longBitsToDouble(
                        preferences.getLong(
                                "Default Rating",
                                Double.doubleToLongBits(1500)
                        )
                ),
                Double.longBitsToDouble(
                        preferences.getLong(
                                "Default Deviation",
                                Double.doubleToLongBits(350)
                        )
                ),
                Double.longBitsToDouble(
                        preferences.getLong(
                                "Default Volatility",
                                Double.doubleToLongBits(0.06)
                        )
                )
        );
    }

    int updatePlayer(String uID, double rating, double deviation, double volatility)
    {
        ContentValues cv = new ContentValues();
        cv.put("rating", rating);
        cv.put("deviation", deviation);
        cv.put("volatility", volatility);

        return updatePlayer(uID, cv);
    }

    int updatePlayer(String uID, ContentValues cv)
    {
        return db.update("Players", cv, "uID = ?", new String[] { uID });
    }

    int deletePlayer(String uID)
    {
        int deletedGames = db.delete("Games", "winner = ? OR loser = ?", new String[] { uID, uID });
        return db.delete("Players", "uID = ?", new String[] { uID }) + deletedGames;
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
                "volatility REAL NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS Games (" +
                "gameID INTEGER PRIMARY KEY, " +
                "winner VARCHAR(32) NOT NULL, " +
                "loser VARCHAR(32) NOT NULL, " +
                "isDraw INTEGER NOT NULL,\n" +
                "FOREIGN KEY (winner) REFERENCES Players (uID),\n" +
                "FOREIGN KEY (loser) REFERENCES Players (uID));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        db.execSQL("DROP TABLE IF EXISTS Games;");
        db.execSQL("DROP TABLE IF EXISTS Players;");
        onCreate(db);
    }
}
