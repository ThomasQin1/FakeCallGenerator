package control.pot.coffee.fakecallgenerator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CallingActivity extends AppCompatActivity {
    private static final String TAG = "CallingActivity";
    Context ctx;
    AppCompatActivity activity;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private View mAnswerView; // For answer button before call is answered or rejected
    private View mRejectView; // For reject button before call is answered or rejected
    private ImageButton mEndCallView; // For end call button after call is answered
    private View mEndCallCircleView;//Endcallbuttonhandler
    private View mEndHandleCallView;//Endcallhandlebuttonhandler

    private ImageButton mAnswerCallView; // For end call button after call is answered
    private View mAnswerCallCircleView;//Answercallcirclebuttonhandler
    private View mAnswerHandleCallView;//Answercallhandlebuttonhandler

    private ImageButton mRejectCallView; // For end call button after call is answered
    private View mRejectCallCircleView;//Rejectcallbuttonhandler
    private View mRejectHandleCallView;//Rejectcallbuttonhandler

    private View mEndPictureView;//Endcallbuttonhandler
    private AudioManager audioManager;
    private Uri photo;
    private String name;
    private String number;
    private String photoUri;
    private Vibrator v;
    private WindowManager.LayoutParams layoutParam;

    private Bundle extras;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    //mutes ALL STREAMS
    public void muteAll(){

        audioManager.setStreamVolume(AudioManager.STREAM_ALARM ,        AudioManager.ADJUST_MUTE, 1);
        audioManager.setStreamVolume(AudioManager.STREAM_DTMF ,         AudioManager.ADJUST_MUTE, 2);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC ,        AudioManager.ADJUST_MUTE, 3);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION , AudioManager.ADJUST_MUTE, 4);
        audioManager.setStreamVolume(AudioManager.STREAM_RING ,         AudioManager.ADJUST_MUTE, 5);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM ,       AudioManager.ADJUST_MUTE, 6);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL ,   AudioManager.ADJUST_MUTE, 7);
        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        ctx = this;
        activity = this;

        extras = getIntent().getExtras();

        Log.i(TAG, "Calling Activity started");

        //Allows activity to be displayed on lock screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE); // To later play the ringtone

        //muteAll();

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        mAnswerView = findViewById(R.id.acceptButton); // Answer call button handler
        mRejectView = findViewById(R.id.rejectButton); // Reject call button handler
        mEndCallView = (ImageButton)findViewById(R.id.endCallButton); // End call button handler

        mEndCallCircleView=findViewById(R.id.red_circle2);//Endcallbuttonhandler
        mEndHandleCallView=findViewById(R.id.reject2);//Endcallbuttonhandler
        mEndPictureView=findViewById(R.id.contact_picture);//Endcallbuttonhandler

        mAnswerCallView =(ImageButton) findViewById(R.id.acceptButton);
        mAnswerCallCircleView = findViewById(R.id.green_circle);
        mAnswerHandleCallView = findViewById(R.id.answer);

        mRejectCallView = (ImageButton) findViewById(R.id.rejectButton);
        mRejectCallCircleView = findViewById(R.id.red_circle);
        mRejectHandleCallView = findViewById(R.id.reject);


        // Get name, number, phtoUri information

            String Ename = extras.getString(Constants.EXTRA_KEY_NAME, null);
            String Enumber = extras.getString(Constants.EXTRA_KEY_NUMBER, null);
            photoUri = extras.getString(Constants.EXTRA_KEY_PHOTO, null);

        Log.v("MainActivity", Ename + " | " + Enumber + " | " + photoUri);
        Log.v("CallingActivity", Ename + " | " + Enumber + " | " + photoUri);


        name = (Ename != null ? Ename : "Mom");
        number = (Enumber != null ? Enumber : "1 (858) 453 5343");


        TextView nameView = (TextView) findViewById(R.id.activity_calling_name);
        TextView numberView = (TextView) findViewById(R.id.activity_calling_number);

        nameView.setText(name);
        numberView.setText(number);

        // Set the contact image
        if(photoUri == null){
            CardView cardView = (CardView) findViewById(R.id.contact_picture);
            final ImageView imageView = (ImageView) cardView.findViewById(R.id.contact_pic);
            imageView.setImageDrawable(getResources(R.drawable.default_contact));
        }
        else{
            photo = Uri.parse(photoUri);
            CardView cardView = (CardView) findViewById(R.id.contact_picture);
            final ImageView imageView = (ImageView) cardView.findViewById(R.id.contact_pic);
            imageView.setImageURI(photo);
        }

        // Getting the default ringTone to play
        final MediaPlayer player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        player.start();

        //Phone vibrates
        // Get instance of Vibrator from current Context
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        // Start without a delay, Vibrate for 1200 milliseconds, Sleep for 500 milliseconds
        long[] pattern = {0, 1200, 500};

        v.vibrate(pattern, 0);

        //FOR WHEN THE ACCEPT BUTTON IS PRESSED
        mAnswerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stop ringing and vibrating
                player.stop();
                v.cancel();

                mAnswerView.setVisibility(View.INVISIBLE);
                mRejectView.setVisibility(View.INVISIBLE);

                // Change View to look like in call
                mAnswerCallView.setVisibility(View.INVISIBLE);
                mAnswerCallCircleView.setVisibility(View.INVISIBLE);
                mAnswerHandleCallView.setVisibility(View.INVISIBLE);

                mRejectCallView.setVisibility(View.INVISIBLE);
                mRejectCallCircleView.setVisibility(View.INVISIBLE);
                mRejectHandleCallView.setVisibility(View.INVISIBLE);

                mEndCallView.setVisibility(View.VISIBLE);
                mEndCallCircleView.setVisibility(View.VISIBLE);
                mEndHandleCallView.setVisibility(View.VISIBLE);

                // Put screen to sleep
                WindowManager.LayoutParams layoutParam = getWindow().getAttributes();
                // oldBrightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS)/255f;
                layoutParam.screenBrightness = 0;
                layoutParam.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                getWindow().setAttributes(layoutParam);

                mEndCallView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.finish();
                    }
                });
            }
        });

        // WHEN THE REJECT BUTTON IS PRESSED
        mRejectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.stop();
                v.cancel();

                //Schedule next activity
                int repeats = extras.getInt(Constants.EXTRA_KEY_REPEATS);
                if (repeats > 0) {
                    int interval= extras.getInt(Constants.EXTRA_KEY_INTERVAL);
                    CallScheduler CS = new CallScheduler(ctx, interval, repeats, interval,
                            name, number, photoUri);
                    CS.schedule();
                }

                finish();

            }
        });

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        // findViewById(R.id.acceptButton).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}