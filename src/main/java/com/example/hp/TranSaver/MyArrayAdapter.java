package com.example.hp.TranSaver;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


class MyArrayAdapter<S> extends ArrayAdapter<String> {
    private String[] objects;
    Context context;

    MyArrayAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        textView.setText(objects[position]);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_icon);
        int[] colors = {Color.parseColor("#EF4B23"), Color.parseColor("#1CBCC3"), Color.parseColor("#2B445D"), Color.parseColor("#FFD45C")};
        imageView.setBackgroundColor(colors[position % colors.length]);

        return rowView;
    }
}
