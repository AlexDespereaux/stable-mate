package com.jaram.jarambuild;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

public class AboutUsActivity extends AppCompatActivity
{

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.bar_menu, menu);
        return true;
    }

    public void goHome()
    {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
