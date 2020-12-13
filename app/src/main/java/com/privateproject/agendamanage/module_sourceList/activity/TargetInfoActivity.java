package com.privateproject.agendamanage.module_sourceList.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.privateproject.agendamanage.databinding.SourcelistActivityTargetInfoBinding;
import com.privateproject.agendamanage.module_sourceList.server.InfoPageServer;
import com.privateproject.agendamanage.utils.ToastUtil;

public class TargetInfoActivity extends AppCompatActivity {
    private SourcelistActivityTargetInfoBinding pageBinding;
    private InfoPageServer infoPageServer;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageBinding = SourcelistActivityTargetInfoBinding.inflate(LayoutInflater.from(this));
        setContentView(pageBinding.getRoot());
        this.infoPageServer = new InfoPageServer(this,pageBinding);
        //预估所需时间添加监听器
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
        infoPageServer.setTargetInfoPageContent(this.id);
    }
}