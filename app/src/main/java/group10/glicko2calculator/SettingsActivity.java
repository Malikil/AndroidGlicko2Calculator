package group10.glicko2calculator;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.goochjs.glicko2.RatingCalculator;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        final double tau = Double.longBitsToDouble(preferences.getLong(
                "System Tau",
                Double.doubleToLongBits(0.75)
        ));
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

        final EditText tauText = findViewById(R.id.tauEntry),
                       ratingText = findViewById(R.id.defRatingEntry),
                       deviationText = findViewById(R.id.defDeviationEntry),
                       volatilityText = findViewById(R.id.defVolatilityEntry);

        tauText.setText(Double.toString(tau));
        ratingText.setText(Double.toString(rating));
        deviationText.setText(Double.toString(deviation));
        volatilityText.setText(Double.toString(volatility));

        ((Button)findViewById(R.id.saveButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try
                {
                    double newTau = Double.parseDouble(tauText.getText().toString()),
                            newRating = Double.parseDouble(ratingText.getText().toString()),
                            newDeviation = Double.parseDouble(deviationText.getText().toString()),
                            newVolatility = Double.parseDouble(volatilityText.getText().toString());

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong("System Tau", Double.doubleToLongBits(newTau));
                    editor.putLong("Default Rating", Double.doubleToLongBits(newRating));
                    editor.putLong("Default Deviation", Double.doubleToLongBits(newDeviation));
                    editor.putLong("Default Volatility", Double.doubleToLongBits(newVolatility));
                    editor.commit();
                    finish();
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
                ratingText.setText("1500");
                deviationText.setText("350");
                volatilityText.setText("0.06");
            }
        });
    }
}
