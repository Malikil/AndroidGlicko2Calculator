package group10.glicko2calculator;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
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

public class AddGameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        // Set keyboard suggestions to existing players

        ((Button)findViewById(R.id.addGameButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String winner = ((EditText)findViewById(R.id.player1Entry)).getText().toString().trim(),
                        loser = ((EditText)findViewById(R.id.player2Entry)).getText().toString().trim();
                boolean draw = ((CheckBox)findViewById(R.id.drawCheck)).isChecked();

                // Check if the players are in the database
                if (DatabaseHandler.playerExists(winner))
                {
                    // TODO
                }
                else
                {
                    // Winner doesn't exist, ask to create

                    return;
                }

                if (DatabaseHandler.playerExists(loser))
                {
                    // TODO
                }
                else
                {
                    // Loser doesn't exist, ask to create

                    return;
                }

                if (DatabaseHandler.addGame(winner, loser, draw) == -1)
                    Toast.makeText(
                            AddGameActivity.this,
                            "Failed to add game.\nHave you added the same player twice?",
                            Toast.LENGTH_SHORT).show();
                else
                {
                    // TODO For now, add games to the calculator here
                    // Create player objects using database information
                    Cursor player = DatabaseHandler.getPlayers(winner);
                    player.moveToFirst();
                    Rating rWinner = new Rating(
                            winner,
                            null,
                            player.getFloat(1),
                            player.getFloat(2),
                            player.getFloat(3)
                    );

                    player = DatabaseHandler.getPlayers(loser);
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
                    DatabaseHandler.updatePlayer(
                            winner,
                            (float)rWinner.getRating(),
                            (float)rWinner.getRatingDeviation(),
                            (float)rWinner.getVolatility()
                    );

                    DatabaseHandler.updatePlayer(
                            loser,
                            (float)rLoser.getRating(),
                            (float)rLoser.getRatingDeviation(),
                            (float)rLoser.getVolatility()
                    );

                    setResult(RESULT_OK);
                    finish();
                }
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
