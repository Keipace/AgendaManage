package com.privateproject.agendamanage.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.bean.Course;
import com.privateproject.agendamanage.db.CourseDao;

import java.util.List;

public class WeekTimeEditChoiseActivity extends AppCompatActivity {
    public static final int RESULTCODE_WEEKTIMEEDIT = 1;

    private CourseDao courseDao;
    private List<Course> courseList;
    private int row, col;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_week_time_edit_choise_layout);
        courseDao = new CourseDao(this);

        Intent intent = getIntent();
        setResult(RESULTCODE_WEEKTIMEEDIT, intent);
        row = intent.getIntExtra("row", -1);
        col = intent.getIntExtra("col", -1);
        if (row==-1 || col==-1) {
            finish();
        }

        courseList = courseDao.selectByPosition(row, col);
        RecyclerView recyclerView = findViewById(R.id.item_weekTimeEditChoise_recyclerView);
        Button addBtn = findViewById(R.id.item_weekTimeEditChoise_add_btn);

        // 设置list列表
        recyclerView.setHasFixedSize(true);
        RecyclerView.Adapter adapter = new RecyclerView.Adapter<CourseViewHolder>() {
            @NonNull
            @Override
            public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(WeekTimeEditChoiseActivity.this).inflate(R.layout.item_week_time_course_list, parent, false);
                return new CourseViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
                holder.nameTv.setText(courseList.get(position).getClassname());
                holder.addressTv.setText(courseList.get(position).getAddress());

                //删除事件
                holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        courseDao.deleteCourseById(courseList.get(position));
                        courseList = courseDao.selectByPosition(row, col);
                        notifyDataSetChanged();
                    }
                });

                //编辑事件
                holder.editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(false, position, new InnerListener() {
                            @Override
                            public void innnerAction() {
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
            }

            @Override
            public int getItemCount() {
                return courseList.size();
            }
        };
        recyclerView.setAdapter(adapter);

        // 设置添加按钮监听器
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(true, -1, new InnerListener() {
                    @Override
                    public void innnerAction() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void showDialog(boolean isAdd, int position, InnerListener innerListener) {
        View view = LayoutInflater.from(WeekTimeEditChoiseActivity.this).inflate(R.layout.item_week_time_add_layout, null);
        EditText courseNameEt = view.findViewById(R.id.item_WeekTimeAdd_courseName_et);
        EditText addressEt = view.findViewById(R.id.item_WeekTimeAdd_address_et);
        AlertDialog.Builder builder = new AlertDialog.Builder(WeekTimeEditChoiseActivity.this);
        builder.setView(view)
                .setNegativeButton("取消",null);
        if (isAdd) {
            builder.setTitle("添加每周事件")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //获得相应的控件
                            //将新的Course添加至数据库
                            String courseName = courseNameEt.getText().toString();
                            String address = addressEt.getText().toString();
                            courseDao.addCourse(new Course(courseName, address, row, col));
                            //更新courseList内容
                            courseList = courseDao.selectByPosition(row, col);
                            innerListener.innnerAction();
                        }
                    });
        } else {
            Course course = courseList.get(position);
            courseNameEt.setText(course.getClassname());
            addressEt.setText(course.getAddress());
            builder.setTitle("编辑每周事件")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //获得相应的控件
                            //将新的Course添加至数据库
                            String courseName = courseNameEt.getText().toString();
                            String address = addressEt.getText().toString();
                            course.setClassname(courseName);
                            course.setAddress(address);
                            courseDao.updateCourse(course);
                            //更新courseList内容
                            courseList = courseDao.selectByPosition(row, col);
                            innerListener.innnerAction();
                        }

                    });
        }
        builder.create().show();
    }

    private interface InnerListener {
        public void innnerAction();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
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

}