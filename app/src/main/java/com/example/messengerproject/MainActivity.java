package com.example.messengerproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_CODE = "Main";
    private ActionBarDrawerToggle drawerToggle;

    // Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Elements
    private DrawerLayout mDrawer;
    private AppBarLayout mAppBar;
    private SearchView mSearchView;
    private ImageView mMainMenuButton;
    private RecyclerView mRecycleView;
    private NavigationView mNavigationView;
    private View mHeaderMenu;
    private ImageView mProfileImage;
    private TextView mProfileName;
    private TabLayout mTabLayout;

    // Drawer menu
    private int mCreateGroup;
    private int mContacts;
    private int mSettings;
    private int mAbout;
    private int mExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        mProfileName.setText(mAuth.getCurrentUser().getPhoneNumber());

        mMainMenuButton.setOnClickListener(view -> {
            mDrawer.open();
        });

        mNavigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == mCreateGroup) {

            } else if (item.getItemId() == mContacts) {

            } else if (item.getItemId() == mSettings) {

            } else if (item.getItemId() == mAbout) {

            } else if (item.getItemId() == mExit) {
                mAuth.signOut();
                startActivity(new Intent(this, AuthActivity.class));
            }

            return true;
        });
    }

    private void init() {
        // Elements
        mDrawer = findViewById(R.id.a_main_drawer);
        mAppBar = findViewById(R.id.a_main_app_bar);
        mSearchView = findViewById(R.id.a_main_search_view);
        mMainMenuButton = findViewById(R.id.a_main_menu);
        mRecycleView = findViewById(R.id.a_main_recycle_view);
        mNavigationView = findViewById(R.id.a_main_navigation_view);

        // Tab
        mTabLayout = findViewById(R.id.a_main_tab);

        // Drawer header
        mHeaderMenu = mNavigationView.getHeaderView(0);
        mProfileImage = mHeaderMenu.findViewById(R.id.header_menu_image);
        mProfileName = mHeaderMenu.findViewById(R.id.header_menu_name);

        // Drawer items
        mCreateGroup = R.id.m_drawer_create_group;
        mContacts = R.id.m_drawer_contacts;
        mSettings = R.id.m_drawer_settings;
        mAbout = R.id.m_drawer_about;
        mExit = R.id.m_drawer_exit;
    }
}