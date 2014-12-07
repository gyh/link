package com.example.market.ljw.function.lockscreen;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.market.ljw.R;

public class LockSettingActivity extends Activity{
	private final String LOCK_SCREEN_ON_OFF = "lock_screen_on_off";
	
	private CheckBox mSetOnOff;
	
	private boolean mIsLockScreenOn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_locksetting);
		
		// read saved setting.
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);		
		mIsLockScreenOn = prefs.getBoolean(LOCK_SCREEN_ON_OFF, false);
		
		// set checkbox with saved value.
		mSetOnOff = (CheckBox) findViewById(R.id.set_onoff);		
		mSetOnOff.setChecked(mIsLockScreenOn);
		
		mSetOnOff.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(buttonView.isChecked()){
					mSetOnOff.setText("Set IPhone LockScreen ON. [Now is on]");
				}
				else{
					mSetOnOff.setText("Set IPhone LockScreen ON. [Now is off]");
				}
			}
			
		});
		
		EnableSystemKeyguard(false);
	}
	
	/**/
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	
    	startService(new Intent(this, MyLockScreenService.class));
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	
    	mIsLockScreenOn = mSetOnOff.isChecked();
    	
    	if(mIsLockScreenOn)
    		// keep on disabling the system Keyguard
    		EnableSystemKeyguard(false);
    	else {
    		stopService(new Intent(this, MyLockScreenService.class));
    		// recover original Keyguard
    		EnableSystemKeyguard(true);
    	}
    	
    	// save the setting before leaving.
    	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putBoolean(LOCK_SCREEN_ON_OFF, mIsLockScreenOn);
		editor.commit();
    	    	
    }
    
    void EnableSystemKeyguard(boolean bEnable){
    	KeyguardManager mKeyguardManager=null;
    	KeyguardLock mKeyguardLock=null; 
    	
    	mKeyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);  
    	mKeyguardLock = mKeyguardManager.newKeyguardLock(""); 
    	if(bEnable)
    		mKeyguardLock.reenableKeyguard();
    	else
    		mKeyguardLock.disableKeyguard();
    }
}








// test only
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//public class iReceiver extends BroadcastReceiver{
//        @Override
//        public void onReceive(Context context, Intent intent) {
//                // TODO Auto-generated method stub
//                        Intent BootIntent = new Intent(context,Service1.class);
//                        BootIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startService(BootIntent);                
//        }
//}
//
//AndroidMainfest.xml�ǵü���
//
//                <receiver android:name="iReceiver" >
//                    <intent-filter>
//                            <action android:name="android.intent.action.BOOT_COMPLETED" />
//                            <category android:name="android.intent.category.HOME" />
//                           </intent-filter>
//                </receiver>
//
//        <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
