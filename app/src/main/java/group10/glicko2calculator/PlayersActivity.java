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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PlayersActivity extends AppCompatActivity
{
    private String sort;
    private boolean asc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);
        //Create a toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Put back button on toolbar
        putInToolbar(toolbar);
        //Get database
        DatabaseHandler db = new DatabaseHandler(this);
        if (savedInstanceState != null)
        {
            sort = savedInstanceState.getString("Current Sort", "uID");
            asc = savedInstanceState.getBoolean("Sort Type", true);
        }
        else { sort = "uID"; asc = true; }

        // Load players from database into adapter
        final ListView playerList = findViewById(R.id.playerList);
        playerList.setAdapter(new PlayersAdapter(this, db.getAllPlayers(sort, asc)));
        playerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent infoIntent = new Intent(PlayersActivity.this, PlayerInfoActivity.class);
                // Need to find which player was clicked
                String player = playerList.getAdapter().getItem(i).toString();
                infoIntent.putExtra("Player Name", player);
                startActivityForResult(infoIntent, 0);
            }
        });

        // Action listeners for sorting on column headers
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
                        1
                );
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        //Save
        outState.putString("Current Sort", sort);
        outState.putBoolean("Sort Type", asc);
        super.onSaveInstanceState(outState);
    }

    private void putInToolbar(Toolbar toolbar) {
        //Set toolbar back icon
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
        //Get drawable bitmap scaled
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
            updatePlayerList();
            if (requestCode == 1)
                Toast.makeText(this, "Added Player", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePlayerList()
    {
        ((PlayersAdapter)
                ((ListView)findViewById(R.id.playerList)).getAdapter()
        ).updatePlayers(new DatabaseHandler(this).getAllPlayers(sort, asc));
    }
}
