package group10.glicko2calculator;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;

import java.util.List;

public class PlayersActivity extends AppCompatActivity
{
    private String sort;
    private boolean asc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        putInToolbar(toolbar);

        DatabaseHandler db = new DatabaseHandler(this);
        if (savedInstanceState != null)
        {
            sort = savedInstanceState.getString("Current Sort", "uID");
            asc = savedInstanceState.getBoolean("Sort Type", true);
        }
        else { sort = "uID"; asc = true; }

        // Load players from database into adapter
        ((ListView)findViewById(R.id.playerList)).setAdapter(
                new PlayersAdapter(this, db.getAllPlayers(sort, asc))
        );

        // Action listeners for column headers
        findViewById(R.id.playerHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (sort.equals("uID"))
                    asc = !asc;
                else
                {
                    sort = "uID";
                    asc = true;
                }
                updatePlayerList();
            }
        });
        findViewById(R.id.ratingHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (sort.equals("rating"))
                    asc = !asc;
                else
                {
                    sort = "rating";
                    asc = false;
                }
                updatePlayerList();
            }
        });
        findViewById(R.id.deviationHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (sort.equals("deviation"))
                    asc = !asc;
                else
                {
                    sort = "deviation";
                    asc = true;
                }
                updatePlayerList();
            }
        });

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

    /*@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        sort = savedInstanceState.getString("Current Sort", "uID");
        asc = savedInstanceState.getBoolean("Sort Type", asc);

        asc = !asc;

        ((PlayersAdapter)
                ((ListView)findViewById(R.id.playerList)).getAdapter()
        ).updatePlayers(
                new DatabaseHandler(this).getAllPlayers(sort, asc)
        );

        super.onRestoreInstanceState(savedInstanceState);
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putString("Current Sort", sort);
        outState.putBoolean("Sort Type", asc);
        super.onSaveInstanceState(outState);
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
            updatePlayerList();
    }

    private void updatePlayerList()
    {
        ((PlayersAdapter)
                ((ListView)findViewById(R.id.playerList)).getAdapter()
        ).updatePlayers(new DatabaseHandler(this).getAllPlayers(sort, asc));
    }
}
