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

    public GamesAdapter(Activity context, Cursor cursor)
    {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public int getCount()
    {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int i)
    {
        cursor.moveToPosition(i);
        return cursor.getString(0);
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
        cursor.moveToPosition(i);

        ((TextView)view.findViewById(R.id.column1)).setText(cursor.getString(1));
        ((TextView)view.findViewById(R.id.column2)).setText(cursor.getString(2));
        boolean isDraw = cursor.getInt(3) > 0;
        ((TextView)view.findViewById(R.id.column3)).setText(isDraw
                            ? ""
                            : "Draw");

        return view;
    }
}
