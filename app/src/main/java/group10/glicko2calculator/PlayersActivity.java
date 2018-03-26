package group10.glicko2calculator;

import android.content.Intent;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load players from database into adapter
        ((ListView)findViewById(R.id.playerList)).setAdapter(
                new PlayersAdapter(this, DatabaseHandler.getPlayers())
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            ((PlayersAdapter)
                    ((ListView)findViewById(R.id.playerList)).getAdapter()
            ).updatePlayers(DatabaseHandler.getPlayers());
            Log.i("Activity Result", "Updated Players");
        }
    }
}
