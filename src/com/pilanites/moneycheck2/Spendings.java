package com.pilanites.moneycheck2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Spendings {

	private static final String DATABASE_NAME = "Expenses";
	private static final int DATABASE_VERSION = 1;
	private static final String SPENDINGS_TABLE_NAME = "ExpensesTable";
	private static final String SPENT_ON = "SpentOn";
	private static final String AMOUNT = "Amount";
	private static final String TIME = "Time";
	private static final String GROUP = "GroupText";
	private static final String SPENDINGS_ID = "_id";
	private static final String GROUPS_TABLE_NAME = "GroupsTable";
	private static final String GROUP_NAME = "GroupName";
	private static final String GROUP_ID = "Group_id";
	
	private static final String[] COLUMNS_SPENDINGS = {SPENDINGS_ID, SPENT_ON, AMOUNT, TIME, GROUP};
	private static final String[] COLUMNS_GROUPS = {GROUP_ID, GROUP_NAME};
	
	
	private Context ourContext;
	private DbHelper ourDatabase;
	private SQLiteDatabase ourDb;
	private Cursor spendingsCursor;
	private Cursor groupsCursor;
	private int genUse;
	private ContentValues values;
	private long tempTime2;
	private long tempTime3;
	
	public Spendings (Context c){
		ourContext = c;
	}
	
	public void open(){
		ourDatabase = new DbHelper(ourContext);
		ourDb = ourDatabase.getWritableDatabase();
		spendingsCursor = ourDb.query(SPENDINGS_TABLE_NAME, COLUMNS_SPENDINGS, null, 
				null, null, null, null);
		groupsCursor = ourDb.query(GROUPS_TABLE_NAME, COLUMNS_GROUPS, null, 
				null, null, null, null);
	}
	
	public void addNewGroup(String name){
		values = new ContentValues();
		values.put(GROUP_NAME, name);
		ourDb.insertOrThrow(GROUPS_TABLE_NAME, null, values);
		values.clear();
	}
	
	public String[] getGroups(){
		String[] g = new String[groupsCursor.getCount()+1];
		g[groupsCursor.getCount()] = "None";
		int i = 0;
		if(groupsCursor.getCount() == 0) return g;
		groupsCursor.moveToLast();
		do{
			g[i] = groupsCursor.getString(groupsCursor.getColumnIndex(GROUP_NAME));
			i++;
		} while(groupsCursor.moveToPrevious());
		return g;
	}
	
	public String[] getGroupsWithoutNone() {
		String[] g = new String[groupsCursor.getCount()];
		int i = 0;
		if(groupsCursor.getCount() == 0) return g;
		groupsCursor.moveToLast();
		do{
			g[i] = groupsCursor.getString(groupsCursor.getColumnIndex(GROUP_NAME));
			i++;
		} while(groupsCursor.moveToPrevious());
		return g;
	}
	
	public String[] getGroupsWithAll() {
		String[] g = new String[groupsCursor.getCount()+2];
		g[groupsCursor.getCount()] = "None";
		g[groupsCursor.getCount()+1] = "All";
		int i = 0;
		if(groupsCursor.getCount() == 0) return g;
		groupsCursor.moveToLast();
		do{
			g[i] = groupsCursor.getString(groupsCursor.getColumnIndex(GROUP_NAME));
			i++;
		} while(groupsCursor.moveToPrevious());
		return g;
	}
	
	public void addSpending(String spentOn, float amount) {
		values = new ContentValues();
		values.put(SPENT_ON, spentOn);
		values.put(AMOUNT, amount);
		values.put(GROUP, getId(Main.selectedGroup));
		values.put(TIME, System.currentTimeMillis());
		ourDb.insertOrThrow(SPENDINGS_TABLE_NAME, null, values);
		spendingsCursor.moveToLast();
		values.clear();	
	}
	
	public int totalGroups(){
		return groupsCursor.getCount();
	}
	
	public float sumGroup(int id){
		initializeCursor(id);
		if (spendingsCursor.getCount() == 0) {
			
			return 0;
		}
		float sum = 0;
		spendingsCursor.moveToFirst();
		do{
			sum += spendingsCursor.getFloat(spendingsCursor.getColumnIndex(AMOUNT));
		} while(spendingsCursor.moveToNext());
		return sum;
	}
	
	public int getGroup(int itemId) {
		spendingsCursor = ourDb.query(SPENDINGS_TABLE_NAME, COLUMNS_SPENDINGS, SPENDINGS_ID + " = " + itemId, null, null, null, null);
		spendingsCursor.moveToFirst();
		if (spendingsCursor.getInt(spendingsCursor.getColumnIndex(GROUP)) == -1) {
			return groupsCursor.getCount();
		} else {
			genUse = 0;
			if (groupsCursor.getCount() == 0) return 0;
			groupsCursor.moveToLast();
			while (groupsCursor.getInt(groupsCursor.getColumnIndex(GROUP_ID)) != spendingsCursor.getInt(spendingsCursor.getColumnIndex(GROUP))) {
				genUse++;
				groupsCursor.moveToPrevious();
			}
			return genUse;
		}
	}
	
	public String getStringForMail () {
		StringBuilder i = new StringBuilder("Below are the expenses I wish to share.\n");
		String k[] = getGroupsWithAll();
		i.append("These spendings were put under the group " + k[Details.currentGroupId] + "\n");
		i.append("\n\t\t\tTotal: " + sumGroup(Details.currentGroupId));
		initializeCursor(Details.currentGroupId);
		i.append("\n"+"\t"+"Spent On"+"\t"+"\t"+"\t"+"Amount"+"\t"+"\t"+"\t"+"Date");
		if(spendingsCursor.getCount() < 1) i.append("\n\n\n\nSorry, No records found!");
		else {
			spendingsCursor.moveToLast();
			genUse = spendingsCursor.getCount();
			do {
				i.append("\n"+"\t"+ spendingsCursor.getString(spendingsCursor.getColumnIndex(SPENT_ON)) + 
						"\t"+"\t" + spendingsCursor.getFloat(spendingsCursor.getColumnIndex(AMOUNT))+ 
						"\t"+"\t" + DateClass.getDate(spendingsCursor.getLong(spendingsCursor.getColumnIndex(TIME))));
			} while (spendingsCursor.moveToPrevious());
		}
		i.append("\n\nThe mail was sent using the Android App Money Check by Pilanites.\nCheckout the app at https://market.android.com/details?id=com.pilanites.moneycheck2");
		i.append("\n Thanks!");
		return i.toString();
	}
	
	public String getGroupName(int id) {
		
		return null;
	}
	
	public void initializeCursor (int id) {
		spendingsCursor.close();
		if (groupsCursor.getCount() == 0) {
			spendingsCursor = ourDb.query(SPENDINGS_TABLE_NAME, COLUMNS_SPENDINGS,
					null, null, null, null, null);
			return;
		}
		switch(Details.currentDuration){
			case Details.ALL:
				tempTime2 = 0;
			break;
			case Details.MONTH:
				tempTime2 = DateClass.getMonth();
			break;
			case Details.WEEK:
				tempTime2 = DateClass.getBefore(-6);
				
			break;
			case Details.YESTERDAY:
				tempTime2 = DateClass.getBefore(-1);
				tempTime3 = DateClass.getYesterday();
				if (id == groupsCursor.getCount() + 1)
					spendingsCursor = ourDb.query(SPENDINGS_TABLE_NAME, COLUMNS_SPENDINGS,
							TIME + " > " + tempTime2 + " AND " + TIME + " < " + tempTime3, null, null, null, null);
				else {
					spendingsCursor = ourDb.query(SPENDINGS_TABLE_NAME, COLUMNS_SPENDINGS,
							GROUP + " = " + getId(id) + " AND " + TIME + " > " + tempTime2 + " AND " + TIME + " < " + tempTime3,
							null, null, null, null);
				}
				return;
			case Details.TODAY:
				tempTime2 = DateClass.getToday();
			break;
		}
		if (id == groupsCursor.getCount() + 1)
			spendingsCursor = ourDb.query(SPENDINGS_TABLE_NAME, COLUMNS_SPENDINGS,
					TIME + " > " + tempTime2, null, null, null, null);
		else {
			spendingsCursor = ourDb.query(SPENDINGS_TABLE_NAME, COLUMNS_SPENDINGS,
					GROUP + " = " + getId(id) + " AND " + TIME + " > " + tempTime2,
					null, null, null, null);
		}
	}
	
	public int getId(int id) {
		int a;
		if(id == groupsCursor.getCount()) a = -1;
		else if (id == groupsCursor.getCount() + 1) a = -2;
		else {
			int b = id;
			groupsCursor.moveToLast();
			while(b>0) {
				b--;
				groupsCursor.moveToPrevious();
			}
			a = groupsCursor.getInt(groupsCursor.getColumnIndexOrThrow(GROUP_ID));
		}
		return a;
	}
	
	public void deleteGroup(int id) {
		ourDb.delete(GROUPS_TABLE_NAME, GROUP_ID + " = " + getId(id), null);
		spendingsCursor.close();
		spendingsCursor = ourDb.query(SPENDINGS_TABLE_NAME, COLUMNS_SPENDINGS, 
				GROUP + " = " + getId(id),
				null, null, null, null);
		if (spendingsCursor.getCount() == 0) return;
		values = new ContentValues();
		values.put(GROUP, -1);
		ourDb.update(SPENDINGS_TABLE_NAME, values, 
				GROUP + " = " +getId(id), null);
	}
	
	public void changeGroup (int itemID, int id) {
		values = new ContentValues();
		values.put(GROUP, getId(id));
		ourDb.update(SPENDINGS_TABLE_NAME, values, 
				SPENDINGS_ID + " = " + itemID, null);
	}
	
	public void populateRecords(ListView lv, int id){
		initializeCursor(id);
		int count;
		if((count =spendingsCursor.getCount()) == 0) {
			lv.setAdapter(new ArrayAdapter<String>(ourContext, android.R.layout.simple_list_item_1));
			return;
		}
		int position = 0;
		String[] list_names = new String[count];
		float[] list_amount = new float[count];
		int[] list_id = new int[count];
		long[] list_time = new long[count];
		spendingsCursor.moveToLast();
		do{
			list_names[position] = spendingsCursor.getString(spendingsCursor.getColumnIndex(SPENT_ON));
			list_amount[position] = spendingsCursor.getFloat(spendingsCursor.getColumnIndex(AMOUNT));
			list_id[position] = spendingsCursor.getInt(spendingsCursor.getColumnIndex(SPENDINGS_ID));
			list_time[position] = spendingsCursor.getLong(spendingsCursor.getColumnIndex(TIME));
			position++;
		}while(spendingsCursor.moveToPrevious());
		lv.setAdapter(new MyAdapter<String> (ourContext, 
				android.R.layout.simple_list_item_1, list_names, list_amount, list_id, list_time));
	}
	
	public void delete(int id) {
		ourDb.delete(SPENDINGS_TABLE_NAME, SPENDINGS_ID+" = "+id, null);
	}
	
	public void close() {
		spendingsCursor.close();
		groupsCursor.close();
		ourDb.close();
		ourDatabase.close();
	}
	
	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE IF NOT EXISTS "+ SPENDINGS_TABLE_NAME 
					+ " (" + SPENDINGS_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ SPENT_ON + " VARCHAR, " + AMOUNT + " FLOAT(15), "
					+ TIME + " LONG(20), " + GROUP +" INTEGER(3));");
			db.execSQL("CREATE TABLE IF NOT EXISTS " + GROUPS_TABLE_NAME 
					+ " (" + GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ GROUP_NAME + " VARCHAR);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + SPENDINGS_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + GROUPS_TABLE_NAME);
			onCreate(db);
		}
		
	}
	
}
