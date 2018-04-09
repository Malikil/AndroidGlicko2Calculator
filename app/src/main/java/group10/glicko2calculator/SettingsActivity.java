package group10.glicko2calculator;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.goochjs.glicko2.RatingCalculator;

import java.util.Locale;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity
{
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //Get shared preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final float tau = preferences.getFloat("System Tau", 0.75F);
        final double rating = Double.longBitsToDouble(preferences.getLong(
                "Default Rating",
                Double.doubleToLongBits(1500))
        );
        final double deviation = Double.longBitsToDouble(preferences.getLong(
                "Default Deviation",
                Double.doubleToLongBits(350))
        );
        final double volatility = Double.longBitsToDouble(preferences.getLong(
                "Default Volatility",
                Double.doubleToLongBits(0.06))
        );
        //Get edittexts
        final EditText tauText = findViewById(R.id.tauEntry),
                       ratingText = findViewById(R.id.defRatingEntry),
                       deviationText = findViewById(R.id.defDeviationEntry),
                       volatilityText = findViewById(R.id.defVolatilityEntry);
        //Set edittexts programatically
        tauText.setText(Float.toString(tau));
        ratingText.setText(Double.toString(rating));
        deviationText.setText(Double.toString(deviation));
        volatilityText.setText(Double.toString(volatility));
        //Make save button save
        ((Button)findViewById(R.id.saveButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try
                {
                    final float newTau = Float.parseFloat(tauText.getText().toString());
                    final double newRating = Double.parseDouble(ratingText.getText().toString()),
                            newDeviation = Double.parseDouble(deviationText.getText().toString()),
                            newVolatility = Double.parseDouble(volatilityText.getText().toString());

                    // Input validation
                    if (newTau < 0.3 || newTau > 1.2)
                        new AlertDialog.Builder(SettingsActivity.this)
                                .setTitle("System Tau")
                                .setMessage("Tau is a system constant which constrains changes in a " +
                                        "player's volatility after unexpected game outcomes. The " +
                                        "expected range of values goes from 0.3 to 1.2, with lower " +
                                        "numbers indicating less predictable results. Setting the " +
                                        "System's Tau to a number outside this range may result in " +
                                        "unexpected rating calculation results. Are you sure you want " +
                                        "to do this?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        putValues(newTau, newRating, newDeviation, newVolatility);
                                        finish();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        tauText.setText(Float.toString(tau));
                                    }
                                })
                        .show();
                    else
                    {
                        putValues(newTau, newRating, newDeviation, newVolatility);
                        finish();
                    }
                }
                catch (NumberFormatException nfe)
                {
                    Toast.makeText(
                            SettingsActivity.this,
                            "Please make sure the entered values are numbers.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
        ((Button)findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
        ((Button)findViewById(R.id.btnReset)).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                tauText.setText("0.75");
                ratingText.setText("1500.0");
                deviationText.setText("350.0");
                volatilityText.setText("0.06");
            }
        });
    }

    private void putValues(float tau, double rating, double deviation, double volatility)
    {
        //Save preferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("System Tau", tau);
        editor.putLong("Default Rating", Double.doubleToLongBits(rating));
        editor.putLong("Default Deviation", Double.doubleToLongBits(deviation));
        editor.putLong("Default Volatility", Double.doubleToLongBits(volatility));
        editor.commit();
    }
}
