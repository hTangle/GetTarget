package top.hsup.gettarget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import top.hsup.gettarget.R;
import top.hsup.gettarget.model.ToDoItem;

public class ToDoAdapter extends ArrayAdapter<ToDoItem> {

    private int resourceId;
    private List<ToDoItem> items;

    public ToDoAdapter(Context context, int textViewResourceId, List<ToDoItem> items) {
        super(context, textViewResourceId, items);
        this.resourceId = textViewResourceId;
        this.items=items;
    }

    public void add(ToDoItem item){
        if(items==null){
            items=new ArrayList<>();
        }
        if(item!=null){
            items.add(0,item);
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ToDoItem item = getItem(position);
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
        viewHolder.time.setText(item.getTime());
        viewHolder.item.setText(item.getItem());
        return view;
    }

    // 定义一个内部类，用于对控件的实例进行缓存
    class ViewHolder {
        TextView time;
        TextView item;
    }

}

