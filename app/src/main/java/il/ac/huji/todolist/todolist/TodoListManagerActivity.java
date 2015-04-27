package il.ac.huji.todolist.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TodoListManagerActivity extends ActionBarActivity {

    /* ================================================================================= */

    // IDs definitions
    private static final int RETURN_FROM_ADD_MENU = 1;
    private static final int menuItemDelete = Menu.FIRST + 1;
    private static final int menuItemCall = Menu.FIRST + 2;

    // Global variables
    private TodoItemsAdapter todoItemsAdapter;
    private SQLiteDatabase db;
    private DBHelper dbHelper;

/* ================================================================================= */

    /**
     * A custom adapter for the todoItems
     */
    private class TodoItemsAdapter extends SimpleCursorAdapter {

        private int titleIndex, dueDateIndex;

        // Constructor
        public TodoItemsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            titleIndex = c.getColumnIndex(DBHelper.TITLE_COL_NAME);
            dueDateIndex = c.getColumnIndex(DBHelper.DUE_DATE_COL_NAME);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            return inflater.inflate(R.layout.row, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Find the relevant TextViews
            TextView txtTodoTitle = (TextView) view.findViewById(R.id.txtTodoTitle);
            TextView txtTodoDueDate = (TextView) view.findViewById(R.id.txtTodoDueDate);

            // Set the text of the item (both title and dueDate)
            txtTodoTitle.setText(cursor.getString(titleIndex));
            Date dueDate = new Date(cursor.getLong(dueDateIndex));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            txtTodoDueDate.setText(sdf.format(dueDate));

            // Set the color of the task
            if (removeTime(dueDate).compareTo(removeTime(new Date())) < 0) {
                txtTodoTitle.setTextColor(Color.RED);
                txtTodoDueDate.setTextColor(Color.RED);
            } else {
                txtTodoTitle.setTextColor(Color.BLACK);
                txtTodoDueDate.setTextColor(Color.BLACK);
            }
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
    protected void onDestroy() {
        super.onDestroy();
        db.close();
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
            // Display the Add ContextMenu
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
        // Create the database
        dbHelper = new DBHelper(TodoListManagerActivity.this);
        db = dbHelper.getWritableDatabase();

        // Create the adapter
        Cursor cursor = dbHelper.getCursor(db);
        String[] from = new String[] {DBHelper.TITLE_COL_NAME, DBHelper.DUE_DATE_COL_NAME};
        int[] to = new int[] {R.id.txtTodoTitle, R.id.txtTodoDueDate};
        todoItemsAdapter = new TodoItemsAdapter(TodoListManagerActivity.this, R.layout.row, cursor, from, to, 0);

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
                    // Extract the data from the Intent
                    Date dueDate = (Date) data.getSerializableExtra("dueDate");
                    String title = data.getStringExtra("title");

                    // Add the new item
                    handleItemAdd(title, dueDate);
                }
                break;
        }
    }

    /* ================================================================================= */

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Set the title of the context menu
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        String title = getTitleAtPosition(position);
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
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case menuItemDelete:
                return handleItemDelete(info.id);
            case menuItemCall:
                return handleItemCall(info.position);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* ================================================================================= */

    /**
     * Handles deletion of a todoItem
     * @param id the id of the todoItem in lstTodoItems
     */
    private boolean handleItemDelete(long id) {
        db.delete(DBHelper.TABLE_NAME, DBHelper.KEY_COL_NAME + " = " + Long.toString(id), null);
        todoItemsAdapter.changeCursor(dbHelper.getCursor(db));
        Toast.makeText(this, R.string.successful_delete, Toast.LENGTH_LONG).show();
        return true;
    }

    /* ================================================================================= */

    /**
     * Handles click on a "Dial" option
     * @param position the id of the todoItem in lstTodoItems
     */
    private boolean handleItemCall(int position) {
        String title = getTitleAtPosition(position);
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
    private static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /* ================================================================================= */

    /**
     * Add a new item to the database
     * @param title the title of the new item
     * @param dueDate the due date of the new item
     */
    private void handleItemAdd(String title, Date dueDate) {
        ContentValues todoItem = new ContentValues();
        todoItem.put(DBHelper.TITLE_COL_NAME, title);
        todoItem.put(DBHelper.DUE_DATE_COL_NAME, dueDate.getTime());
        db.insert(DBHelper.TABLE_NAME, null, todoItem);
        todoItemsAdapter.changeCursor(dbHelper.getCursor(db));
    }

    /* ================================================================================= */

    /**
     * Returns the title of the todoItem in the given position
     * @param position the position of the todoItem
     * @return the title of the todoItem in the given position
     */
    private String getTitleAtPosition (int position) {
        Cursor cursor = todoItemsAdapter.getCursor();
        cursor.moveToPosition(position);
        return cursor.getString(todoItemsAdapter.titleIndex);
    }
}