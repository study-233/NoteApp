package com.example.NoteApp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainLikeFragment extends Fragment {


    public static Fragment newInstance() {
        return new MainLikeFragment();
    }


    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fm_like,container, false);
        return view;
    }
}
