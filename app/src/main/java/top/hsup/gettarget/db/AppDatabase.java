package top.hsup.gettarget.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import top.hsup.gettarget.db.converter.ToDoJobConverters;
import top.hsup.gettarget.db.dao.ToDoJobDao;
import top.hsup.gettarget.db.model.ToDoJob;

@Database(entities = {ToDoJob.class},version = 2)
@TypeConverters({ToDoJobConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ToDoJobDao toDoJobDao();
}
