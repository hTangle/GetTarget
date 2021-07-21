package top.hsup.gettarget.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import top.hsup.gettarget.R;
import top.hsup.gettarget.db.model.ToDoJob;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
        implements View.OnClickListener {
    private List<ToDoJob> jobs;
    private Handler handler;

    public RecyclerAdapter(Handler handler) {
        this.jobs = new ArrayList<>();
        this.handler = handler;
    }

    public void clear() {
        jobs.clear();
    }

    public void deleteByIndex(int index) {
        if (index < jobs.size()) {
            jobs.remove(index);
        }
    }

    public void add(ToDoJob job) {
        jobs.add(job);
    }

    public void addAll(List<ToDoJob> jobs) {
        this.jobs.addAll(jobs);
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
        ToDoJob job = jobs.get(position);
        job.index = position;
        holder.text.setText(job.getTitle());
        holder.text.setTag(job);
        holder.renameButton.setTag(job);
        holder.deleteButton.setTag(job);
        holder.topButton.setTag(job);
    }

    @Override
    public int getItemCount() {
        return jobs.size();
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
        Message msg = new Message();
        msg.what = 1;
        Bundle bundle = new Bundle();
        ToDoJob job = (ToDoJob) v.getTag();
        bundle.putString("content", v.getTag().toString());
        bundle.putInt("id", job.getId());
        bundle.putInt("index", job.index);
        switch (v.getId()) {
            case R.id.text:
                bundle.putString("function", "test");
                break;
            case R.id.button_rename:
                bundle.putString("function", "button_rename");
                break;
            case R.id.button_delete:
                bundle.putString("function", "button_delete");
                break;
            case R.id.button_top:
                bundle.putString("function", "button_top");
                break;
        }
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
}
