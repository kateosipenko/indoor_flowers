package com.indoor.flowers.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.evgeniysharafan.utils.Fragments;
import com.evgeniysharafan.utils.Toasts;
import com.indoor.flowers.R;
import com.indoor.flowers.fragment.FlowersFragment;
import com.indoor.flowers.fragment.GroupsFragment;
import com.indoor.flowers.util.FragmentCommunicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.and_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.and_nav_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.and_drawer_open, R.string.and_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.mnd_flowers);
            openNavigationItem(R.id.mnd_flowers);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (canGoBack()) {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        openNavigationItem(item.getItemId());
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openNavigationItem(@IdRes int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case R.id.mnd_flowers:
                fragment = FlowersFragment.newInstance();
                break;
            case R.id.mnd_groups:
                fragment = GroupsFragment.newInstance();
                break;
            default:
                Toasts.showFuture();
                break;
        }

        if (fragment != null) {
            Fragments.replace(getSupportFragmentManager(), R.id.and_content, fragment, null, false);
        }
    }

    private boolean canGoBack() {
        Fragment fragment = Fragments.getById(getSupportFragmentManager(), android.R.id.content);
        return !(fragment instanceof FragmentCommunicator && ((FragmentCommunicator) fragment).onBackPressed());
    }
}
