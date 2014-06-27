package com.hui.mybox.activity;

import com.hui.mybox.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class BoxMainFrameAct extends ActionBarActivity implements TabListener  {
	
	private static final String TAG = "BoxMainFrameAct";
	private ActionBar actionBar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_act);
		
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_section1))
				.setTabListener(this));

		actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_section2))
				.setTabListener(this));

		actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_section3))
				.setTabListener(this));

	}

	@Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        //����action items  
        getMenuInflater().inflate(R.menu.main, menu);  
        return true;  
    } 

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		Log.i(TAG, arg0.getText().toString());
		switch (arg0.getPosition()) {
		case 0:
			ImgListFragment imgListFragment = new ImgListFragment();
			arg1 = getSupportFragmentManager().beginTransaction();
			arg1.add(R.id.fl_fragment_container, imgListFragment, imgListFragment.getClass().getCanonicalName());
			arg1.commit();
			break;

		default:
			break;
		}
		
		
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	
	

}