package com.example.NoteApp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NoteApp.adapter.MyAdapter;
import com.example.NoteApp.bean.Note;
import com.example.NoteApp.util.SpfUtil;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainNoteFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private MyAdapter mMyAdapter;
    private NoteDbOpenHelper mNoteDbOpenHelper;

    private List<Note> mNotes;


    public static final int MODE_LINEAR = 0;
    public static final int MODE_GRID = 1;

    public static final String KEY_LAYOUT_MODE = "key_layout_mode";


    private int currentListLayoutMode = MODE_LINEAR;
    // Note: No argument constructor.
    public MainNoteFragment() {  }

    public static Fragment newInstance() {
        return new MainNoteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_note, container, false);

        // Initialize your UI components here. Don't initialize in onCreate() anymore.
        mRecyclerView = view.findViewById(R.id.rlv);

        // button click event
        Button addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);
            }
        });
        // other initialization logic
        PullToRefreshView mPullToRefreshView = view.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(() -> {
            long REFRESH_DELAY = 700;
            mPullToRefreshView.postDelayed(() -> mPullToRefreshView.setRefreshing(false), REFRESH_DELAY);
        });
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initEvent();
        refreshDataFromDb();
        setListLayout();

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDataFromDb();
        setListLayout();
    }

    // Other functions, replace all `getActivity()` with `getActivity()`
    // ...........
    private void initEvent() {
        mMyAdapter = new MyAdapter(getActivity(), mNotes);
        mRecyclerView.setAdapter(mMyAdapter);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        mRecyclerView.setLayoutManager(linearLayoutManager);
//        mMyAdapter.setViewType(MyAdapter.TYPE_LINEAR_LAYOUT);
        setListLayout();

    }
    private void initData() {
        mNotes = new ArrayList<>();
        mNoteDbOpenHelper = new NoteDbOpenHelper(getActivity());
//
//        for (int i = 0; i < 30; i++) {
//            Note note = new Note();
//            note.setTitle("这是标题"+i);
//            note.setContent("这是内容"+i);
//            note.setCreatedTime(getCurrentTimeFormat());
//            mNotes.add(note);
//        }

//        mNotes = getDataFromDB();

    }

    private void refreshDataFromDb() {
        mNotes = getDataFromDB();
        mMyAdapter.refreshData(mNotes);
    }

    private void setListLayout() {
        currentListLayoutMode = SpfUtil.getIntWithDefault(getActivity(), KEY_LAYOUT_MODE, MODE_LINEAR);
        if (currentListLayoutMode == MODE_LINEAR) {
            setToLinearList();
        }else{
            setToGridList();
        }
    }

    private List<Note> getDataFromDB() {
        return mNoteDbOpenHelper.queryAllFromDb();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void setToLinearList() {
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //分割线
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getActivity(), R.drawable.custom_divider)));
        mRecyclerView.addItemDecoration(divider);

        mMyAdapter.setViewType(MyAdapter.TYPE_LINEAR_LAYOUT);
        mMyAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setToGridList() {
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        //分割线
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getActivity(), R.drawable.custom_divider)));
        mRecyclerView.addItemDecoration(divider);

        mMyAdapter.setViewType(MyAdapter.TYPE_GRID_LAYOUT);
        mMyAdapter.notifyDataSetChanged();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        androidx.appcompat.widget.SearchView searchView =
                (androidx.appcompat.widget.SearchView) menu.findItem(R.id.menu_search).getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mNotes = mNoteDbOpenHelper.queryFromDbByTitle(newText);
                    mMyAdapter.refreshData(mNotes);
                    return true;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);

        switch (item.getItemId()) {

            case R.id.menu_linear:
                setToLinearList();
                currentListLayoutMode = MODE_LINEAR;
                SpfUtil.saveInt(getActivity(),KEY_LAYOUT_MODE,MODE_LINEAR);

                return true;
            case R.id.menu_grid:

                setToGridList();
                currentListLayoutMode = MODE_GRID;
                SpfUtil.saveInt(getActivity(),KEY_LAYOUT_MODE,MODE_GRID);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentListLayoutMode == MODE_LINEAR) {
            MenuItem item = menu.findItem(R.id.menu_linear);
            item.setChecked(true);
        } else {
            MenuItem item = menu.findItem(R.id.menu_grid);
            item.setChecked(true);
        }
    }
}
