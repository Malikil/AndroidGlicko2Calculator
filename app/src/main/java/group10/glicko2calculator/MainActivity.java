package group10.glicko2calculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get all buttons
        final Button infoButton = findViewById(R.id.viewInfoButton),
                     playersButton = findViewById(R.id.viewPlayersButton),
                     gamesButton = findViewById(R.id.viewGamesButton),
                     calculatorButton = findViewById(R.id.systemInfoButton);

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Show legal information and a short description of the app.
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });
        playersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Go to the players activity, where the user can see all players in a table
                // and add more players if desired
                startActivity(new Intent(MainActivity.this, PlayersActivity.class));
            }
        });
        gamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Go to the games activity, where the user can see all current games in a
                // table, and add more games as they occur. Perhaps also include calculating new
                // ratings from the games screen
                startActivity(new Intent(MainActivity.this, GamesActivity.class));
            }
        });
        calculatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Go to a calculator settings activity, where the user can adjust the default
                // values for rating, rating deviation, and volatility, as well as adjust the system
                // tau
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
    }
}
