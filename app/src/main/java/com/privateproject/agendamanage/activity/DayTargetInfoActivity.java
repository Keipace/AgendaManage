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
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageBinding = ActivityDayTargetInfoBinding.inflate(LayoutInflater.from(this));
        setContentView(pageBinding.getRoot());
        this.infoPageServer = new InfoPageServer(this,pageBinding);
        // 获取传送过来的id参数，默认值为-1（即传送失败时为-1）
        this.id = getIntent().getIntExtra("id", -1);
        if(id==-1) {// id传送失败
            // 跳转回主页面
            Intent intent = new Intent(this, MainActivity.class);
            ToastUtil.newToast(this, "打开页面失败");
            startActivity(intent);
            // 销毁activity
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 根据id来设置页面内容
        infoPageServer.setDayTargetInfoPageContent(this.id);
        // 设置页面不可编辑状态
        infoPageServer.inputDayEditable(false);
    }
}