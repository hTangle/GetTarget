package top.hsup.gettarget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import top.hsup.gettarget.R;
import top.hsup.gettarget.db.model.ToDoJob;
import top.hsup.gettarget.util.Constants;

public class ToDoJobAdapter extends ArrayAdapter<ToDoJob> {
    private int resourceId;
    private List<ToDoJob> jobs;

    public ToDoJobAdapter(Context context, int textViewResourceId, List<ToDoJob> jobs) {
        super(context, textViewResourceId, jobs);
        this.resourceId = textViewResourceId;
        this.jobs = jobs;
    }

    @Override
    public void add(@Nullable ToDoJob job) {
        if(jobs==null){
            jobs=new ArrayList<>();
        }
        jobs.add(job);
    }

    @Override
    public void addAll(@NonNull Collection<? extends ToDoJob> collection) {
        if(jobs==null){
            jobs=new ArrayList<>();
        }
        jobs.addAll(collection);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ToDoJob item = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.item = view.findViewById(R.id.todo_item_item);
            viewHolder.time = view.findViewById(R.id.todo_item_time);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.time.setText(item.getTitle());
        viewHolder.item.setText(item.getCreateDate());
        return view;
    }

    // 定义一个内部类，用于对控件的实例进行缓存
    class ViewHolder {
        TextView time;
        TextView item;
    }
}
