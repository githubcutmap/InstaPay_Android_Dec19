package gramtarang.mint.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import gramtarang.mint.R;



/*Adapter_Ministatement
* we have added a list view in ministatement_recipt screen so this is the adapter for that list view*/
public class Adapter_Ministatement extends ArrayAdapter {

    private final Context context;
    private final String[] date;
    private final String[] type;
    private final String[] amount;
    String amounts;
//the data we need to show in the list view
public Adapter_Ministatement(Context context, String[] date, String[] type, String[] amount) {
        super(context,-1,date);
        this.context = context;
        this.date = date;
        this.type = type;
        this.amount = amount;
    }

    //we have attached the list view items in the listview
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView =layoutInflater.inflate(R.layout.list_item_ministatement,parent,false);
        TextView textView_date=(TextView)rowView.findViewById(R.id.text_date);
        TextView textView_type=(TextView)rowView.findViewById(R.id.text_type);
        TextView textView_amount=(TextView)rowView.findViewById(R.id.text_amount);
        textView_date.setText(date[position]);
        textView_type.setText(type[position]);
        amounts=amount[position];
        double double_amount = Double.parseDouble(amounts);
        amounts=String.format("%,.2f", double_amount);

        textView_amount.setText(amounts);
        return rowView;

    }

    //getItems from the pogo class
    @Nullable
    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    //getting the id of the items which is in the list view
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
