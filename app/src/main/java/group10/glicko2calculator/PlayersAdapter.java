package group10.glicko2calculator;

import android.app.Activity;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.goochjs.glicko2.Rating;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.zip.Inflater;

public class PlayersAdapter extends BaseAdapter
{
    private Activity context;
    private Cursor cursor;

    public PlayersAdapter(Activity context, Cursor dbCursor)
    {
        this.context = context;
        cursor = dbCursor;
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
        cursor.moveToPosition(i);

        // Use a number formatter to display numbers
        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        ((TextView)view.findViewById(R.id.column1)).setText(cursor.getString(0));
        ((TextView)view.findViewById(R.id.column2)).setText(String.format("%.0f", cursor.getFloat(1)));
        ((TextView)view.findViewById(R.id.column3)).setText(String.format("%.1f", cursor.getFloat(2))); // TODO

        return view;
    }

    void updatePlayers(Cursor cursor)
    {
        if (this.cursor != null)
            this.cursor.close();
        if (cursor != null)
            this.cursor = cursor;
        notifyDataSetChanged();
    }
}
