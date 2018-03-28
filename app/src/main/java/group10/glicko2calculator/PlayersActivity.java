package group10.glicko2calculator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class PlayersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        putInToolbar(toolbar);

        DatabaseHandler db = new DatabaseHandler(this);

        // Load players from database into adapter
        ((ListView)findViewById(R.id.playerList)).setAdapter(
                new PlayersAdapter(this, db.getAllPlayers("uID", true))
        );

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivityForResult(
                        new Intent(PlayersActivity.this, AddPlayerActivity.class),
                        0
                );
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            ((PlayersAdapter)
                    ((ListView)findViewById(R.id.playerList)).getAdapter()
            ).updatePlayers(
                    new DatabaseHandler(this).getAllPlayers("uID", true)
            );
        }
    }
}
