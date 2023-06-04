package com.santisj.foodfast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.santisj.foodfast.adapters.ViewPagerAdapter;
import com.santisj.foodfast.utils.SaveData;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPagerOnBoarding;
    LinearLayout dotLayout;

    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;

    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            );
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (firstTime()) {
            viewPagerOnBoarding = (ViewPager) findViewById(R.id.viewPager);
            dotLayout = (LinearLayout) findViewById(R.id.indicator_layout);
            nextButton = (Button) findViewById(R.id.btn_onBoarding_next);

            viewPagerAdapter = new ViewPagerAdapter(this);
            viewPagerOnBoarding.setAdapter(viewPagerAdapter);

            setUpIndicator(0);
            viewPagerOnBoarding.addOnPageChangeListener(viewListener);

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getItem(0) < 2) {
                        viewPagerOnBoarding.setCurrentItem(getItem(1), true);
                    } else {
                        Intent intent = new Intent(MainActivity.this, EnterActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        } else {
            Intent intent = new Intent(MainActivity.this, EnterActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean firstTime() {
        SharedPreferences sharedPreferences = getSharedPreferences(SaveData.FIRST_TIME, MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime", true);

        if (isFirstTime) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();
            return true;
        }
        return false;
    }

    public void setUpIndicator(int pos) {
        dots = new TextView[3];
        dotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(70);
            dots[i].setTextColor(getResources().getColor(R.color.dot_unselected,
                    getApplicationContext().getTheme()));
            dotLayout.addView(dots[i]);
        }

        dots[pos].setTextColor(getResources().getColor(R.color.dot_selected,
                getApplicationContext().getTheme()));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setUpIndicator(position);

            if (position >= 2) {
                nextButton.setText(R.string.txt_finish);
            } else {
                nextButton.setText(R.string.txt_next);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getItem(int i) {
        return viewPagerOnBoarding.getCurrentItem() + i;
    }
}