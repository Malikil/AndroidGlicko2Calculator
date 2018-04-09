package group10.glicko2calculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The screen to add players to the database
 */
public class AddPlayerActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);
        //Get text views
        final EditText pName = findViewById(R.id.nameEntry),
                    ratingEntry = findViewById(R.id.ratingEntry),
                    deviationEntry = findViewById(R.id.deviationEntry),
                    volatilityEntry = findViewById(R.id.volatilityEntry);
        //Get shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final double defaultRating = Double.longBitsToDouble(preferences.getLong(
                "Default Rating",
                Double.doubleToLongBits(1500)));
        final double defaultDeviation = Double.longBitsToDouble(preferences.getLong(
                "Default Deviation",
                Double.doubleToLongBits(350))
        );
        final double defaultVolatility = Double.longBitsToDouble(preferences.getLong(
                "Default Volatility",
                Double.doubleToLongBits(0.06))
        );
        //Set hint for text views programmatically
        ratingEntry.setHint(Double.toString(defaultRating));
        deviationEntry.setHint(Double.toString(defaultDeviation));
        volatilityEntry.setHint(Double.toString(defaultVolatility));

        //Make save button save and cancel button cancel
        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String name = pName.getText().toString().trim();
                if (name.length() < 1 || name.length() > 32)
                {
                    Toast.makeText(
                            AddPlayerActivity.this,
                            "Please enter a player name between 1 and 32 characters.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                else
                {
                    // If the user hasn't entered values for rating, deviation, or volatility, use
                    // the default values
                    double rating, deviation, volatility;
                    if (!ratingEntry.getText().toString().isEmpty())
                        try
                        {
                            rating = Double.parseDouble(ratingEntry.getText().toString());
                        }
                        catch (NumberFormatException nfe)
                        {
                            Toast.makeText(
                                    AddPlayerActivity.this,
                                    "Please enter a valid decimal number for Rating.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }
                    else
                        rating = defaultRating;

                    if (!deviationEntry.getText().toString().isEmpty())
                        try
                        {
                            deviation = Double.parseDouble(deviationEntry.getText().toString());
                        }
                        catch (NumberFormatException nfe)
                        {
                            Toast.makeText(
                                    AddPlayerActivity.this,
                                    "Please enter a valid decimal number for Rating Deviation.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }
                    else
                        deviation = defaultDeviation;

                    if (!volatilityEntry.getText().toString().isEmpty())
                        try
                        {
                            volatility = Double.parseDouble(volatilityEntry.getText().toString());
                        }
                        catch (NumberFormatException nfe)
                        {
                            Toast.makeText(
                                    AddPlayerActivity.this,
                                    "Please enter a valid decimal number for Volatility.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }
                    else
                        volatility = defaultVolatility;

                    // Add the player
                    if (new DatabaseHandler(AddPlayerActivity.this)
                            .addPlayer(name, rating, deviation, volatility) == -1)
                        Toast.makeText(
                                AddPlayerActivity.this,
                                "Failed to add player.\nDoes this player already exist?",
                                Toast.LENGTH_SHORT
                        ).show();
                    else
                    {
                        setResult(RESULT_OK);
                        finish();
                    }
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
