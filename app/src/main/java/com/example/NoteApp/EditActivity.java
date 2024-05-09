package com.example.NoteApp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.NoteApp.bean.Note;
import com.example.NoteApp.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private Note note;
    private EditText etTitle,etContent;

    private NoteDbOpenHelper mNoteDbOpenHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);

        initData();

    }

    private void initData() {
        Intent intent = getIntent();
        note = (Note) intent.getSerializableExtra("note");
        if (note != null) {
            etTitle.setText(note.getTitle());
            etContent.setText(note.getContent());
        }
        mNoteDbOpenHelper = new NoteDbOpenHelper(this);
    }

    public void save(View view) {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(title)) {
            ToastUtil.toastShort(this, "标题不能为空！");
            return;
        }

        note.setTitle(title);
        note.setContent(content);
        note.setCreatedTime(getCurrentTimeFormat());
        long rowId = mNoteDbOpenHelper.updateData(note);
        if (rowId != -1) {
            ToastUtil.toastShort(this, "修改成功！");
            this.finish();
        }else{
            ToastUtil.toastShort(this, "修改失败！");
        }

    }

    @SuppressLint({"SimpleDateFormat", "WeekBasedYear"})
    private String getCurrentTimeFormat() {
        SimpleDateFormat sdf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        }
        Date date = new Date();
        if (sdf != null) {
            return sdf.format(date);
        }
        return null;
    }
}