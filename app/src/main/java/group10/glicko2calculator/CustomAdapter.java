package group10.glicko2calculator;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class CustomAdapter extends BaseAdapter
{
    private Activity context;
    private ArrayList<String> col1;
    private ArrayList col2;
    private ArrayList col3;

    public CustomAdapter(Activity context, ArrayList<String> col1, ArrayList col2, ArrayList col3)
    {
        this.context = context;
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
    }

    @Override
    public int getCount()
    {
        return col1.size();
    }

    @Override
    public Object getItem(int i)
    {
        return new Object[] { col1.get(i), col2.get(i), col3.get(i) };
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

        ((TextView)view.findViewById(R.id.column1)).setText(col1.get(i));
        ((TextView)view.findViewById(R.id.column2)).setText(col2.get(i).toString());
        ((TextView)view.findViewById(R.id.column3)).setText(col3.get(i).toString());

        return view;
    }
}
