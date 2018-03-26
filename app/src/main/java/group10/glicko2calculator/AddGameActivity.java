package group10.glicko2calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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
                    Toast.makeText(AddGameActivity.this, Boolean.toString(draw), Toast.LENGTH_SHORT).show();
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
