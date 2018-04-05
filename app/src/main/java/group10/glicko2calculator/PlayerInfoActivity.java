package group10.glicko2calculator;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

public class PlayerInfoActivity extends AppCompatActivity
{
    private final int DOUBLE_EQUALITY = 2;
    private float initRating, initDeviation;
    private double initVolatility;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        putInToolbar(toolbar);

        final String pname = this.getIntent().getStringExtra("Player Name");
        final DatabaseHandler db = new DatabaseHandler(this);
        initValues(db, pname);

        // Set button listeners
        findViewById(R.id.makeGameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent addGame = new Intent(PlayerInfoActivity.this, AddGameActivity.class);
                addGame.putExtra("Init Player", pname);
                startActivityForResult(addGame, 0);
            }
        });
        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Get values from EditText
                EditText ratingEntry = findViewById(R.id.ratingEntry),
                        deviationEntry = findViewById(R.id.deviationEntry),
                        volatilityEntry = findViewById(R.id.volatilityEntry);
                float newRating = Float.parseFloat(ratingEntry.getText().toString()),
                        newDeviation = Float.parseFloat(deviationEntry.getText().toString());
                double newVolatility = Double.parseDouble(volatilityEntry.getText().toString());
                // Check if values are different
                ContentValues cv = new ContentValues();
                if (Math.abs(initRating - newRating) > Float.MIN_NORMAL)
                    cv.put("rating", newRating);
                if (Math.abs(initDeviation - newDeviation) > Float.MIN_NORMAL)
                    cv.put("deviation", newDeviation);
                if (Math.abs(initVolatility - newVolatility) > Double.MIN_NORMAL * DOUBLE_EQUALITY)
                    cv.put("volatility", newVolatility);
                // Push values to database
                if (cv.size() > 0)
                {
                    db.updatePlayer(pname, cv);
                    Toast.makeText(
                            PlayerInfoActivity.this,
                            "Saved changes.",
                            Toast.LENGTH_SHORT
                    ).show();
                    setResult(RESULT_OK);
                }
                else
                    Toast.makeText(
                            PlayerInfoActivity.this,
                            "No changes to save.",
                            Toast.LENGTH_SHORT
                    ).show();
            }
        });
        findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String deleteMessage = "Do you really want to delete %s and all of their games?\nThere is no undo.";
                new AlertDialog.Builder(PlayerInfoActivity.this)
                        .setTitle("Delete Player?")
                        .setMessage(String.format(deleteMessage, pname))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                db.deletePlayer(pname);
                                setResult(RESULT_OK);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    private void initValues(DatabaseHandler db, String playerName)
    {
        Cursor playerObj = db.getPlayerWithGameCount(playerName);
        playerObj.moveToFirst();

        initRating = playerObj.getFloat(1);
        initDeviation = playerObj.getFloat(2);
        initVolatility = playerObj.getDouble(3);
        int won = playerObj.getInt(4),
                total = playerObj.getInt(5);
        playerObj.close();
        EditText ratingEntry = findViewById(R.id.ratingEntry),
                deviationEntry = findViewById(R.id.deviationEntry),
                volatilityEntry = findViewById(R.id.volatilityEntry);
        TextView nameDisplay = findViewById(R.id.playerName),
                gamesWonDisplay = findViewById(R.id.wonGamesDisplay),
                gamesTotalDisplay = findViewById(R.id.totalGamesDisplay),
                winRateDisplay = findViewById(R.id.winRateDisplay);

        nameDisplay.setText(playerName);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            // Get player name from label
            String pname = ((TextView)findViewById(R.id.playerName)).getText().toString();
            initValues(new DatabaseHandler(PlayerInfoActivity.this), pname);
            setResult(RESULT_OK);
        }
    }

    private void putInToolbar(Toolbar toolbar) {
        Drawable drawable = getResources().getDrawable(R.drawable.arrow);
        drawable = resize(drawable, 64, 64);
        toolbar.setNavigationIcon(drawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private Drawable resize(Drawable image, int width, int height) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width, height, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }
}
