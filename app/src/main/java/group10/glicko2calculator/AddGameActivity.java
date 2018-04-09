package group10.glicko2calculator;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.UserDictionary;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.goochjs.glicko2.Rating;
import org.goochjs.glicko2.RatingCalculator;
import org.goochjs.glicko2.RatingPeriodResults;

import java.util.ArrayList;
import java.util.Locale;

public class AddGameActivity extends AppCompatActivity {
    //Create a list of players
    ArrayList<String> players = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_game);
        //Create database handler
        final DatabaseHandler db = new DatabaseHandler(this);
        //Set data for autocomplete feilds
        setAutocompleteData(db);
        //Get AutoCompleteTextViews
        final AutoCompleteTextView editTxtPlayer1 = findViewById(R.id.player1Entry);
        final AutoCompleteTextView editTxtPlayer2 = findViewById(R.id.player2Entry);
        //Set Threshhold
        editTxtPlayer1.setThreshold(1);
        editTxtPlayer2.setThreshold(1);
        //Set an array adapter for Autocomplete
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item ,players);
        editTxtPlayer1.setAdapter(adapter);
        editTxtPlayer2.setAdapter(adapter);
        
        //Create button to swap names
        ImageButton flipButton = (ImageButton)findViewById(R.id.swapPlayersButton);
        flipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text1 = editTxtPlayer1.getText().toString();
                editTxtPlayer1.setText(editTxtPlayer2.getText());
                editTxtPlayer2.setText(text1);
            }
        });



        // Get the player from the intent, if present
        Intent intent = this.getIntent();
        String initPlayer = intent.getStringExtra("Init Player");
        if (initPlayer != null)
            ((EditText)findViewById(R.id.player1Entry)).setText(initPlayer);

        ((Button)findViewById(R.id.addGameButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String winner = ((EditText)findViewById(R.id.player1Entry)).getText().toString().trim(),
                        loser = ((EditText)findViewById(R.id.player2Entry)).getText().toString().trim();
                boolean draw = ((CheckBox)findViewById(R.id.drawCheck)).isChecked();

                // Make sure both player boxes have players selected
                if (winner.length() < 1 || loser.length() < 1)
                    Toast.makeText(
                            AddGameActivity.this,
                            "Please select both players.",
                            Toast.LENGTH_SHORT
                    ).show();
                else if(winner.length() > 32 || loser.length() > 32)
                    Toast.makeText(
                            AddGameActivity.this,
                            "Player names should be less than 32 characters.",
                            Toast.LENGTH_SHORT
                    ).show();
                else if(winner.equals(loser))
                    Toast.makeText(
                            AddGameActivity.this,
                            "Please select two different players.",
                            Toast.LENGTH_SHORT
                    ).show();
                // Check if the players are in the database
                else if (!db.playerExists(winner))
                {
                    if (!db.playerExists(loser))
                        askForPlayer(winner, loser); // Neither winner nor loser exist
                    else
                        askForPlayer(winner, null); // Only winner doesn't exist
                }
                else if (!db.playerExists(loser))
                    askForPlayer(loser, null); // Only loser doesn't exist
                else
                {
                    db.addGame(winner, loser, draw);
                    // Create player objects using database information
                    Cursor player = db.getPlayer(winner);
                    player.moveToFirst();
                    Rating rWinner = new Rating(
                            winner,
                            null,
                            player.getDouble(1),
                            player.getDouble(2),
                            player.getDouble(3)
                    );
                    player.close();

                    player = db.getPlayer(loser);
                    player.moveToFirst();
                    Rating rLoser = new Rating(
                            loser,
                            null,
                            player.getDouble(1),
                            player.getDouble(2),
                            player.getDouble(3)
                    );
                    player.close();

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
                            Double.longBitsToDouble(
                                    preferences.getLong(
                                            "Default Volatility",
                                            Double.doubleToLongBits(0.06)
                                    )
                            )
                    );
                    calculator.updateRatings(results);

                    // Update database with new results
                    db.updatePlayer(
                            winner,
                            rWinner.getRating(),
                            rWinner.getRatingDeviation(),
                            rWinner.getVolatility()
                    );

                    db.updatePlayer(
                            loser,
                            rLoser.getRating(),
                            rLoser.getRatingDeviation(),
                            rLoser.getVolatility()
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
                //Crete alert to ask whether to create player
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

    public void setAutocompleteData(DatabaseHandler db)
    {
        try {
            //Get cursor from DB
            Cursor playerList = db.getAllPlayers("uID", true);
            if (playerList != null) {
                playerList.moveToFirst();
                do {
                    //Add player to list for autocomplete
                    players.add(playerList.getString(0));
                } while (playerList.moveToNext());
                playerList.close();
            }
        }
        catch (Exception ex)
        {
            //Make toast if something went wrong
            Toast.makeText(this, "AutoComplete was not able to index players", Toast.LENGTH_SHORT).show();
        }
    }
}
