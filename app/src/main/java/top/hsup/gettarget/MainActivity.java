package top.hsup.gettarget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.dialogs.BottomDialog;

import com.kongzue.dialogx.interfaces.DialogLifecycleCallback;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.liuzhenlin.simrv.SlidingItemMenuRecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import top.hsup.gettarget.adapter.RecyclerAdapter;
import top.hsup.gettarget.adapter.ToDoJobAdapter;
import top.hsup.gettarget.db.AppDatabase;
import top.hsup.gettarget.db.dao.ToDoJobDao;
import top.hsup.gettarget.db.model.ToDoJob;
import top.hsup.gettarget.util.Constants;

public class MainActivity extends AppCompatActivity {
    private String targetDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-M-dd", Locale.CHINESE));
    private AppDatabase db;
    private ToDoJobDao dao;
    private Handler handler;
    private EditText jobInput;
    private RecyclerAdapter jobAdapter;

    private String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-M-dd", Locale.CHINESE));
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        DialogX.init(this);
        final SlidingItemMenuRecyclerView simrv = findViewById(R.id.simrv);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomDialog.show("新增", "",
                        new OnBindView<BottomDialog>(R.layout.add_layout) {
                            @Override
                            public void onBind(BottomDialog dialog, View v) {
                                //v.findViewById...
                                jobInput = v.findViewById(R.id.new_job);
                            }
                        }).setOkButton("确认", new OnDialogButtonClickListener<BottomDialog>() {
                    @Override
                    public boolean onClick(BottomDialog baseDialog, View v) {
                        String title = jobInput.getText().toString();
                        if (!title.equals("")) {
                            ToDoJob job = new ToDoJob().setTitle(jobInput.getText().toString()).setCreateDate(targetDate).setUpdateTime(getCurrentDate());
                            new Thread() {
                                @Override
                                public void run() {
                                    dao.insertJob(job);
                                    jobAdapter.add(job);
                                    handler.sendEmptyMessage(0);
                                    simrv.setItemDraggable(true);
                                }
                            }.start();
                        }
                        return false;
                    }
                });
            }
        });
        CalendarView calendarView = findViewById(R.id.calendar_view);
        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                if (isClick) {
                    if (!(calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay()).equals(targetDate)) {
                        targetDate = calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay();
                        new Thread() {
                            @Override
                            public void run() {
                                List<ToDoJob> jobs = dao.getUnfinishedJobByCreateDate(targetDate);
                                jobAdapter.clear();
                                jobAdapter.addAll(jobs);
                                handler.sendEmptyMessage(0);
                                simrv.setItemDraggable(true);
                            }
                        }.start();
                    }
                }
            }
        });
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        jobAdapter.notifyDataSetChanged();
                    case 1:
                        Bundle bundle = msg.getData();
                        if (bundle != null) {
                            if (bundle.containsKey("function")) {
                                switch (bundle.getString("function")) {
                                    case "button_delete":
                                        new Thread() {
                                            public void run() {
                                                dao.updateJobStatus(bundle.getInt("id"), 1);
                                                jobAdapter.deleteByIndex(bundle.getInt("index"));
                                                sendEmptyMessage(0);
                                            }
                                        }.start();

                                }
                                Toast.makeText(MainActivity.this, bundle.getString("function") + ":" + bundle.getString("content"), Toast.LENGTH_SHORT).show();
                            }
                        }

                }
            }
        };
        simrv.setLayoutManager(new LinearLayoutManager(this));
        jobAdapter = new RecyclerAdapter(handler);
        simrv.setAdapter(jobAdapter);
        simrv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        new Thread() {
            @Override
            public void run() {
                db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, Constants.DB_NAME).build();
                dao = db.toDoJobDao();
                List<ToDoJob> jobs = dao.getUnfinishedJobByCreateDate(targetDate);
                jobAdapter.clear();
                if (jobs.size() > 0) {
                    jobAdapter.addAll(jobs);
                    handler.sendEmptyMessage(0);
                    simrv.setItemDraggable(true);
                }
            }
        }.start();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != db) {
            db.close();
        }
    }
}
