package com.example.smartdispenser.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentPagerAdapter extends FragmentStateAdapter {
    List<Fragment> fragmentlist = new ArrayList<>();

    public FragmentPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle, List<Fragment> fragments) {
        super(fragmentManager, lifecycle);
        fragmentlist = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentlist.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentlist.size();
    }
}
