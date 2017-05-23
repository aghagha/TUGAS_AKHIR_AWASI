package com.aghagha.tagg;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aghagha.tagg.data.AntaraSessionManager;
import com.astuetz.PagerSlidingTabStrip;

public class MuridActivity extends AppCompatActivity {
    private static String userId;

    private LinearLayout mTabsLinearLayout;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    Intent intent;

    AntaraSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_murid);

        session = new AntaraSessionManager(getApplicationContext());

        if(!session.isLoggedIn()){
            session.logoutUser(MuridActivity.this);
//            finish();
        }
        intent = getIntent();
        userId = intent.getStringExtra("id");

        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        Drawable drawable = ContextCompat.getDrawable(this,R.drawable.ic_option);
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toolbar.setOverflowIcon(drawable);
        setSupportActionBar(toolbar);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager)findViewById(R.id.vp_pager);
        pager.setOffscreenPageLimit(3);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        final String pages[]={"Beranda","Forum","Tugas","Laporan"};
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
        getMenuInflater().inflate(R.menu.menu_main_murid,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.profile:
                Intent intent = new Intent(MuridActivity.this, ProfilActivity.class);
                intent.putExtra("idMurid","0");
                startActivity(intent);
                break;
            case R.id.profileMurid:
                Intent intentMurid = new Intent(MuridActivity.this, ProfilActivity.class);
                intentMurid.putExtra("idMurid",session.getKeyMuridId());
                startActivity(intentMurid);
                break;
            case R.id.ganti:
                finish();
                break;
            case R.id.logout:
                session.logoutUser(MuridActivity.this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyPagerAdapter extends GuruActivity.MyPagerAdapter implements PagerSlidingTabStrip.IconTabProvider{
        private int NUM_ITEMS = 4;
        private int tabIcons[] = {R.drawable.ic_home,R.drawable.ic_chats,R.drawable.ic_tugas,R.drawable.ic_laporan};

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
                    return FragmentBerandaMurid.newInstance(intent.getStringExtra("nama"),intent.getStringExtra("sekolah"),intent.getStringExtra("kelas"),intent.getStringExtra("gambar"));
                case 1: // CHAT
                    return FragmentPengumumanMurid.newInstance();
                case 2: // TUGAS
                    return TugasMuridFragment.newInstance();
                case 3: // CHAT
                    return LaporanFragment.newInstance();
                default:
                    return FragmentBerandaMurid.newInstance(intent.getStringExtra("nama"),intent.getStringExtra("sekolah"),intent.getStringExtra("kelas"),intent.getStringExtra("gambar"));
            }
        }

        @Override
        public int getPageIconResId(int position) {
            return tabIcons[position];
        }
    }
}
