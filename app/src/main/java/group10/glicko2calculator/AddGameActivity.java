package group10.glicko2calculator;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.UserDictionary;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.goochjs.glicko2.Rating;
import org.goochjs.glicko2.RatingCalculator;
import org.goochjs.glicko2.RatingPeriodResults;

import java.util.Locale;

public class AddGameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);
        final DatabaseHandler db = new DatabaseHandler(this);

        // TODO Set keyboard suggestions to existing players here

        ((Button)findViewById(R.id.addGameButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String winner = ((EditText)findViewById(R.id.player1Entry)).getText().toString().trim(),
                        loser = ((EditText)findViewById(R.id.player2Entry)).getText().toString().trim();
                boolean draw = ((CheckBox)findViewById(R.id.drawCheck)).isChecked();

                // Check if the players are in the database
                if (!db.playerExists(winner))
                {
                    if (!db.playerExists(loser))
                        askForPlayer(winner, loser); // Neither winner nor loser exist
                    else
                        askForPlayer(winner, null); // Only winner doesn't exist
                }
                else if (!db.playerExists(loser))
                    askForPlayer(loser, null); // Only loser doesn't exist
                else if (db.addGame(winner, loser, draw) == -1)
                    Toast.makeText(
                            AddGameActivity.this,
                            "Failed to add game.\nHave you added the same player twice?",
                            Toast.LENGTH_SHORT).show();
                else
                {
                    // TODO For now, add games to the calculator here
                    // Create player objects using database information
                    Cursor player = db.getPlayer(winner);
                    player.moveToFirst();
                    Rating rWinner = new Rating(
                            winner,
                            null,
                            player.getFloat(1),
                            player.getFloat(2),
                            player.getFloat(3)
                    );

                    player = db.getPlayer(loser);
                    player.moveToFirst();
                    Rating rLoser = new Rating(
                            loser,
                            null,
                            player.getFloat(1),
                            player.getFloat(2),
                            player.getFloat(3)
                    );

                    // Create a result set
                    RatingPeriodResults results = new RatingPeriodResults();
                    if (draw)
                        results.addDraw(rWinner, rLoser);
                    else
                        results.addResult(rWinner, rLoser);

                    // Calculate new ratings
                    SharedPreferences preferences =
                            PreferenceManager.getDefaultSharedPreferences(AddGameActivity.this);
                    RatingCalculator calculator = new RatingCalculator(
                            preferences.getFloat("System Tau", 0.75F),
                            preferences.getFloat("Default Volatility", 0.06F)
                    );
                    calculator.updateRatings(results);

                    // Update database with new results
                    db.updatePlayer(
                            winner,
                            (float)rWinner.getRating(),
                            (float)rWinner.getRatingDeviation(),
                            (float)rWinner.getVolatility()
                    );

                    db.updatePlayer(
                            loser,
                            (float)rLoser.getRating(),
                            (float)rLoser.getRatingDeviation(),
                            (float)rLoser.getVolatility()
                    );

                    setResult(RESULT_OK);
                    finish();
                }
            }

            /**
             * Method in add game button click listener to make adding players to the database on
             * the fly easier
             * @param uID User ID
             * @param uID2 Second user ID, NULL if only one player to add
             */
            private void askForPlayer(final String uID, final String uID2)
            {
                new AlertDialog.Builder(AddGameActivity.this)
                        .setTitle("Create Player")
                        .setMessage(String.format(
                                Locale.getDefault(),
                                "There is no player named %s, would you like to create them?",
                                uID
                        ))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if (new DatabaseHandler(AddGameActivity.this)
                                        .addPlayerWithDefaults(uID, AddGameActivity.this) == -1)
                                    Toast.makeText(
                                            AddGameActivity.this,
                                            String.format(
                                                    Locale.getDefault(),
                                                    "Could not create an entry for %s. Please" +
                                                            " try adding them through the players" +
                                                            "screen, then create this game again.",
                                                    uID
                                            ),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                if (uID2 != null)
                                    askForPlayer(uID2, null);
                                else
                                    ((Button)findViewById(R.id.addGameButton)).performClick();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        ((Button)findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
