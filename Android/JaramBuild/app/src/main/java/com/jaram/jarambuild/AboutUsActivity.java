package com.jaram.jarambuild;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jaram.jarambuild.utils.TinyDB;

public class AboutUsActivity extends AppCompatActivity
{
    String TAG = "About";
    //user
    TinyDB tinydb;
    String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        //home button in action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        if (toolbar != null) {
            toolbar.setLogo(R.drawable.my_logo_shadow_96px);

            //Listener for item selection change
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goHome();
                }
            });
        }

        tinydb = new TinyDB(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.bar_menu, menu);
        return true;
    }

    //custom menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.settingsMenuBtn)
        {
            Log.d(TAG, "Settings Btn Clicked");
            //Go to settings activity
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (item.getItemId() == R.id.helpMenuBtn)
        {
            Log.d(TAG, "Help Btn Clicked");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://docs.google.com/document/d/1CFCF-80XOzv55uB1acoBkKkKK8FgZzDq0q24luXXdzI/edit?usp=sharing"));
            startActivity(browserIntent);

        } else if (item.getItemId() == R.id.logoutMenuBtn)
        {
            Log.d(TAG, "Logout Btn Clicked");
            //set logged in user to null
            tinydb.putString("loggedInAccount", "");
            //return to Login Page
            Intent settingsIntent = new Intent(this, MainActivity.class);
            startActivity(settingsIntent);
            finish();
        }
        else if (item.getItemId() == R.id.aboutBtn)
        {
            Log.d(TAG, "About Btn Clicked");
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void goHome()
    {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
