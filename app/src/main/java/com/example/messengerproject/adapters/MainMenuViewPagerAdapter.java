package com.example.messengerproject.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.messengerproject.fragments.AllConversationsFragment;
import com.example.messengerproject.fragments.DialogsFragment;
import com.example.messengerproject.fragments.GroupsFragment;

public class MainMenuViewPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 3;
    private static final String DEBUG_CODE = "MainMenuViewPagerAdapter";

    public MainMenuViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new AllConversationsFragment();
        } else if (position == 1) {
            return new DialogsFragment();
        } else if (position == 2) {
            return new GroupsFragment();
        }

        return new AllConversationsFragment();
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
