package group10.glicko2calculator;

import android.app.Activity;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.goochjs.glicko2.Result;

import java.util.ArrayList;

public class GamesAdapter extends BaseAdapter
{
    private Activity context;
    private Cursor cursor;
    private ArrayList<Result> list;

    public GamesAdapter(Activity context, ArrayList<Result> list)
    {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        if (view == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.list_adapter_layout, null);
        }

        Result game = list.get(i);
        ((TextView)view.findViewById(R.id.column1)).setText(game.getWinner().getUid());
        ((TextView)view.findViewById(R.id.column2)).setText(game.getLoser().getUid());
        boolean isDraw = game.getScore(game.getLoser()) > 0;
        ((TextView)view.findViewById(R.id.column3)).setText(isDraw
                            ? game.getWinner().getUid()
                            : "Draw");

        return view;
    }
}
