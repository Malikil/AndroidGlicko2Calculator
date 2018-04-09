package group10.glicko2calculator;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The list of all games
 */
public class GamesActivity extends AppCompatActivity
{
    private String sort;
    private boolean asc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        //Create a toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Put back button in toolbar
        putInToolbar(toolbar);
        //Get database
        final DatabaseHandler db = new DatabaseHandler(this);
        //Set list adapter
        ((ListView)findViewById(R.id.gamesList)).setAdapter(
                new GamesAdapter(this, db.getGames("gameID", true))
        );

        // If the table was sorted, restore the sort
        if (savedInstanceState != null)
        {
            sort = savedInstanceState.getString("Sort", "gameID");
            asc = savedInstanceState.getBoolean("Ascending", true);
        }
        else { sort = "gameID"; asc = true; }

        // Ask to delete games on click
        ((ListView)findViewById(R.id.gamesList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, final long l)
            {
                String deleteMessage = "Do you really want to delete the game between %s and %s?\n" +
                        "The players' ratings will not be affected.";
                Cursor c = db.getGameByID(Long.toString(l));
                String winner = c.getString(1);
                String loser = c.getString(2);
                c.close();
                new AlertDialog.Builder(GamesActivity.this)
                        .setTitle("Delete Game?")
                        .setMessage(String.format(deleteMessage, winner, loser))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                db.deleteGame((int)l);
                                updateGameList();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        // Action listeners for sorting on column headers
        findViewById(R.id.p1Header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (sort.equals("winner"))
                    asc = !asc;
                else
                {
                    sort = "winner";
                    asc = true;
                }
                updateGameList();
            }
        });
        findViewById(R.id.p2Header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (sort.equals("loser"))
                    asc = !asc;
                else
                {
                    sort = "loser";
                    asc = true;
                }
                updateGameList();
            }
        });
        findViewById(R.id.drawHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (sort.equals("isDraw"))
                    asc = !asc;
                else
                {
                    sort = "isDraw";
                    asc = true;
                }
                updateGameList();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivityForResult(
                        new Intent(GamesActivity.this, AddGameActivity.class),
                        0
                );
            }
        });
    }

    private void putInToolbar(Toolbar toolbar) {
        //Get arrow and resize
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
        //Resize using bitmap scaling
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width, height, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putString("Sort", sort);
        outState.putBoolean("Ascending", asc);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            updateGameList();
            Toast.makeText(this, "Added Game", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateGameList()
    {
        ((GamesAdapter)
                ((ListView)findViewById(R.id.gamesList)).getAdapter()
        ).updateGames(new DatabaseHandler(this).getGames(sort, asc));
    }
}
