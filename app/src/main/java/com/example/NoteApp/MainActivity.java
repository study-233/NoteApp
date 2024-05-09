package com.example.NoteApp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NoteApp.adapter.MyAdapter;
import com.example.NoteApp.bean.Note;
import com.example.NoteApp.util.SpfUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private FloatingActionButton mBtnAdd;
    private List<Note> mNotes;
    private MyAdapter mMyAdapter;

    private NoteDbOpenHelper mNoteDbOpenHelper;

    public static final int MODE_LINEAR = 0;
    public static final int MODE_GRID = 1;

    public static final String KEY_LAYOUT_MODE = "key_layout_mode";


    private int currentListLayoutMode = MODE_LINEAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDataFromDb();
        setListLayout();
    }

    private void setListLayout() {
        currentListLayoutMode = SpfUtil.getIntWithDefault(this, KEY_LAYOUT_MODE, MODE_LINEAR);
        if (currentListLayoutMode == MODE_LINEAR) {
            setToLinearList();
        }else{
            setToGridList();
        }
    }

    private void refreshDataFromDb() {
        mNotes = getDataFromDB();
        mMyAdapter.refreshData(mNotes);
    }

    private void initEvent() {
        mMyAdapter = new MyAdapter(this, mNotes);
        mRecyclerView.setAdapter(mMyAdapter);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(linearLayoutManager);
//        mMyAdapter.setViewType(MyAdapter.TYPE_LINEAR_LAYOUT);
        setListLayout();

    }

    private void initData() {
        mNotes = new ArrayList<>();
        mNoteDbOpenHelper = new NoteDbOpenHelper(this);
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

    private List<Note> getDataFromDB() {
       return mNoteDbOpenHelper.queryAllFromDb();
    }

    @SuppressLint("SimpleDateFormat")
    private String getCurrentTimeFormat() {
        SimpleDateFormat simpleDateFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        }
        Date date = new Date();
        if (simpleDateFormat != null) {
            return simpleDateFormat.format(date);
        }
        return null;
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.rlv);
    }


    public void add(View view) {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        return super.onCreateOptionsMenu(menu);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);

        switch (item.getItemId()) {

            case R.id.menu_linear:
                setToLinearList();
                currentListLayoutMode = MODE_LINEAR;
                SpfUtil.saveInt(this,KEY_LAYOUT_MODE,MODE_LINEAR);

                return true;
            case R.id.menu_grid:

                setToGridList();
                currentListLayoutMode = MODE_GRID;
                SpfUtil.saveInt(this,KEY_LAYOUT_MODE,MODE_GRID);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setToLinearList() {
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //分割线
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.custom_divider)));
        mRecyclerView.addItemDecoration(divider);

        mMyAdapter.setViewType(MyAdapter.TYPE_LINEAR_LAYOUT);
        mMyAdapter.notifyDataSetChanged();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void setToGridList() {
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        //分割线
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.custom_divider)));
        mRecyclerView.addItemDecoration(divider);

        mMyAdapter.setViewType(MyAdapter.TYPE_GRID_LAYOUT);
        mMyAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentListLayoutMode == MODE_LINEAR) {
            MenuItem item = menu.findItem(R.id.menu_linear);
            item.setChecked(true);
        } else {
            menu.findItem(R.id.menu_grid).setChecked(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

}