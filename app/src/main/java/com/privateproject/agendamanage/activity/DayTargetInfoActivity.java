package com.privateproject.agendamanage.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.privateproject.agendamanage.MainActivity;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.databinding.ActivityDayTargetInfoBinding;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.server.InfoPageServer;
import com.privateproject.agendamanage.utils.ComponentUtil;
import com.privateproject.agendamanage.utils.DayTargetUtil;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.text.ParseException;

public class DayTargetInfoActivity extends AppCompatActivity {
    private InfoPageServer infoPageServer;
    private ActivityDayTargetInfoBinding pageBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageBinding = ActivityDayTargetInfoBinding.inflate(LayoutInflater.from(this));
        setContentView(pageBinding.getRoot());
        this.infoPageServer = new InfoPageServer(this,pageBinding);

        int id = getIntent().getIntExtra("id", -1);
        if (!infoPageServer.setDayTargetInfoPageContent(id)) {
            Intent intent = new Intent(this, MainActivity.class);
            ToastUtil.newToast(this, "打开页面失败");
            startActivity(intent);
        }
        infoPageServer.inputDayEditable(false);
    }



}