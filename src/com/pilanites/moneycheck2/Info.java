package com.pilanites.moneycheck2;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class Info extends Activity {

	public static boolean Kill = false;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(Kill) Info.this.finish();
		super.onResume();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		if((System.currentTimeMillis() - Main.qTime) > 500) Kill = false;
	}
	
	public void details(View v){
		Intent intent = new Intent(Info.this, Details.class);
		Info.this.finish();
		if(getIntent().getIntExtra("Starter", 0)!= 1) startActivity(intent);
	}
	
	public void addNew(View v){
		Intent intent = new Intent(Info.this, Main.class);
		Info.this.finish();
		if(getIntent().getIntExtra("Starter", 1)!= 0) startActivity(intent);
	}
	
	public void rateIt(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW); 
		intent.setData(Uri.parse("market://details?id=com.pilanites.moneycheck2")); 
		startActivity(intent);
	}
	
	public void quit(View v){
		Details.Kill = true;
		Main.Kill = true;
		Main.qTime = System.currentTimeMillis();
		Info.this.finish();
	}

}
