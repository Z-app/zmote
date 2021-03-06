package se.z_app.zmote.gui;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

import se.z_app.stb.EPG;
import se.z_app.stb.Program;
import se.z_app.stb.STB;
import se.z_app.stb.STBEvent;
import se.z_app.stb.api.EPGData;
import se.z_app.stb.api.RemoteControl;
import se.z_app.stb.api.STBContainer;
import se.z_app.stb.api.STBListener;
import se.z_app.stb.api.RemoteControl.Button;
import android.app.ActionBar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;

import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;


/**
 * The main Fragment activity, extends the Sherlock library to support compatibility.
 * 
 * @author refrectored by: Linus Back
 * 
 */
public class MainTabActivity extends SherlockFragmentActivity implements TabListener, Observer{

	private com.actionbarsherlock.app.ActionBar actionBar;
	private Tab tabRC;
	private Tab tabMain;
	private Tab tabEPG;
	private Tab tabFav;
	private Tab tabWeb;
	private Spinner mySpinner;
	private ArrayList<String> STBNames;
	private Vibrator vibe;	
	private static Handler myHandler;
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private Fragment currentFragmen;
	private RemoteControlFragment rcfragment;
	private EPGFragment epgfragment;
	private WebTVFragment webfragment;
	private MainViewFragment mainfragment;
    private ChannelInformationFragment chinfragment;
    private PlayMediaFilesFragment filesfragment;
    private ImageView volumIcon;
    private ProgressBar volumPB;
    private int tmp;
    private boolean boolTemp;
    public int SDK_INT = android.os.Build.VERSION.SDK_INT;
	
    /**
     * Update of the volume and mute state.
     * @param observable
     * @param data
     */
	@Override
	public void update(Observable observable, Object data) {
		STBEvent event = STBListener.instance().getCurrentEvent();
		
		if(event.getType().equals("mute")){
			System.out.println();
			boolTemp = event.getState();
			 
			runOnUiThread(new Runnable() {
				boolean state = boolTemp;
				
				@Override
				public void run() {
					if(state){
						volumIcon.setImageDrawable(getResources().getDrawable(R.drawable.vol_mute));
					}else{ 
						volumIcon.setImageDrawable(getResources().getDrawable(R.drawable.vol_up2));
					}
					
				}
			});
			
		}else if(event.getType().equals("volume")){
			tmp = event.getValue();
			runOnUiThread(new Runnable() {
				int value = tmp;
				@Override
				public void run() {
					volumPB.setProgress(value);	
					volumIcon.setImageDrawable(getResources().getDrawable(R.drawable.vol_up2));
				}
			});
		}
	}
    
    
    /**
     * Destroy of the view
     */
    @Override
	protected void onDestroy() {
		super.onDestroy();
		STBListener.instance().deleteObserver(this);
	}



	/**
	 * Standard create function for the fragment activity.
	 * Sets the layout.
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_tab);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		STBListener.instance().addObserver(this);
		
		vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE) ;

		myHandler = new Handler(){
			public void handleMessage(Message mst){
				super.handleMessage(mst);
				setAlive(mst.what);
			}
		};
		new LoadVolume().execute();
		new Thread(new MyTimedTask()).start();
	}


	/**
	 * Allows you to set the orientation of the screen from outside of the class
	 * @param i
	 */
    public void setOrientation(int i){
    		setRequestedOrientation(i);
    }


	/**
	 * Vibrates the phone for 95 milliseconds.
	 */
	public void vibrate(){
		vibe.vibrate(95);
	}
	
	/**
	 * vibrates the phone a number a number of milliseconds.
	 * @param ms number of milliseconds the the phone vibrates
	 */
	public void vibrate(int ms){
		vibe.vibrate(ms);
	}

	/**
	 * Restores the navigation state.
	 * @param savedInstanceState
	 */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM) && (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)) {
        	getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

	/**
	 * Saves the navigation state.
	 * @param outState
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
				getSupportActionBar().getSelectedNavigationIndex());
	}

	/**
	 * Creates and shows the Action bar, including the navigations tabs and the
	 * drop-down list.
	 * @param menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		actionBar = getSupportActionBar();

		/*Calls the private function to create return the view containing the 
    	spinner*/
		View dropDownView = createDotAndDropDown();

		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(dropDownView);


		actionBar.setLogo(R.drawable.green_button2);
		actionBar.setDisplayUseLogoEnabled(true);        
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		addTheNavigationTabs();

		return true;
	}

	/**
	 * Used to bind events to the physical volume buttons of the device.
	 * In this case, it increase and decreases the volume of the STB.
	 * @param event
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();
		switch (keyCode) {
		
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (action == KeyEvent.ACTION_UP) {
				RemoteControl.instance().sendButton(Button.VOLPLUS);
				vibrate();
			}
			return true;
			
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (action == KeyEvent.ACTION_DOWN) {
				RemoteControl.instance().sendButton(Button.VOLMINUS);
				vibrate();
			}
			return true;
			
		default:
			return super.dispatchKeyEvent(event);
		}
	}

	/**
	 * Sets and displays the fragment that the user selects from the tabs.
	 * If new fragments are implemented they should be set here.
	 * @param tab
	 * @param ft
	 */
	@Override
	public void onTabSelected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		Fragment fragment = null;
    	boolean isNew = false;
    	
    	if(!tab.equals(tabWeb)){
    		if(findViewById(R.id.search_box_webtv) != null){
    		InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(findViewById(R.id.search_box_webtv).getWindowToken(), 0);
    		}
    	}
    	
    	if(tab.equals(tabRC)){
    		Log.i("FragmentLog", "RC");
    		if(rcfragment == null){
    			rcfragment = new RemoteControlFragment(this);
    			isNew = true;
    		}
    		fragment = rcfragment;
    		
    	}
    	else if(tab.equals(tabEPG)){
    		Log.i("FragmentLog", "EPG");
    		if(epgfragment == null){
    			epgfragment = new EPGFragment(this);
    			isNew = true;
    		}
    		fragment = epgfragment;
    	}
    	else if(tab.equals(tabWeb)){
    		Log.i("FragmentLog", "WebTV");
    		if(webfragment == null){
    			webfragment = new WebTVFragment(this);
    			isNew = true;
    		}
    		fragment = webfragment;
    		
    	}
		else if(tab.equals(tabFav)){
			Log.i("FragmentLog", "Files");
			if(filesfragment == null){
				filesfragment = new PlayMediaFilesFragment(this);
				isNew = true;
    		}
			fragment = filesfragment;
					
		}else if(tab.equals(tabMain)){
			Log.i("FragmentLog", "Main");
			if(mainfragment == null && SDK_INT > 10){
				mainfragment = new MainViewFragment(this);
				isNew = true;
			}
			fragment = mainfragment;
		}

    	
		if(fragment != null)
			currentFragmen = fragment;
			if(isNew)
				getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
			else
				getSupportFragmentManager().beginTransaction().show(fragment).commit();
 
	}

	/**
	 * Auto-generated method. Does nothing.
	 */
	@Override
	public void onTabUnselected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {

		Fragment fragment = null;
		if(tab.equals(tabRC)){
    		Log.i("Detaching FragmentLog", "RC");
    		fragment = rcfragment;
    	}
    	else if(tab.equals(tabEPG)){
    		Log.i("Detaching FragmentLog", "EPG");
    		fragment = epgfragment;
    	}
    	else if(tab.equals(tabWeb)){
    		Log.i("Detaching FragmentLog", "WebTV");
    		fragment = webfragment;
    	}
		else if(tab.equals(tabFav)){
			Log.i("Detaching FragmentLog", "Fav");
			fragment = filesfragment;
			
		}
		else if(tab.equals(tabMain)){
			Log.i("Detaching FragmentLog", "Main");
			fragment = mainfragment;
		}
    	
		if (fragment != null) {
	         //ft.detach(fragment);
			
	        getSupportFragmentManager().beginTransaction().hide(fragment).commit();
	        getSupportFragmentManager().beginTransaction().hide(currentFragmen).commit();
	    }	

	}
	
	/**
	 * Auto-generated method. Does nothing.
	 */
	@Override
	public void onTabReselected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		Fragment fragment = null;
		if(tab.equals(tabRC)){
    		Log.i("Detaching FragmentLog", "RC");
    		fragment = rcfragment;
    	}
    	else if(tab.equals(tabEPG)){
    		Log.i("Detaching FragmentLog", "EPG");
    		fragment = epgfragment;
    	}
    	else if(tab.equals(tabWeb)){
    		Log.i("Detaching FragmentLog", "WebTV");
    		fragment = webfragment;
    	}
		else if(tab.equals(tabFav)){
			Log.i("Detaching FragmentLog", "Fav");
			fragment = filesfragment;
			
		}
		else if(tab.equals(tabMain)){
			Log.i("Detaching FragmentLog", "Main");
			fragment = mainfragment;
		}
    	
		if (fragment != null) {
	        getSupportFragmentManager().beginTransaction().hide(currentFragmen).commit();
	        getSupportFragmentManager().beginTransaction().show(fragment).commit();
	        currentFragmen = fragment;
	    }	

	}
		
	/**
	 * Shows channels information
	 * @param program
	 */
	public void showChannelInformation(Program program){
		getSupportFragmentManager().beginTransaction().hide(currentFragmen).commit();
		if(chinfragment == null){
			chinfragment = new ChannelInformationFragment(this, program);
			getSupportFragmentManager().beginTransaction().add(R.id.container, chinfragment).commit();
			
		}else{
			chinfragment.focusOnProgram(program);
			getSupportFragmentManager().beginTransaction().show(chinfragment).commit();
		}
		currentFragmen = chinfragment;
			
	}

	/**
	 * Adds all the navigation tabs to the action bar.
	 */
	private void addTheNavigationTabs() {
		// For each of the sections in the app, add a tab to the action bar.
		tabRC = actionBar.newTab().setIcon(R.drawable.ic_dialog_dialer);
		tabMain = actionBar.newTab().setIcon(R.drawable.ic_new_home);
		tabEPG = actionBar.newTab().setIcon(R.drawable.collections_go_to_today);

		tabFav = actionBar.newTab().setIcon(R.drawable.sd_storage);
		tabWeb = actionBar.newTab().setIcon(R.drawable.location_map);

		// Add the tabs to the action bar
		actionBar.addTab(tabMain.setTabListener(this));
		actionBar.addTab(tabRC.setTabListener(this));
		actionBar.addTab(tabEPG.setTabListener(this));
		actionBar.addTab(tabWeb.setTabListener(this));
		actionBar.addTab(tabFav.setTabListener(this));

	}
	
	/**
	 * Creates the spinner for the drop down menu.
	 * @return
	 */
	private View createDotAndDropDown(){

		View myView = getLayoutInflater().inflate(
				R.layout.activity_main_tab_actionbar, null);
		mySpinner = (Spinner) myView.findViewById(R.id.action_bar_spinner);

		/*gets all the boxes from the STB container. And saves the index of the
    	 selected one so that the spinners default value is correct.*/
		Iterator<STB> iterator = STBContainer.instance().iterator();
		STBNames = new ArrayList<String>();
		int temp = 0;
		int selected = 0;
		STB stb;
		
		while(iterator.hasNext()){ 		
			if(STBContainer.instance().getActiveSTB().equals(stb = iterator.next())){
				selected = temp;
			}
			STBNames.add(stb.getBoxName());
			temp++;
		}

		//Adds the Edit button to the Spinner
		STBNames.add("Edit...");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, STBNames);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.
				simple_spinner_dropdown_item);
		// Apply the adapter to the spinner, also applies a listener.
		mySpinner.setAdapter(adapter);
		mySpinner.setSelection(selected);

		mySpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(selected));
		
		volumPB = (ProgressBar)myView.findViewById(R.id.volume_progressbar);
		volumIcon = (ImageView)myView.findViewById(R.id.volume_icon);
		volumIcon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RemoteControl.instance().sendButton(Button.MUTE);
			}
		});

		return myView;				
	}

	public void setAlive(int isAlive){

		if(actionBar == null){
			return;
		}
		if(isAlive==1){
			actionBar.setLogo(R.drawable.green_button2);
		}else{
			actionBar.setLogo(R.drawable.red_dot);
		}
	}

	/**
	 * Private class that implements OnItemSelectedListener. The reason for this
	 * is that i got some sort of conflict when i tried to implement this 
	 * interface in the mainTabActivity class, possible because of the Sherlock 
	 * library.
	 * 
	 * There might be a better solution for this, but this works right now.
	 * @author Linus Back (linba708)
	 *
	 */
	private class MyOnItemSelectedListener implements OnItemSelectedListener{

		/**
		 * Sets the active STB in the STB container based on what the user 
		 * selects in the drop down menu.
		 */
		private int lastSelected;
		public MyOnItemSelectedListener(int selected) {
			lastSelected = selected;
		}

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			//Checks if the chosen item is the edit one.
			if(arg2==arg0.getAdapter().getCount()-1){
				Intent mainIntent = new Intent(MainTabActivity.this,
				SelectSTBActivity.class); 
				MainTabActivity.this.startActivity(mainIntent);
				arg0.setSelection(lastSelected);
				
				
			}
			else{
				lastSelected = arg2;
				STBContainer.instance().setActiveSTB(STBContainer.instance().getSTBs()[arg2]);
			}
		}

		/**
		 * Autogenerated function. Does nothing right now.
		 */
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {


		}


	}
	
	/**
	 * Loads the volume of the app.
	 */
	private class LoadVolume extends AsyncTask<Integer, Integer, Integer>{

		@Override
		protected Integer doInBackground(Integer... params) {
			return EPGData.instance().getVolume();
		}
		
		@Override
		protected void onPostExecute(Integer volume) {
			if(volumPB != null)
				volumPB.setProgress(volume);
		}
		
	}
	
	/**
	 * Asynchronous task.
	 */
	private class MyTimedTask implements Runnable{

		int timeout= 1000;
		int timer = 100;
		boolean boxactive = false;
		boolean newBoxactive = false;


		@Override
		public void run() {
			while(true){
				try {
					newBoxactive = Inet4Address.getByName(STBContainer.instance().getActiveSTB().getIP()).isReachable(timeout);
					
					if(!newBoxactive)
						newBoxactive = Inet4Address.getByName(STBContainer.instance().getActiveSTB().getIP()).isReachable(timeout);
					
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				if(boxactive == newBoxactive){

				}
				else if(newBoxactive){		
					myHandler.sendEmptyMessage(1);
					boxactive = newBoxactive;
				}
				else{
					myHandler.sendEmptyMessage(0);
					boxactive = newBoxactive;
				}
				try {
					Thread.sleep(timer);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}



}
