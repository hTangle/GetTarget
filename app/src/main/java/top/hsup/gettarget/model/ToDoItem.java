package top.hsup.gettarget.model;

public class ToDoItem {
    private String time;
    private String item;

    public ToDoItem(String time, String item) {
        this.time = time;
        this.item = item;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return time + "-" + item;
    }
}
