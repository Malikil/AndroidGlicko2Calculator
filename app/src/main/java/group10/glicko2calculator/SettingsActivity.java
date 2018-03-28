package group10.glicko2calculator;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        final float tau = preferences.getFloat("System Tau", 0.75F),
                    rating = preferences.getFloat("Default Rating", 1500),
                    deviation = preferences.getFloat("Default Deviation", 350),
                    volatility = preferences.getFloat("Default Volatility", 0.06F);

        final EditText tauText = findViewById(R.id.tauEntry),
                       ratingText = findViewById(R.id.defRatingEntry),
                       deviationText = findViewById(R.id.defDeviationEntry),
                       volatilityText = findViewById(R.id.defVolatilityEntry);

        tauText.setText(Float.toString(tau));
        ratingText.setText(Float.toString(rating));
        deviationText.setText(Float.toString(deviation));
        volatilityText.setText(Float.toString(volatility));

        ((Button)findViewById(R.id.saveButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try
                {
                    float newTau = Float.parseFloat(tauText.getText().toString()),
                          newRating = Float.parseFloat(ratingText.getText().toString()),
                          newDeviation = Float.parseFloat(deviationText.getText().toString()),
                          newVolatility = Float.parseFloat(volatilityText.getText().toString());

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putFloat("System Tau", newTau);
                    editor.putFloat("Default Rating", newRating);
                    editor.putFloat("Default Deviation", newDeviation);
                    editor.putFloat("Default Volatility", newVolatility);
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
    }
}
