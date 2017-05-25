package com.aghagha.tagg;

import android.content.Context;
import android.content.Intent;
import android.gesture.GestureUtils;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.astuetz.PagerSlidingTabStrip;

public class GuruActivity extends AppCompatActivity {
    public Context globalContext = null;

    private LinearLayout mTabsLinearLayout;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;

    AntaraSessionManager session;
    FragmentPagerAdapter adapterViewPager;
    ViewPager.OnPageChangeListener operation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalContext = this.getApplication();
        setContentView(R.layout.activity_guru);
        session = new AntaraSessionManager(getApplicationContext());

        if(!session.isLoggedIn()){
            session.logoutUser(GuruActivity.this);
//            finish();
        }

        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        Drawable drawable = ContextCompat.getDrawable(this,R.drawable.ic_option);
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toolbar.setOverflowIcon(drawable);
        setSupportActionBar(toolbar);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager)findViewById(R.id.vp_pager);
        pager.setOffscreenPageLimit(2);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        final String pages[]={"Beranda","Forum","Tugas"};

        pager.setAdapter(adapter);
        tabs.setViewPager(pager);

        setUpTabs();

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                toolbar.setTitle(pages[position]);
                for(int i = 0; i< mTabsLinearLayout.getChildCount(); i++){
                    ImageButton tv = (ImageButton) mTabsLinearLayout.getChildAt(i);
                    if(i==position){
                        tv.setColorFilter(Color.WHITE);
                    } else {
                        tv.setColorFilter(ContextCompat.getColor(getBaseContext(),R.color.colorSekunder));
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

    }

    private void setUpTabs() {
        mTabsLinearLayout = (LinearLayout)tabs.getChildAt(0);
        for(int i = 0; i< mTabsLinearLayout.getChildCount(); i++){
            ImageButton tv = (ImageButton) mTabsLinearLayout.getChildAt(i);
            if(i==0){
                tv.setColorFilter(Color.WHITE);
            } else {
                tv.setColorFilter(ContextCompat.getColor(getBaseContext(),R.color.colorSekunder));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main_guru,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.profile:
                Intent intent = new Intent(GuruActivity.this, ProfilActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                session.logoutUser(GuruActivity.this);
//                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Tekan Kembali sekali lagi untuk keluar",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider{
        private static int NUM_ITEMS = 3;
        private int tabIcons[] = {R.drawable.ic_home,R.drawable.ic_chats,R.drawable.ic_tugas};

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // BERANDA
                    return FragmentBerandaGuru.newInstance(0, "Page # 1");
                case 1: // CHAT
                    return FragmentPengumumanGuru.newInstance();
                case 2: // TUGAS
                    return FragmentTugasGuru.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getPageIconResId(int position) {
            return tabIcons[position];
        }
    }
}
