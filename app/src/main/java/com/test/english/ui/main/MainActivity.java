package com.test.english.ui.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.exam.english.R;
import com.exam.english.databinding.ActivityMainBinding;
import com.test.english.ui.adapter.MainViewPagerAdapter;
import com.test.english.ui.frag1.Fragment1;
import com.test.english.ui.frag2.Fragment2;
import com.test.english.ui.frag3.Fragment3;
import com.test.english.ui.frag4.Fragment4;
import com.test.english.ui.helper.BottomNavigationNotShiftHelper;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewPagerAdapter mainViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        BottomNavigationNotShiftHelper.disableShiftMode(binding.bottomNavigation);
        setupViewPager(binding.mainViewPager);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.frag1:
                        binding.mainViewPager.setCurrentItem(0);
                        return true;
                    case R.id.frag2:
                        binding.mainViewPager.setCurrentItem(1);
                        return true;
                    case R.id.frag3:
                        binding.mainViewPager.setCurrentItem(2);
                        return true;
                    case R.id.frag4:
                        binding.mainViewPager.setCurrentItem(3);
                        return true;
                }
                return false;
            }
        });

        binding.mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                binding.bottomNavigation.getMenu().getItem(position).setChecked(true);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setupViewPager(ViewPager viewPager) {
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());

        mainViewPagerAdapter.addFragment(Fragment1.newInstance());
        mainViewPagerAdapter.addFragment(Fragment2.newInstance());
        mainViewPagerAdapter.addFragment(Fragment3.newInstance());
        mainViewPagerAdapter.addFragment(Fragment4.newInstance());

        binding.mainViewPager.setAdapter(mainViewPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
