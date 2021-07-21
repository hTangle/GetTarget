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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
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

import top.hsup.gettarget.adapter.ToDoJobAdapter;
import top.hsup.gettarget.db.AppDatabase;
import top.hsup.gettarget.db.dao.ToDoJobDao;
import top.hsup.gettarget.db.model.ToDoJob;

public class MainActivity extends AppCompatActivity {
    private List<ToDoJob> todoItems = new ArrayList<>();
    private String targetDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-M-dd", Locale.CHINESE));
    private AppDatabase db;
    private ToDoJobDao dao;
    private Handler handler;
    private EditText jobInput;

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
        final RecyclerAdapter jobAdapter=new RecyclerAdapter();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomDialog.show("新增", "",
                        new OnBindView<BottomDialog>(R.layout.add_layout) {
                            @Override
                            public void onBind(BottomDialog dialog, View v) {
                                //v.findViewById...
                                if (jobInput == null) {
                                    jobInput = v.findViewById(R.id.new_job);
                                }
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
                                    todoItems.add(job);
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
                                List<ToDoJob> jobs = dao.getJobByCreateDate(targetDate);
                                todoItems.clear();
                                todoItems.addAll(jobs);
                                handler.sendEmptyMessage(0);
                                simrv.setItemDraggable(true);
                            }
                        }.start();
                    }
                }
            }
        });

        simrv.setLayoutManager(new LinearLayoutManager(this));
        simrv.setAdapter(jobAdapter);
        simrv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        jobAdapter.notifyDataSetChanged();
                }
            }
        };

        new Thread() {
            @Override
            public void run() {
                db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "job_todo").build();
                dao = db.toDoJobDao();
                List<ToDoJob> jobs = dao.getJobByCreateDate(targetDate);
                RecyclerAdapter adapter = ((RecyclerAdapter) simrv.getAdapter());
                assert adapter != null;
                final int old = adapter.getItemCount();
                todoItems.clear();
                if (jobs.size() > 0) {
                    todoItems.addAll(jobs);
                    adapter.notifyDataSetChanged();
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

    private final class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
            implements View.OnClickListener {

        RecyclerAdapter() {
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_simrv, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ToDoJob job = todoItems.get(position);
            holder.text.setText(job.getTitle());
            holder.text.setTag(position);
            holder.renameButton.setTag(position);
            holder.deleteButton.setTag(position);
            holder.topButton.setTag(position);
        }

        @Override
        public int getItemCount() {
            return todoItems.size();
        }

        final class ViewHolder extends RecyclerView.ViewHolder {
            final TextView text;
            final TextView renameButton;
            final TextView deleteButton;
            final TextView topButton;

            ViewHolder(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                renameButton = itemView.findViewById(R.id.button_rename);
                deleteButton = itemView.findViewById(R.id.button_delete);
                topButton = itemView.findViewById(R.id.button_top);

                text.setOnClickListener(RecyclerAdapter.this);
                renameButton.setOnClickListener(RecyclerAdapter.this);
                deleteButton.setOnClickListener(RecyclerAdapter.this);
                topButton.setOnClickListener(RecyclerAdapter.this);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.text:
                    Toast.makeText(MainActivity.this,
                            "Click itemView " + v.getTag().toString(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button_rename:
                    Toast.makeText(MainActivity.this,
                            "Rename itemView " + v.getTag().toString(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button_delete:
                    Toast.makeText(MainActivity.this,
                            "Delete itemView " + v.getTag().toString(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button_top:
                    Toast.makeText(MainActivity.this,
                            "Top itemView " + v.getTag().toString(),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
