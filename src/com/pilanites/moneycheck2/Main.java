package com.pilanites.moneycheck2;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class Main extends Activity {
	
	EditText amount_edittext,spent_on_edittext, add_new_group;
	Button saveButton;
	String money;
	Spinner groups;
	Spendings newSpending, groupSpendings;
	Toast legal, illegal;
	static boolean Kill = false;
	static long qTime = 0;
	ArrayAdapter<String> a;
	ListView deleteGroupList;
	Builder deleteGroupBuilder;
	AlertDialog listToBeDismissed;
	SharedPreferences pref;
	static int selectedGroup = 0;
	int click;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        amount_edittext = (EditText) findViewById(R.id.amount_edit_text);
        spent_on_edittext = (EditText) findViewById(R.id.spent_on_edit_text);
        groups = (Spinner) findViewById(R.id.spinner1);
        
        pref = getSharedPreferences("MONCHK", 0);
        
        amount_edittext.setText(pref.getString("Amount", ""));
        spent_on_edittext.setText(pref.getString("Stuff", ""));
        
        if((System.currentTimeMillis() - Main.qTime) > 500) Kill = false;
        
        groupSpendings = new Spendings(this);
        
        populateSpin(false);
        
        /** Create the adView
        AdView adView = new AdView(this, AdSize.BANNER, "a14e04f979335ad");
        // Lookup your LinearLayout assuming it’s been given
        // the attribute android:id="@+id/mainLayout"
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout2);
        // Add the adView to it
        layout.addView(adView);
        // Initiate a generic request to load it with an ad
        adView.loadAd(new AdRequest());
        */
    }
    
    /* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId() == R.id.addnewgroup){	
			Builder d = new AlertDialog.Builder(this);
			d.setTitle(getResources().getString(R.string.addnewgp));
			d.setView(getNewGroupView());
			d.setPositiveButton(getResources().getString(R.string.save),
					new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						groupSpendings.open();
						groupSpendings.addNewGroup(add_new_group.getText().toString());
						groupSpendings.close();
						add_new_group.setText("");
						groupSpendings.open();
						a = new ArrayAdapter<String>(Main.this, 
				        		android.R.layout.simple_spinner_item, 
				        		groupSpendings.getGroups());
				        a.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
						groups.setAdapter(a);
						groupSpendings.close();
						groups.setSelection(0);
				}
			});
			d.show();
		} else if (item.getItemId() == R.id.savemenumain){
			saveTable(add_new_group);
		} else if (item.getItemId() == R.id.detailsfrommenu) {
			details(add_new_group);
		} else if (item.getItemId() == R.id.deletegroup) {
			deleteGroupBuilder = new AlertDialog.Builder(Main.this);
			deleteGroupBuilder.setTitle(getResources().getString(R.string.deletegroup));
			deleteGroupBoxPopulate();
			deleteGroupList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					click = arg2;
					Builder b = new AlertDialog.Builder(Main.this);
					b.setTitle("Are you sure you want to delete?");
					b.setPositiveButton("Yes", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							listToBeDismissed.dismiss();
							groupSpendings.open();
							groupSpendings.deleteGroup(click);
							groupSpendings.close();
							deleteGroupBoxPopulate();
							populateSpin(true);
							listToBeDismissed = deleteGroupBuilder.create();
							listToBeDismissed.show();
						}
					});
					b.setNegativeButton("No", null);
					b.show();
				}
			});
			listToBeDismissed = deleteGroupBuilder.create();
			listToBeDismissed.show();
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void deleteGroupBoxPopulate() {
		groupSpendings.open();
		deleteGroupList = new ListView(this);
		ArrayAdapter<String> a = new ArrayAdapter<String>(Main.this, 
				android.R.layout.simple_dropdown_item_1line, 
				groupSpendings.getGroupsWithoutNone());
		deleteGroupList.setAdapter(a);
		deleteGroupBuilder.setView(deleteGroupList);
		groupSpendings.close();
		deleteGroupList.setCacheColorHint(Color.TRANSPARENT);
	}
	
	public void populateSpin(boolean deleted) {
		groupSpendings.open();
        a = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, 
        		groupSpendings.getGroups());
        a.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        groups.setAdapter(a);
        groupSpendings.close();
        
        if(!deleted){
        	groups.setSelection(pref.getInt("Group", 0));
        	selectedGroup = pref.getInt("Group", 0);
        } else {
        	groups.setSelection(0);
        	selectedGroup = 0;
        }
        groups.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				selectedGroup = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public View getNewGroupView() {
		add_new_group = new EditText(this);
		add_new_group.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		add_new_group.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		return add_new_group;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(Kill) Main.this.finish();
		super.onResume();
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		SharedPreferences defs = getSharedPreferences("MONCHK", 0);
		SharedPreferences.Editor editor = defs.edit();
		editor.putString("Amount", amount_edittext.getText().toString());
		editor.putString("Stuff", spent_on_edittext.getText().toString());
		editor.putInt("Group", groups.getSelectedItemPosition());
		editor.commit();
		super.onStop();
	}
	
	public void saveTable(View v){
		newSpending = new Spendings(Main.this);
		try{
			if(amount_edittext.getText().length()==0){
				illegal = Toast.makeText(Main.this, R.string.illegal_entry_amount, 1500);
				illegal.show();
				return;
			}
			if(spent_on_edittext.getText().length()==0){
				illegal = Toast.makeText(Main.this, R.string.illegal_entry_spenton, 1500);
				illegal.show();
				return;
			}
			newSpending.open();
			newSpending.addSpending(spent_on_edittext.getText().toString(), 
					Float.valueOf(amount_edittext.getText().toString()));
			Log.d("MainAct", "Selected  position = "+groups.getSelectedItemPosition());
			newSpending.close();
			spent_on_edittext.setText(null);
			amount_edittext.setText(null);
			Log.d("MainAct", "Ahoy there.");
			legal.show();
		} catch (Exception e){
			illegal = Toast.makeText(Main.this, R.string.legal_entry, 1500);
			illegal.show();
		}
	}
	
	
	public void details(View v){
		Intent intent = new Intent(Main.this, Details.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public void info(View v){
		Intent intent = new Intent(Main.this, Info.class);
		intent.putExtra("Starter", 0);
		startActivity(intent);
	}
	
	public void quit(View v) {
		Details.Kill = true;
		Info.Kill = true;
		Main.qTime = System.currentTimeMillis();
		Main.this.finish();
	}
	
	public void send(View v){
/*		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    	emailIntent.setType("plain/text");
    	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{emailID.getText().toString()});
    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Money Check");
    	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "You've spent " + amount.getText().toString()+" on "+stuff.getText().toString());
    	Main.this.startActivity(Intent.createChooser(emailIntent, "Money Check"));
 */
		Intent intent = new Intent (Main.this, Details.class);
		startActivity(intent);
	}
}