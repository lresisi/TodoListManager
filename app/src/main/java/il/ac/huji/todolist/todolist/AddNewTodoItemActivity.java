package il.ac.huji.todolist.todolist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

public class AddNewTodoItemActivity extends Activity {

    /* ================================================================================= */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_todo_item);
        this.setFinishOnTouchOutside(false);

        // Define the ok button
        Button btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();

                // Calculate the title
                EditText edtNewItem = (EditText) findViewById(R.id.edtNewItem);
                String title = edtNewItem.getText().toString();
                intent.putExtra("title", title);

                // Calculate the due date
                DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
                Date dueDate = extractDateFromDatePicker(datePicker);
                intent.putExtra("dueDate", dueDate);

                // Return
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // Define the cancel button
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    /* ================================================================================= */

    /**
     * Extracts the date from the datePicker object
     * @param datePicker
     * @return the date represented by the datePicker object
     */
    private static Date extractDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

}
