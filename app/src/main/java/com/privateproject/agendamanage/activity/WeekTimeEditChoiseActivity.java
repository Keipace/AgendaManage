package com.privateproject.agendamanage.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.adapter.WeekTimeAdapter;
import com.privateproject.agendamanage.bean.Course;
import com.privateproject.agendamanage.db.CourseDao;
import com.privateproject.agendamanage.server.WeekTimeAddServer;
import com.privateproject.agendamanage.viewHolder.WeekTimeViewHolder;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class WeekTimeEditChoiseActivity extends AppCompatActivity {
    public static final int RESULTCODE_WEEKTIMEEDIT = 1;

    private RecyclerView recyclerView;
    private Button addBtn;
    private List<Course> courseList;
    private CourseDao courseDao;
    private int posi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_week_time_edit_choise_layout);


        recyclerView = findViewById(R.id.item_weekTimeEditChoise_recyclerView);
        recyclerView.setHasFixedSize(true);
        courseDao = new CourseDao(this);
        addBtn = findViewById(R.id.item_weekTimeEditChoise_add_btn);

        Intent intent = getIntent();
        posi = intent.getIntExtra("posi", -1);
        courseList = courseDao.selectAll(posi);
        setResult(RESULTCODE_WEEKTIMEEDIT, intent);

        MyAdapter adapter = new MyAdapter();
        adapter.setInnerAdapter(adapter);
        recyclerView.setAdapter(adapter);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(WeekTimeEditChoiseActivity.this).inflate(R.layout.item_week_time_add_layout, null);
                EditText courseNameEt = view.findViewById(R.id.item_WeekTimeAdd_courseName_et);
                EditText addressEt = view.findViewById(R.id.item_WeekTimeAdd_address_et);
                AlertDialog.Builder builder = new AlertDialog.Builder(WeekTimeEditChoiseActivity.this);
                builder.setTitle("添加每周事件")
                        .setView(view)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //获得相应的控件
                                //将新的Course添加至数据库
                                String courseName = courseNameEt.getText().toString();
                                String address = addressEt.getText().toString();
                                WeekTimeAddServer weekTimeAddServer = new WeekTimeAddServer(WeekTimeEditChoiseActivity.this);
                                weekTimeAddServer.addCoures(courseDao, posi, courseName, address);
                                //更新courseList内容
                                courseList = courseDao.selectAll(posi);
                            }

                        })
                        .create().show();
            }
        });
    }


    class CourseViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout rootView;
        TextView nameTv;
        TextView addressTv;
        Button deleteBtn;
        Button editBtn;

        public CourseViewHolder(View itemView) {
            super(itemView);
            this.rootView = itemView.findViewById(R.id.item_weekTimeCourse_rootView_layout);
            this.nameTv = itemView.findViewById(R.id.item_weekTimeCourse_courseName_tv);
            this.addressTv = itemView.findViewById(R.id.item_weekTimeCourse_address_tv);
            this.deleteBtn = itemView.findViewById(R.id.item_weekTimeCourse_delete_btn);
            this.editBtn = itemView.findViewById(R.id.item_weekTimeCourse_edit_btn);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<CourseViewHolder> {
        private RecyclerView.Adapter innerAdapter;

        public void setInnerAdapter(RecyclerView.Adapter innerAdapter) {
            this.innerAdapter = innerAdapter;
        }

        @Override
        public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //此处一定要加parent
            View view = LayoutInflater.from(WeekTimeEditChoiseActivity.this).inflate(R.layout.item_week_time_course_list, parent, false);
            return new CourseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
            holder.nameTv.setText(courseList.get(position).getClassname());
            holder.addressTv.setText(courseList.get(position).getAddress());

            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    courseDao.deleteCourseById(courseList.get(position));
                    courseList = courseDao.selectAll(posi);
                    innerAdapter.notifyDataSetChanged();
                }
            });
            holder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = LayoutInflater.from(WeekTimeEditChoiseActivity.this).inflate(R.layout.item_week_time_add_layout, null);
                    EditText courseNameEt = view.findViewById(R.id.item_WeekTimeAdd_courseName_et);
                    courseNameEt.setText(courseList.get(position).getClassname());
                    EditText addressEt = view.findViewById(R.id.item_WeekTimeAdd_address_et);
                    addressEt.setText(courseList.get(position).getAddress());
                    AlertDialog.Builder builder = new AlertDialog.Builder(WeekTimeEditChoiseActivity.this);
                    builder.setTitle("编辑每周事件")
                            .setView(view)
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //获得相应的控件
                                    //将新的Course添加至数据库
                                    String courseName = courseNameEt.getText().toString();
                                    String address = addressEt.getText().toString();
                                    WeekTimeAddServer weekTimeAddServer = new WeekTimeAddServer(WeekTimeEditChoiseActivity.this);
                                    weekTimeAddServer.updateCoures(courseDao, posi, courseName, address);
                                    //更新courseList内容
                                    courseList = courseDao.selectAll(posi);
                                }

                            })
                            .create().show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return courseList.size();
        }
    }

}