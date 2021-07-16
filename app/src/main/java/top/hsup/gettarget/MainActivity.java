package top.hsup.gettarget;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import top.hsup.gettarget.adapter.ToDoAdapter;
import top.hsup.gettarget.model.ToDoItem;

public class MainActivity extends AppCompatActivity {
    private List<ToDoItem> todoItems = new ArrayList<>();
    private ToDoAdapter toDoAdapter;
    private int count = 10;
    private String targetDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINESE));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDoAdapter.add(new ToDoItem(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.CHINESE)), "todo-" + count));
                count++;
                System.out.println(count);
            }
        });
        initToDoItems();
        toDoAdapter = new ToDoAdapter(MainActivity.this, R.layout.todo_item, todoItems);
        NestedListView todoListView = findViewById(R.id.items_shower);
        todoListView.setAdapter(toDoAdapter);
        setListViewHeightBasedOnChildren(todoListView);
        todoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ToDoItem toDoItem = todoItems.get(i);
                Toast.makeText(MainActivity.this, toDoItem.getItem(), Toast.LENGTH_SHORT).show();
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
                    System.out.println(calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay());
                    targetDate = calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay();
                    toDoAdapter.add(new ToDoItem(targetDate, "todo-" + count));
                    count++;
                    System.out.println(count);
                }
            }
        });
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    private void initToDoItems() {
        for (int i = 0; i < 1; i++) {
            ToDoItem toDoItem = new ToDoItem(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM", Locale.CHINESE)), "todo-" + i);
            System.out.println(toDoItem);
            todoItems.add(toDoItem);
        }

    }
}