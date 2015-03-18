package il.ac.huji.todolist.todolist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class TodoListManagerActivity extends ActionBarActivity {

    /* ================================================================================= */

    private ArrayList<String> todoItemsArr;
    private ArrayAdapter<String> todoItemsAdapter;

    /* ================================================================================= */

    /**
     * A custom adapter for the todoItems
     */
    private class TodoItemsAdapter<E> extends ArrayAdapter<E> {

        // Define the colors for the todoItems
        private final int[] colorsArr = {Color.RED, Color.BLUE};

        // Constructor
        public TodoItemsAdapter(Context context, int resource, ArrayList<E> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            int colorIndex = position % colorsArr.length;
            view.setTextColor(colorsArr[colorIndex]);
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

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_list_manager, menu);
        return true;
    }

    /* ================================================================================= */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.add):
                return menuItemAdd();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* ================================================================================= */

    /**
     * Handles the behavior upon addition of a new todoItem
     * @return true if a new item was added, false otherwise
     */
    private boolean menuItemAdd() {
        EditText edtNewItem = (EditText) findViewById(R.id.edtNewItem);
        String newItemContent = edtNewItem.getText().toString();

        // Handle empty tasks
        if (newItemContent.trim().isEmpty()) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.empty_item_title)
                    .setMessage(R.string.empty_item_message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();

            return false;
        }

        // Add the task to the array
        todoItemsArr.add(newItemContent);
        todoItemsAdapter.notifyDataSetChanged();

        // Reset the newItem field
        edtNewItem.setText("");

        return true;
    }

    /* ================================================================================= */

    /**
     * Creates an adapter for the todoItems list and binds them together
     */
    private void buildListViewAdapter() {
        // Initialize a new, empty array
        todoItemsArr = new ArrayList<String>();

        // Initialize a new adapter
        todoItemsAdapter = new TodoItemsAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                todoItemsArr);

        // Bind the listView and the adapter
        ListView listView = (ListView) findViewById(R.id.lstTodoItems);
        listView.setAdapter(todoItemsAdapter);
        listView.setOnItemLongClickListener(longClickListener);
    }

    /* ================================================================================= */

    /**
     * Defines the behavior upn long click on a todoItem
     */
    private OnItemLongClickListener longClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, final int position, long arg3) {
        new AlertDialog.Builder(TodoListManagerActivity.this)
                .setTitle(todoItemsArr.get(position))
                .setNegativeButton(R.string.delete_string, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        menuItemDelete(position);
                    }
                }).show();

        return true;
        }
    };

    /* ================================================================================= */

    private void menuItemDelete(int position) {
        todoItemsArr.remove(position);
        todoItemsAdapter.notifyDataSetChanged();
    }
}