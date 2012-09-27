package com.pilanites.moneycheck2;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyAdapter<T> extends ArrayAdapter<String> {

	Context context;
	String[] list_names;
	float[] list_amount;
	int[] list_id;
	long[] list_time;
	
	public MyAdapter(Context context, int textViewResourceId, 
			String[] objects, float[] amount, int[] id, long[] time) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.list_names = objects;
		this.list_amount = amount;
		this.list_id = id;
		this.list_time = time;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.tryer, parent, false);
		
		TextView t1 = (TextView) row.findViewById(R.id.format_1);
		TextView t2 = (TextView) row.findViewById(R.id.format_2);
		TextView t3 = (TextView) row.findViewById(R.id.format_3);
		
		row.setId(list_id[position]);
		t1.setText(list_names[position]);
		t2.setText(Float.toString(list_amount[position]));
		t3.setText(DateClass.getDate(list_time[position]));
		return row;
	}

	

}
