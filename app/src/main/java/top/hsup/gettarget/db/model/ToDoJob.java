package top.hsup.gettarget.db.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        indices = {
                @Index(value = "title"),
                @Index("create_date"),
                @Index("finish_date")
        }
)
public class ToDoJob {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "content")
    public String content;
    @ColumnInfo(name = "create_date")
    public String createDate;
    @ColumnInfo(name = "finish_date")
    public String finishDate;
    @ColumnInfo(name = "update_time")
    public String updateTime;

    public ToDoJob() {
    }

    @Ignore
    public ToDoJob(String title, String content, String createDate, String updateTime) {
        this.title = title;
        this.content = content;
        this.createDate = createDate;
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public ToDoJob setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ToDoJob setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ToDoJob setContent(String content) {
        this.content = content;
        return this;
    }

    public String getCreateDate() {
        return createDate;
    }

    public ToDoJob setCreateDate(String createDate) {
        this.createDate = createDate;
        return this;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public ToDoJob setFinishDate(String finishDate) {
        this.finishDate = finishDate;
        return this;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public ToDoJob setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}
