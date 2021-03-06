package com.example.messengerproject.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.messengerproject.R;
import com.example.messengerproject.adapters.MainMenuViewPagerAdapter;
import com.example.messengerproject.fragments.EditNameFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_CODE = "Main";
    public static final int NICKNAME_MAX_SIZE = 20;

    // Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    // Elements
    private DrawerLayout mDrawer;
    private AppBarLayout mAppBar;
    private SearchView mSearchView;
    private ImageView mMainMenuButton;
    private NavigationView mNavigationView;
    private View mHeaderMenu;
    private ImageView mProfileImage;
    private TextView mProfileName;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;

    // View pager
    private ViewPager2 mViewPager;
    private FragmentStateAdapter fragmentStateAdapter;

    // Drawer menu
    private int mContacts;
    private int mSettings;
    private int mAbout;
    private int mExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        mFirebaseDatabase.getReference("Users/" + mAuth.getCurrentUser().getPhoneNumber() + "/Name")
                .get()
                .addOnSuccessListener(runnable -> {
                    if (runnable == null || runnable.getValue().toString().equals("")) {
                        mProfileName.setText(mAuth.getCurrentUser().getPhoneNumber());
                        return;
                    }
                    mProfileName.setText(runnable.getValue().toString());
        });

        mToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.m_main_add_conversation) {
                startActivity(new Intent(this, AddConversationActivity.class));
            }

            return false;
        });

        mMainMenuButton.setOnClickListener(view -> {
            mDrawer.open();
        });

        mNavigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == mContacts) {
                startActivity(new Intent(this, ContactsActivity.class));
            } else if (item.getItemId() == mSettings) {

            } else if (item.getItemId() == mAbout) {

            } else if (item.getItemId() == mExit) {
                mAuth.signOut();
                startActivity(new Intent(this, AuthActivity.class));
            }

            return true;
        });

        mProfileName.setOnClickListener(view -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            EditNameFragment editNameFragment = new EditNameFragment(this, mProfileName);
            fragmentTransaction.add(editNameFragment, "");
            fragmentTransaction.commit();
        });


    }

    private void init() {
        // Elements
        mDrawer = findViewById(R.id.a_main_drawer);
        mAppBar = findViewById(R.id.a_main_app_bar);
        mSearchView = findViewById(R.id.a_main_search_view);
        mMainMenuButton = findViewById(R.id.a_main_menu);
        mNavigationView = findViewById(R.id.a_main_navigation_view);
        mToolbar = findViewById(R.id.a_main_tool_bar);

        // Tab
        mTabLayout = findViewById(R.id.a_main_tab);

        // View pager
        mViewPager = findViewById(R.id.a_main_view_pager);
        fragmentStateAdapter = new MainMenuViewPagerAdapter(this);
        mViewPager.setAdapter(fragmentStateAdapter);
        new TabLayoutMediator(mTabLayout, mViewPager, (tab, position) -> {
            tab.setId(position);
            if (position == 0) {
                tab.setText("??????");
            } else if (position == 1) {
                tab.setText("??????????????");
            } else if (position == 2) {
                tab.setText("????????????");
            }
        }).attach();

        // Drawer header
        mHeaderMenu = mNavigationView.getHeaderView(0);
        mProfileImage = mHeaderMenu.findViewById(R.id.header_menu_image);
        mProfileName = mHeaderMenu.findViewById(R.id.header_menu_name);

        // Drawer items
        mContacts = R.id.m_drawer_contacts;
        mSettings = R.id.m_drawer_settings;
        mAbout = R.id.m_drawer_about;
        mExit = R.id.m_drawer_exit;
    }
}