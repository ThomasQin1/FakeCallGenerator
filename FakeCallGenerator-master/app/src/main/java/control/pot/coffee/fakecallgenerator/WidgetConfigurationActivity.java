package control.pot.coffee.fakecallgenerator;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Spinner;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class WidgetConfigurationActivity extends AppCompatActivity implements
    ContactsListFragment.ContactsListFragmentInterface,
    AdapterView.OnItemSelectedListener {
        private static final String TAG = "ConfigActivity";
        private int mAppWidgetId;

        private EditText searchEditText;
        private String searchString;

        private String name;
        private String number;
        private String photoStr;

        //Variable and constants to track fragment states
        private String fragState  = STATE_EMPTY;
        private static final String STATE_EMPTY     = "empty";      //No fragment displayed
        private static final String STATE_LIST      = "list";       //ContactsListFragment displayed
        private static final String STATE_DISPLAY   = "display";    //ContactDisplayFragment displayed

        private int repeat;
        private int delay;
        private int interval;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_widget_configuration);

            Spinner repeat_spinner = (Spinner) findViewById(R.id.repeat_spinner);
            ArrayAdapter<CharSequence> r_adapter = ArrayAdapter.createFromResource(this,
                    R.array.repeat_array, android.R.layout.simple_spinner_item);
            r_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            repeat_spinner.setAdapter(r_adapter);

            Spinner delay_spinner = (Spinner) findViewById(R.id.delay_spinner);
            ArrayAdapter<CharSequence> d_adapter = ArrayAdapter.createFromResource(this,
                    R.array.delay_array, android.R.layout.simple_spinner_item);
            d_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            delay_spinner.setAdapter(d_adapter);

            Spinner interval_spinner = (Spinner) findViewById(R.id.interval_spinner);
            ArrayAdapter<CharSequence> i_adapter = ArrayAdapter.createFromResource(this,
                    R.array.interval_array, android.R.layout.simple_spinner_item);
            i_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            interval_spinner.setAdapter(i_adapter);

            searchEditText = (EditText)findViewById(R.id.search_field);
            repeat_spinner.setOnItemSelectedListener(this);
            delay_spinner.setOnItemSelectedListener(this);
            interval_spinner.setOnItemSelectedListener(this);

        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            switch(parent.getId()) {
                case R.id.repeat_spinner: repeat = Integer.parseInt(parent.getItemAtPosition(pos).toString());
                    break;
                case R.id.delay_spinner: delay = Integer.parseInt(parent.getItemAtPosition(pos).toString());
                    break;
                case R.id.interval_spinner: interval = Integer.parseInt(parent.getItemAtPosition(pos).toString());
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Nothing needs to be done
        }

        //called when contact is pressed in ContactsListFragment
    public void onContactClicked(String lookupKey)  {
        SharedPreferences sharedPrefs = getPreferences(0);
        name   = sharedPrefs.getString(Constants.PREFS_KEY_CONTACT_MAIN_NAME, null);
        number = sharedPrefs.getString(Constants.PREFS_KEY_CONTACT_MAIN_NUMBER, null);
        photoStr = sharedPrefs.getString(Constants.PREFS_KEY_CONTACT_MAIN_PHOTO, null);

        ContactDisplayFragment fragmentDisplay =
                ContactDisplayFragment.newInstance(name, number, photoStr);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (fragState) {
            case STATE_LIST:
            case STATE_DISPLAY:
                fragmentTransaction.replace(R.id.result_layout, fragmentDisplay);
                break;
            case STATE_EMPTY:
            default:
                fragmentTransaction.add(R.id.result_layout, fragmentDisplay);
                break;
        }
        fragmentTransaction.commit();
        fragState = STATE_DISPLAY;
    }

    //Called when search button is pressed
    public void searchContacts(View view) {
        updateSearchString();
        ContactsListFragment fragmentList = ContactsListFragment.newInstance(searchString);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (fragState) {
            case STATE_LIST:
            case STATE_DISPLAY:
                fragmentTransaction.replace(R.id.result_layout, fragmentList);
                break;
            case STATE_EMPTY:
            default:
                fragmentTransaction.add(R.id.result_layout, fragmentList);
                break;
        }
        fragmentTransaction.commit();
        fragState = STATE_LIST;
    }

    //Called when call button is pressed
    //Finishes configuration
    public void placeCall(View view) {
        Log.v(TAG, "Configure Widget has been called");
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        String spc = " | ";
        Log.v(TAG, name + spc + number + spc + photoStr + spc + delay + spc + interval + spc + repeat);

        SharedPreferences sharedPrefs = this.getSharedPreferences(Constants.PREFS_WIDGET_NAME, 0);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String id = Integer.toString(mAppWidgetId);

        editor.putString(Constants.PREFS_WIDGET_ID, id);
        editor.putString(Constants.PREFS_WIDGET_NAME(id), name);
        editor.putString(Constants.PREFS_WIDGET_NUMBER(id), number);
        editor.putString(Constants.PREFS_WIDGET_PHOTO(id), photoStr);
        editor.putInt(Constants.PREFS_WIDGET_DELAY(id), delay);
        editor.putInt(Constants.PREFS_WIDGET_INTERVAL(id), interval);
        editor.putInt(Constants.PREFS_WIDGET_REPEATS(id), repeat);
        editor.commit();


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews views = new RemoteViews(this.getPackageName(),
                R.layout.widget_layout);
        appWidgetManager.updateAppWidget(mAppWidgetId, views);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private void updateSearchString()   {
        searchString = searchEditText.getText().toString().trim();
    }
}
