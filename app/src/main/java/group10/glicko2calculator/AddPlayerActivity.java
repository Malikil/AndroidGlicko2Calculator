package group10.glicko2calculator;

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

public class AddPlayerActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        final EditText pName = findViewById(R.id.nameEntry),
                    ratingEntry = findViewById(R.id.ratingEntry),
                    deviationEntry = findViewById(R.id.deviationEntry),
                    volatilityEntry = findViewById(R.id.volatilityEntry);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        float defaultRating = preferences.getFloat("Default Rating", 1500),
                defaultDeviation = preferences.getFloat("Default Deviation", 350),
                defaultVolatility = preferences.getFloat("Default Volatility", 0.06F);

        ratingEntry.setHint(Float.toString(defaultRating));
        deviationEntry.setHint(Float.toString(defaultDeviation));
        volatilityEntry.setHint(Float.toString(defaultVolatility));

        ((Button)findViewById(R.id.saveButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // TODO Add new player to database
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
