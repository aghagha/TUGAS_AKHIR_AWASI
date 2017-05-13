package com.aghagha.tagg;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.astuetz.PagerSlidingTabStrip;

public class MuridActivity extends AppCompatActivity {
    private static String userId;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;

    AntaraSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_murid);

        session = new AntaraSessionManager(getApplicationContext());

        if(!session.isLoggedIn()){
            session.logoutUser();
            finish();
        }
        Intent intent = getIntent();
        userId = intent.getStringExtra("id");

        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager)findViewById(R.id.vp_pager);
        pager.setOffscreenPageLimit(3);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        final String pages[]={"Beranda","Forum","Tugas","Laporan"};
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                toolbar.setTitle(pages[position]);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main_murid,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.profile:
                Intent intent = new Intent(MuridActivity.this, ProfilActivity.class);
                startActivity(intent);
                break;
            case R.id.ganti:
                finish();
                break;
            case R.id.logout:
                session.logoutUser();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class MyPagerAdapter extends GuruActivity.MyPagerAdapter implements PagerSlidingTabStrip.IconTabProvider{
        private static int NUM_ITEMS = 4;
        private int tabIcons[] = {R.drawable.home,R.drawable.chat,R.drawable.edit,R.drawable.edit};

        public MyPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // BERANDA
                    return FragmentBerandaMurid.newInstance();
                case 1: // CHAT
                    return FragmentPengumumanMurid.newInstance();
                case 2: // TUGAS
                    return TugasMuridFragment.newInstance();
                case 4: // CHAT
                    return FragmentBerandaMurid.newInstance();
                default:
                    return FragmentBerandaMurid.newInstance();
            }
        }

        @Override
        public int getPageIconResId(int position) {
            return tabIcons[position];
        }
    }
}
