package webdad.apps.radiodaten;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	private static int INTERVAL = 1000 * 3; //3 seconds
	public MediaPlayer mp;
	public Boolean prepared;
	public Boolean play;
	public Boolean hold;
	public Boolean hdstreams;
	public Boolean no_reload;
	
	//public WifiLock wifiLock;
	public TimerTask doAsynchronousTask;
    final Handler handler = new Handler();
    public ScheduledExecutorService exec;
    
    public NotificationCompat.Builder notifier;
    public Intent not_intent;
    public PendingIntent contentIntent;
    public NotificationManager mNotificationManager;
    
	public String selectedWave;
	public int reloadSeconds;
	public Boolean SCREENLOCK;
	public String base_url ="http://metadata.br-online.de/openlogger/metadaten-live/";
	public Map<Pair<String,String>, String> zuordnungen;
	public Spinner sp_nav;
	public static TextView txt_rt;
	public static ImageView img;
	public static ToggleButton btn_toogle;
	
	public static String lastText;
	public static Bitmap lastBitmap;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {	
        super.onCreate(savedInstanceState);
        Log.i("App", "Starting");
        setContentView(R.layout.activity_main);
        sp_nav = (Spinner)findViewById(R.id.sp_navigation);
        btn_toogle = (ToggleButton)findViewById(R.id.btn_toogle_Stream);
        txt_rt = (TextView)findViewById(R.id.txt_radiotext);
        img = (ImageView)findViewById(R.id.img_sls);
        zuordnungen = Zuordnungen.getZuordnungen(this);
        loadPrefs(true);
        INTERVAL = 1000 * reloadSeconds;
        timer_Setup();
        player_Setup();
            sp_nav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) 
                {
                    selectedWave=parent.getItemAtPosition(pos).toString();
                    Log.i("App", "Switched Wave to "+selectedWave);
                    timer_Restart();
                    player_Update();
                    notifier_Update();
                }
                public void onNothingSelected(AdapterView<?> parent) 
                {
                }
            });
            
   
    }
    @Override
    public void onStart()
    {
    	super.onStart();
    }
    @Override
    public void onResume()
    {
    	super.onResume();
    	timer_Start();
    	player_Start();
    	notifier_Hide();
    }
    //Here we are running
    @Override
    public void onPause()
    {
    	super.onPause();
    	notifier_Show();
    	timer_Stop();
    	if(!hold)player_Stop();
    }
    @Override
   	public void onStop()
   	{
   		super.onStop();
   		player_Destroy();
   	}
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    }
    public void notifier_Setup()
    {
   	 try
   	 {
   	 not_intent = new Intent(this, MainActivity.class);
   	 not_intent.setAction(Intent.ACTION_MAIN);
   	 not_intent.addCategory(Intent.CATEGORY_LAUNCHER);
   	 not_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
   	 contentIntent = PendingIntent.getActivity(this.getParent(), 0, not_intent, 0);
   	 notifier.setSmallIcon(R.drawable.ic_menu_play_clip);
   	 notifier.setContentTitle(getString(R.string.not_title));
   	 notifier.setContentText(getString(R.string.not_text)+": "+selectedWave);
   	 notifier.setContentIntent(contentIntent);
   	 mNotificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
   	 }
   	 catch(Exception e)
   	 {
   		 String err = (e.getMessage()==null)?"Exception Error":e.getMessage();
   		 Log.e("Notify", err);
   		 e.printStackTrace();
   		 notifier=null;
   	 }
    }
    public void notifier_Update()
    {
    	
    }
    public void notifier_Show()
    {
    	try
    	{
    	if(hold && notifier!=null)mNotificationManager.notify(333, notifier.build());
    	}
    	catch(Exception e)
    	{
    		String err = (e.getMessage()==null)?"Exception Error":e.getMessage();
    		Log.e("NotifyPause", err);
    	}
    }
    public void notifier_Hide()
    {
    	try
    	{
    	if(hold && notifier!=null)mNotificationManager.cancel(333);
    	}
    	catch(Exception e)
    	{
    		String err = (e.getMessage()==null)?"Exception Error":e.getMessage();
    		Log.e("NotifyResume", err);
    	}
    }
    public void notifier_Destroy()
    {
    	
    }
    public void player_Setup()
    {
    	mp = new MediaPlayer();
    	Log.i("MP", "Player created");
    	mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
    	Log.i("MP", "Audio set to Stream");
    	mp.setOnPreparedListener(new OnPreparedListener() 
    		{
			public void onPrepared(MediaPlayer mp) 
				{
				Log.i("MP", "Stream Prepared");
				prepared=true;
				Log.i("MP", "Starting Player...");
		        mp.start();
				}
    		});
    	mp.setOnErrorListener(new OnErrorListener() {
			
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.e("MP", "MP Error: "+what+" E: "+extra);
				return false;
			}
		});
    	mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    	Log.i("App", "WakeMode set");
    	//wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
    	
    	player_Update();
    }
    public void player_Update()
    {
    	Log.i("MP", "Updating Player");
    	player_Stop();
    	prepared=false;
    	if(mp!=null)
    		mp.reset();
    	try 
    	{
    		if(hdstreams)
    		{
    			Log.i("MP", "Setting DataSource to "+zuordnungen.get(new Pair<String, String>(selectedWave, "h_stream_a")));
    			mp.setDataSource(zuordnungen.get(new Pair<String, String>(selectedWave, "h_stream_a")));
    		}
    		else
    		{
    			Log.i("MP", "Setting DataSource to "+zuordnungen.get(new Pair<String, String>(selectedWave, "l_stream_a")));
    			mp.setDataSource(zuordnungen.get(new Pair<String, String>(selectedWave, "l_stream_a")));
    		}
		} 
    	catch (IllegalArgumentException e)
    	{
    		Log.e("MP", e.getMessage());
			return;
		} 
    	catch (IllegalStateException ex) 
    	{
    		String err = (ex.getMessage()==null)?"IllegalStateException":ex.getMessage();
    		Log.e("MP", err);
			return;
		} 
    	catch (IOException e) 
    	{
    		Log.e("MP", e.getMessage());
			return;
		}
    }
    public void player_Start()
    {
    	Log.i("Button", "Play");
    	if(mp!=null)
    	{
	    	if(!mp.isPlaying() && play)
	    	{
	    		try
	        	{
	        		Log.i("MP", "Preparing Async...");
	        		mp.prepareAsync();
	        	}
	        	catch(IllegalStateException e)
	        	{
	        		String err = (e.getMessage()==null)?"IllegalStateException":e.getMessage();
	        		Log.e("MP", err);
	    			return;
	        	}
	    	}
    	}
    	//wifiLock.acquire();
    }
    public void player_Stop()
    {
    	Log.i("Button", "Stop");
    	if(mp!=null)
    	{
	    	if(mp.isPlaying() && prepared)
	    	{
	    		Log.i("MP", "Pausing Player...");
	        	mp.pause();
	    	}
    	}
    	//wifiLock.release();
    }
    public void player_Destroy()
    {
    	player_Stop();
    	if(mp!= null)
    	{
    	mp.reset();
    	mp.release();
    	mp=null;
    	}
    }
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadPrefs(Boolean first_start)
    {
		Log.i("App", "Loading Preferences...");
		try
		{
    	  SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    	  if(first_start)
    	  {
    		  selectedWave = sharedPref.getString("pref_startingWave", getString(R.string.starting_Wave));
    	  }
         reloadSeconds = Integer.parseInt(sharedPref.getString("pref_reloadTime", getString(R.string.def_reloadTime)));	
         Log.i("App", "reload Seconds = "+reloadSeconds);
         SCREENLOCK = sharedPref.getBoolean("pref_screenlock", Boolean.parseBoolean(getString(R.string.def_screenlock)));	
         Log.i("App", "SCREENLOCK = "+SCREENLOCK);
         play = sharedPref.getBoolean("pref_audio_autostart", Boolean.parseBoolean(getString(R.string.def_audio_autostart)));
         Log.i("App", "autoplay = "+play);
         hold = sharedPref.getBoolean("pref_audio_hold", Boolean.parseBoolean(getString(R.string.def_audio_hold)));
         Log.i("App", "hold audio = "+hold);
         hdstreams = sharedPref.getBoolean("pref_audio_hd", Boolean.parseBoolean(getString(R.string.def_audio_hd)));
         Log.i("App", "hdstreams = "+hdstreams);
         no_reload = sharedPref.getBoolean("pref_no_reload", Boolean.parseBoolean(getString(R.string.def_no_reload)));
         Log.i("App", "no reload = "+no_reload);
		}
		catch(Exception e)
		{
			Log.e("App", e.getMessage());
			 if(first_start)
	    	  {
				 selectedWave=getString(R.string.starting_Wave);
	    	  }
			reloadSeconds=Integer.parseInt(getString(R.string.def_reloadTime));
			SCREENLOCK=Boolean.parseBoolean(getString(R.string.def_screenlock));
			play=Boolean.parseBoolean(getString(R.string.def_audio_autostart));
			hold=Boolean.parseBoolean(getString(R.string.def_audio_hold));
			hdstreams=Boolean.parseBoolean(getString(R.string.def_audio_hd));
			e.printStackTrace();
		}
		ArrayAdapter myAdap = (ArrayAdapter) sp_nav.getAdapter();
         int spinnerPosition = myAdap.getPosition(selectedWave);

         sp_nav.setSelection(spinnerPosition);
         if(SCREENLOCK==true)
         {
        	 getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
         }
         else
         {
        	 getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
         }
         if(play)
         {
        	 btn_toogle.setChecked(true);
         }
         else
         {
        	 btn_toogle.setChecked(false);
         }
         if(hold)
         {
        	 notifier_Setup();
         }
    }
	public void onToggleStreamClicked(View view)
	{
		
		 boolean on = ((ToggleButton) view).isChecked();
		 Log.i("Button", "Clicked to "+on);
		 if (on) {
			 play=true;
		       player_Start();
		    } else {
		    	play=false;
		        player_Stop();
		    }
	}
    private void update()
    {
    	Log.i("App", "Updating Data");
    	new UpdateTask_text().execute("");
        new UpdateTask_pic().execute("");
    }
    public void timer_Setup()
    {
    	Log.i("App", "Setting up Timer");
    	if(!no_reload)
    	{
    	exec = Executors.newSingleThreadScheduledExecutor();
        doAsynchronousTask = new TimerTask() 
        	{
            @Override
            public void run() 
            	{
                handler.post(new Runnable() 
                	{
                    public void run() 
                    	{
                        try 
                        	{
                            update();
                            }
                        catch (Exception e) 
                        	{
                        	Log.e("Timer",e.getMessage());
                        	e.printStackTrace();
                            }
                        }
                     });
                 }
             };
    	}
    	else
    	{
    		txt_rt.setText(getString(R.string.no_reload));
    	}
    }
    public void timer_Start()
    {
    	if(!no_reload)
    	{
    	Log.i("Timer", "Starting Timer");
        exec.scheduleAtFixedRate(doAsynchronousTask, 0, INTERVAL, TimeUnit.MILLISECONDS);
    	}
    }
    public void timer_Stop()
    {
    	if(!no_reload)
    	{
    	Log.i("Timer", "Stopping Timer");
    	exec.shutdown();
    	exec = Executors.newSingleThreadScheduledExecutor();
    	}
    }
    public void timer_Restart()
    {
    	Log.i("Timer", "Restarting Timer");
    	timer_Stop();
    	timer_Start();
    }
  public void timer_destroy()
  {
	  
  }

	@Override
   public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("App", "Creating Settings");
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_settings:
	        	try
	        	{
	        	Intent in = new Intent(MainActivity.this, preferences.class);
	        	MainActivity.this.startActivity(in);
	        	}
	        	catch(Exception e)
	        	{
	        	Log.e("Menu", e.getMessage());
	        	}
	        	loadPrefs(false);
	            return true;
	        case R.id.menu_author:
	        	try
	        	{
	        	Intent in = new Intent(MainActivity.this, author.class);
	        	MainActivity.this.startActivity(in);
	        	}
	        	catch(Exception e)
	        	{
	        		Log.e("Menu", e.getMessage());
	        	}
	            return true;
	        case R.id.menu_playstream:
	        	play=true;
	        	player_Start();
	        	return true;
	        case R.id.menu_pausestream:
	        	player_Stop();
	        	return true;
	        case R.id.menu_share:
	        	Intent sendIntent = new Intent();
	        	sendIntent.setAction(Intent.ACTION_SEND);
	        	sendIntent.putExtra(Intent.EXTRA_TEXT, "BR-RadioText: "+txt_rt.getText());
	        	sendIntent.setType("text/plain");
	        	startActivity(sendIntent);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	private class UpdateTask_text extends AsyncTask<String, Void, String> 
		{
		protected String doInBackground(String... urls) 
			{
			Log.i("BG_Text", "Loading Text");
	        return getText(base_url+"cache/1/"+zuordnungen.get(new Pair<String, String>(selectedWave, "text")));
			}
		 protected void onPostExecute(String result) 
		 	{
			 Log.i("BG_Text", "Setting Text: "+result);
			 if(result.equals(getString(R.string.error_text)))
			 {
				 txt_rt.setText(lastText); 
			 }
			 else	
			 {
				 txt_rt.setText(result); 
				 lastText=result;
			 }
			 

		      
		        //TODO: set Text size depending on screen width
		    }    
			    
		}
	
	private class UpdateTask_pic extends AsyncTask<String, Void, Bitmap> 
	{
	protected Bitmap doInBackground(String... urls) 
		{
		Log.i("BG_Pic", "Loading Picture");
        return getBitmap(getImageName(base_url+"cache/slideshow/"+zuordnungen.get(new Pair<String, String>(selectedWave, "img"))));
		}
	 protected void onPostExecute(Bitmap result) 
	 	{
		 Display display = getWindowManager().getDefaultDisplay();
			DisplayMetrics dm =  new DisplayMetrics();
			display.getMetrics(dm);
			
			double w1 = result.getWidth();
			double h1 = result.getHeight();
			
			int w,h;
			double ratio;
			if(display.getRotation()== Surface.ROTATION_0 || display.getRotation()== Surface.ROTATION_180)
			{
				w = dm.widthPixels;
				ratio = w1 / h1;
				h = (int) Math.round(w / ratio);
			}
			else
			{
				h = dm.heightPixels;
				ratio = w1/h1;
				w = (int) Math.round(h / ratio);
			}
		
			
			Bitmap b;
		 if(result.equals(BitmapFactory.decodeResource(getResources(), R.drawable.error_loading)))
		 {
			 b = Bitmap.createScaledBitmap(lastBitmap, w, h, false);
		 }
		 else
		 {
			 b = Bitmap.createScaledBitmap(result, w, h, false);
			 lastBitmap=b;
		 }
			img.getLayoutParams().width=w;
			img.getLayoutParams().height=h;
			img.setAdjustViewBounds(true);
			img.setMaxHeight(h);
			
			if(display.getRotation()== Surface.ROTATION_0 || display.getRotation()== Surface.ROTATION_180)
			{
				//TODO: default display
			}
			else
			{
				//TODO: left bound display
			}
			img.setScaleType(ImageView.ScaleType.FIT_CENTER);
	        img.setImageBitmap(b);
	        Log.i("BG_Pic", "Bitmap set");
	        
	    }    
		    
	}
	
   public String getText(String url_text)
   {
	   Log.i("BG_Text", "Getting HTTP");
	   HttpClient client = new DefaultHttpClient();
	   HttpGet request = new HttpGet(url_text);
	   HttpResponse response;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			Log.e("BG_Text","CPE "+e.getMessage());
			return getString(R.string.error_text);
		} catch (IOException e) {
			Log.e("BG_Text","IOE "+e.getMessage());
			return getString(R.string.error_text);
		}
	   InputStream in;
		try {
			in = response.getEntity().getContent();
		} catch (IllegalStateException e) {
			Log.e("BG_Text","ISE "+e.getMessage());
			return getString(R.string.error_text);
		} catch (IOException e) {
			Log.e("BG_Text","IOE "+e.getMessage());
			return getString(R.string.error_text);
		}
	   BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	   String line = null;
	   try {
		line = reader.readLine();
	} catch (IOException e) {
		Log.e("BG_Text","IOE "+e.getMessage());
		return getString(R.string.error_text);
	}
	   try {
		in.close();
		} catch (IOException e) {
			Log.e("BG_Text","IOE "+e.getMessage());
			return getString(R.string.error_text);
		}
	   Log.i("BG_Text", "Returning: "+line);
	   return line;

   }
   public String getImageName(String url)
   {
	   Log.i("BG_Pic","Getting Image Name");
	   HttpClient client = new DefaultHttpClient();
	   HttpGet request = new HttpGet(url);
	   HttpResponse response;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			Log.e("BG_Pic",e.getMessage());
			return getString(R.string.error_text);
		} catch (IOException e) {
			Log.e("BG_Pic",e.getMessage());
			return getString(R.string.error_text);
		}
	   InputStream in;
		try {
			in = response.getEntity().getContent();
		} catch (IllegalStateException e) {
			Log.e("BG_Pic",e.getMessage());
			return getString(R.string.error_text);
		} catch (IOException e) {
			Log.e("BG_Pic",e.getMessage());
			return getString(R.string.error_text);
		}
	   BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	   String line = null;
	   try {
		line = reader.readLine();
	} catch (IOException e) {
		Log.e("BG_Pic",e.getMessage());
		return getString(R.string.error_text);
	}
	   try {
		in.close();
		} catch (IOException e) {
			Log.e("BG_Pic",e.getMessage());
			return getString(R.string.error_text);
		}
	   //SomeHow get the src of the image in this thing
	  //<!--ts:1348813102--><img src="cache/slideshow/image_dabon3_1348813102.jpg" alt="SlideShow for dabon3" />
	   String[] separated = line.split("\"");
	   Log.i("BG_Pic", "Picture name: "+base_url+separated[1]);
	   return base_url+separated[1];
   }
   public Bitmap getBitmap(String url) {
	   Log.i("BG_Pic", "Getting Bitmap from "+url);
	   URL newurl;
	try {
		newurl = new URL(url);
	} catch (MalformedURLException e) {
		Log.e("BG_Pic", e.getMessage());
		return BitmapFactory.decodeResource(this.getResources(), R.drawable.error_loading);

	} 
	   try {
		return BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
	} catch (IOException e) {
		Log.e("BG_Pic", e.getMessage());
		return BitmapFactory.decodeResource(this.getResources(), R.drawable.error_loading);
	} 
	}
   
}

