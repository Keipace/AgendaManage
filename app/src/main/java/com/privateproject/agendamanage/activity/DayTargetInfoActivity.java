package com.privateproject.agendamanage.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.privateproject.agendamanage.MainActivity;
import com.privateproject.agendamanage.databinding.ActivityDayTargetInfoBinding;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.utils.ToastUtil;

public class DayTargetInfoActivity extends AppCompatActivity {
    private ActivityDayTargetInfoBinding pageBinding;
    private DayTargetDao dayTargetDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageBinding = ActivityDayTargetInfoBinding.inflate(LayoutInflater.from(this));
        setContentView(pageBinding.getRoot());
        dayTargetDao = new DayTargetDao(this);

        int id = getIntent().getIntExtra("id", -1);
        if (!setPageContent(id)) {
            Intent intent = new Intent(this, MainActivity.class);
            ToastUtil.newToast(this, "打开页面失败");
            startActivity(intent);
        }
    }

    // 根据id查询数据库并将查询到的数据填充到页面中
    public boolean setPageContent(int id) {
        if(id==-1)
            return false;
        return true;
    }
}