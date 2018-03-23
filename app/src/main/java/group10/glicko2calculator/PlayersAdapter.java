package group10.glicko2calculator;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.goochjs.glicko2.Rating;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class PlayersAdapter extends BaseAdapter
{
    private Activity context;
    private ArrayList<Rating> list;

    public PlayersAdapter(Activity context, ArrayList<Rating> list)
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
    public Object getItem(int i)
    {
        return list.get(i);
    }

    @Override
    public long getItemId(int i)
    {
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

        Rating player = list.get(i);
        ((TextView)view.findViewById(R.id.column1)).setText(player.getUid());
        ((TextView)view.findViewById(R.id.column2)).setText(Double.toString(player.getRating()));
        ((TextView)view.findViewById(R.id.column3)).setText(Double.toString(player.getRatingDeviation()));

        return view;
    }
}
