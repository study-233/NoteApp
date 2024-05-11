package com.example.NoteApp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.example.NoteApp.MainHomeFragment;
import com.example.NoteApp.MainLikeFragment;
import com.example.NoteApp.MainNoteFragment;

import java.util.ArrayList;
import java.util.List;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public MyFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        mFragmentList.add(MainLikeFragment.newInstance());
        mFragmentList.add(MainNoteFragment.newInstance());
        mFragmentList.add(MainHomeFragment.newInstance());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
