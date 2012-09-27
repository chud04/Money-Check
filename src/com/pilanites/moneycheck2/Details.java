package com.pilanites.moneycheck2;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class Details extends Activity {

	TextView sum;
	Spinner groups;
	String summingLine;
	Spendings s; 
	ListView records;
	Button durationButton;
	View clickedView;
	AlertDialog.Builder dialogBuild;
	AlertDialog dialogBox;
	static int k = 0;
	public static boolean Kill = false;
	
	public static final int TODAY = 0;
	public static final int YESTERDAY = 1;
	public static final int WEEK = 2;
	public static final int MONTH = 3;
	public static final int ALL = 4;
	
	public static final String[] DURATIONS = {"Today", "Yesterday", "Week", "Month", "All"};
	
	public static int currentDuration;
	public static int currentGroupId;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(Kill) Details.this.finish();
		super.onResume();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
		
		sum = (TextView) findViewById(R.id.sum);
		groups = (Spinner) findViewById(R.id.group_selector);
		records = (ListView) findViewById(R.id.record_id);
		
		s = new Spendings(this);
		summingLine = getResources().getString(R.string.summing_line);
		
		currentDuration = ALL;

		s.open();
		populateGroups(groups);
		groups.setSelection(groups.getCount()-1);
		s.close();

		
		if((System.currentTimeMillis() - Main.qTime) > 500) Kill = false;
		
		records.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				clickedView = arg1;
				dialogBuild = new AlertDialog.Builder(Details.this);
				dialogBuild.setTitle("Delete/Change");
				dialogBuild.setMessage("Do you want to delete the spending or change group?");
				dialogBuild.setPositiveButton(R.string.delete, 
						new android.content.DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialogBuild = new AlertDialog.Builder(Details.this);
						dialogBuild.setTitle(getResources().getString(R.string.delete));
						dialogBuild.setMessage("Are you sure you want to delete?");
						dialogBuild.setPositiveButton(android.R.string.yes, new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								s.open();
								s.delete(clickedView.getId());
								populate();
								s.close();
								return;
							}
						});
						dialogBuild.setNegativeButton(android.R.string.no, null);
						dialogBuild.show();
						
						return;
					}
				});
				dialogBuild.setNegativeButton(R.string.change_group, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						s.open();
						dialogBuild = new AlertDialog.Builder(Details.this);
						dialogBuild.setTitle(R.string.change_group);
						dialogBuild.setSingleChoiceItems(s.getGroups(), s.getGroup(clickedView.getId()), new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								s.open();
								s.changeGroup(clickedView.getId(), which);
								populate();
								s.close();
								dialog.dismiss();
							}
						});
						dialogBuild.show();
						s.close();
					}
				});
				dialogBox = dialogBuild.create();
				dialogBox.show();
				return true;
			}
		});
		
		groups.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				s.open();
				populate();
				s.close();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	private void populateGroups(Spinner spin) {
		// TODO Auto-generated method stub
		ArrayAdapter<String> t = new ArrayAdapter<String>(Details.this,
				android.R.layout.simple_spinner_item,
				s.getGroupsWithAll());
		t.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spin.setAdapter(t);
		}

	void populate(){
		currentGroupId = groups.getSelectedItemPosition();
		sum.setText(summingLine +" "+ 
				Float.toString(s.sumGroup(currentGroupId)));
		s.populateRecords(records, currentGroupId);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.detailsmenu, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId() == R.id.email_menu){
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
	    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Money Check");
	    	s.open();
	    	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, s.getStringForMail());
	    	s.close();
	    	startActivity(Intent.createChooser(emailIntent, "Money Check"));
		}
		return true;
	}

	public void addNew(View v){
		Details.this.finish();
	}
	
	public void selectDuration(View v){
		Builder dur = new AlertDialog.Builder(Details.this);
		dur.setTitle(getResources().getString(R.string.duration));
		dur.setSingleChoiceItems(DURATIONS, currentDuration, 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						currentDuration = which;
						s.open();
						populate();
						s.close();
						dialog.dismiss();
					}
				});
		dur.setNegativeButton(getResources().getString(R.string.ok), null);
		dur.show();
	}
	
	public void info (View v){
		Intent intent = new Intent(Details.this, Info.class);
		intent.putExtra("Starter", 1);
		startActivity(intent);
	}
	
	public void quit (View v) {
		Main.Kill = true;
		Info.Kill = true;
		Main.qTime = System.currentTimeMillis();
		Details.this.finish();
	}
}
