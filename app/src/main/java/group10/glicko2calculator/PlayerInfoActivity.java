package group10.glicko2calculator;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

public class PlayerInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String pname = this.getIntent().getStringExtra("Player Name");

        Cursor playerObj = new DatabaseHandler(this).getPlayerWithGameCount(pname);
        playerObj.moveToFirst();

        float initRating = playerObj.getFloat(1),
                initDeviation = playerObj.getFloat(2);
        double initVolatility = playerObj.getDouble(3);
        int won = playerObj.getInt(4),
                total = playerObj.getInt(5);
        EditText ratingEntry = findViewById(R.id.ratingEntry),
                deviationEntry = findViewById(R.id.deviationEntry),
                volatilityEntry = findViewById(R.id.volatilityEntry);
        TextView playerName = findViewById(R.id.playerName),
                gamesWonDisplay = findViewById(R.id.wonGamesDisplay),
                gamesTotalDisplay = findViewById(R.id.totalGamesDisplay),
                winRateDisplay = findViewById(R.id.winRateDisplay);

        playerName.setText(pname);
        ratingEntry.setText(Float.toString(initRating));
        deviationEntry.setText(Float.toString(initDeviation));
        volatilityEntry.setText(Double.toString(initVolatility));

        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        gamesWonDisplay.setText(format.format(won));
        gamesTotalDisplay.setText(format.format(total));
        format = NumberFormat.getPercentInstance();
        float winRate = (float)won / total;
        winRateDisplay.setText(format.format(winRate));
    }

}
