package com.privateproject.agendamanage.module_sourceList.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.privateproject.agendamanage.databinding.SourcelistActivityDayTargetInfoBinding;
import com.privateproject.agendamanage.module_sourceList.server.InfoPageServer;
import com.privateproject.agendamanage.utils.ToastUtil;

public class DayTargetInfoActivity extends AppCompatActivity {
    private InfoPageServer infoPageServer;
    private SourcelistActivityDayTargetInfoBinding pageBinding;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageBinding = SourcelistActivityDayTargetInfoBinding.inflate(LayoutInflater.from(this));
        setContentView(pageBinding.getRoot());
        this.infoPageServer = new InfoPageServer(this,pageBinding);
        // 获取传送过来的id参数，默认值为-1（即传送失败时为-1）
        this.id = getIntent().getIntExtra("id", -1);
        if(id==-1) {// id传送失败
            ToastUtil.newToast(this, "打开页面失败");
            // 销毁activity
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 根据id来设置页面内容
        infoPageServer.setDayTargetInfoPageContent(this.id);
    }
}