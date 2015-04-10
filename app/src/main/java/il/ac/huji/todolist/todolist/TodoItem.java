package il.ac.huji.todolist.todolist;

import java.util.Date;

public class TodoItem {
    private String txtTodoTitle;
    private Date txtTodoDueDate;

    // Constructor
    public TodoItem(String title, Date dueDate) {
        this.txtTodoTitle = title;
        this.txtTodoDueDate = dueDate;
    }

    /**
     * Getter for the title of the task
     * @return the title of the task
     */
    public String getTodoTitle() {
        return this.txtTodoTitle;
    }

    /**
     * Getter for the dueDate of the task
     * @return the dueDate of the task
     */
    public Date getTodoDueDate() {
        return this.txtTodoDueDate;
    }
}