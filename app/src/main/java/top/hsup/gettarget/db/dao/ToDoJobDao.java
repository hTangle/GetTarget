package top.hsup.gettarget.db.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

import top.hsup.gettarget.db.model.ToDoJob;

@Dao
public interface ToDoJobDao {
    @Query("select * from todojob")
    List<ToDoJob> getAll();

    @Query("select * from todojob where create_date = :create")
    List<ToDoJob> getJobByCreateDate(String create);

    @Query("select * from todojob where create_date = :create and status=0")
    List<ToDoJob> getUnfinishedJobByCreateDate(String create);

    @Query("select * from todojob where finish_date = :finish")
    List<ToDoJob> getJobByFinishDate(String finish);

    @Insert(onConflict = 5)
    void insertJob(ToDoJob job);

    @Delete
    void deleteJob(ToDoJob job);

    @Update
    void updateJob(ToDoJob job);

    @Query("update todojob set status = :status where id = :id")
    void updateJobStatus(int id, int status);

}
