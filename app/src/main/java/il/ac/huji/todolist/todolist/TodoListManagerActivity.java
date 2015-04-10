package il.ac.huji.todolist.todolist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TodoListManagerActivity extends ActionBarActivity {

    /* ================================================================================= */

    private static final int RETURN_FROM_ADD_MENU = 1;
    private static final int menuItemDelete = Menu.FIRST + 1;
    private static final int menuItemCall = Menu.FIRST + 2;

    private ArrayList<TodoItem> lstTodoItems;
    private ArrayAdapter<TodoItem> todoItemsAdapter;

    /* ================================================================================= */

    /**
     * A custom adapter for the todoItems
     */
    private class TodoItemsAdapter<E> extends ArrayAdapter<E> {

        // Constructor
        public TodoItemsAdapter(Context context, int resource, ArrayList<E> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater vi = LayoutInflater.from(getContext());
                view = vi.inflate(R.layout.row, null);
            }

            TextView txtTodoTitle = (TextView) view.findViewById(R.id.txtTodoTitle);
            TextView txtTodoDueDate = (TextView) view.findViewById(R.id.txtTodoDueDate);

            // Set the text of the item (both title and dueDate)
            TodoItem todoItem = lstTodoItems.get(position);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dueDate = todoItem.getTodoDueDate();
            txtTodoTitle.setText(todoItem.getTodoTitle());
            txtTodoDueDate.setText(sdf.format(dueDate));

            // Set the color of the task
            if (removeTime(dueDate).compareTo(removeTime(new Date())) < 0) {
                txtTodoTitle.setTextColor(Color.RED);
                txtTodoDueDate.setTextColor(Color.RED);
            } else {
                txtTodoTitle.setTextColor(Color.BLACK);
                txtTodoDueDate.setTextColor(Color.BLACK);
            }

            return view;
        }
    }

    /* ================================================================================= */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);
        buildListViewAdapter();
    }

    /* ================================================================================= */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_todo_list_manager, menu);
        menu.findItem(R.id.menuItemAdd).setIntent(new Intent(this, AddNewTodoItemActivity.class));
        return true;
    }

    /* ================================================================================= */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menuItemAdd):
                super.onOptionsItemSelected(item);
                this.closeOptionsMenu();
                startActivityForResult(item.getIntent(), RETURN_FROM_ADD_MENU);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* ================================================================================= */

    /**
     * Creates an adapter for the todoItems list and binds them together
     */
    private void buildListViewAdapter() {
        // Initialize a new, empty array
        lstTodoItems = new ArrayList<TodoItem>();

        // Initialize a new adapter
        todoItemsAdapter = new TodoItemsAdapter<TodoItem>(getApplicationContext(),
                R.layout.row,
                lstTodoItems);

        // Bind the listView and the adapter
        ListView listView = (ListView) findViewById(R.id.lstTodoItems);
        listView.setAdapter(todoItemsAdapter);

        // Register for menu
        registerForContextMenu(listView);
    }

    /* ================================================================================= */

    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        switch (reqCode) {
            case RETURN_FROM_ADD_MENU:
                // Check if should add a new item
                if (resCode == RESULT_OK) {
                    Date dueDate = (Date) data.getSerializableExtra("dueDate");
                    String title = data.getStringExtra("title");
                    lstTodoItems.add(new TodoItem(title, dueDate));
                    todoItemsAdapter.notifyDataSetChanged();
                }
                break;
        }

        return;
    }

    /* ================================================================================= */

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Set the title of the context menu
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        String title = lstTodoItems.get(position).getTodoTitle();
        menu.setHeaderTitle(title);

        // Add delete option
        menu.add(Menu.NONE, menuItemDelete, Menu.NONE, R.string.delete_string);

        // Add call option (if needed)
        if (title.startsWith(getResources().getString(R.string.call_string))) {
            menu.add(Menu.NONE, menuItemCall, Menu.NONE, title);
        }
    }

    /* ================================================================================= */

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        switch (item.getItemId()) {
            case menuItemDelete:
                return handleItemDelete(position);
            case menuItemCall:
                return handleItemCall(position);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* ================================================================================= */

    /**
     * Handles deletion of a todoItem
     * @param position the index of the todoItem in lstTodoItems
     */
    private boolean handleItemDelete(int position) {
        lstTodoItems.remove(position);
        todoItemsAdapter.notifyDataSetChanged();
        Toast.makeText(this, R.string.successful_delete, Toast.LENGTH_LONG).show();
        return true;
    }

    /* ================================================================================= */

    /**
     * Handles click on a "Dial" option
     * @param position the index of the todoItem in lstTodoItems
     */
    private boolean handleItemCall(int position) {
        String title = lstTodoItems.get(position).getTodoTitle();
        String phoneNumber = title.replace(getResources().getString(R.string.call_string), "");
        Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        startActivity(dial);
        return true;
    }

    /* ================================================================================= */

    /**
     * Remove the time part from the given date variable
     * @param date A date (including time)
     * @return The same date without the time part
     */
    private Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}