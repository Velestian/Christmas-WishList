package com.gretchen.christmaswishlist;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;
import android.util.Log;

public class AddEditActivity extends Activity 
implements OnKeyListener, OnClickListener {
    
    private EditText itemEditText;
    private EditText descriptionEditText;
    private Spinner listSpinner;
    private Spinner prioritySpinner;
    private Spinner costSpinner;
    private Spinner colorSpinner;
    private Button addButton;
    private Button deleteButton;
    private Button cancelButton;

    private WishListDB db;
    private boolean editMode;
    private String currentTabName = "";
    private WishListItem item;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_add_edit);
        
        // get references to widgets
        listSpinner = (Spinner) findViewById(R.id.SpList);
        itemEditText = (EditText) findViewById(R.id.etItem);
        descriptionEditText = (EditText) findViewById(R.id.etDescription);
        prioritySpinner = (Spinner) findViewById(R.id.SpPriority);
        costSpinner = (Spinner) findViewById(R.id.SpCost);
        colorSpinner = (Spinner) findViewById(R.id.SpColor);
        addButton = (Button) findViewById(R.id.btnSave);
        deleteButton = (Button) findViewById(R.id.btnDelete);
        cancelButton = (Button) findViewById(R.id.btnCancel);
        
        // set listeners
        itemEditText.setOnKeyListener(this);
        descriptionEditText.setOnKeyListener(this);
        addButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        // get the database object
        db = new WishListDB(this);


        // set the adapter for the spinners
        ArrayList<List> lists = db.getLists();
        ArrayAdapter<List> adapter = new ArrayAdapter<List>(
                this, R.layout.spinner_list, lists);
        listSpinner.setAdapter(adapter);
        ArrayAdapter adapterPriority = ArrayAdapter.createFromResource(
                this, R.array.item_priority, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapterPriority);
        ArrayAdapter adapterCost = ArrayAdapter.createFromResource(
                this, R.array.item_cost, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        costSpinner.setAdapter(adapterCost);
        ArrayAdapter adapterColor = ArrayAdapter.createFromResource(
                this, R.array.color, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapterColor);
        // get edit mode from intent
        Intent intent = getIntent();
        editMode = intent.getBooleanExtra("editMode", false);
        
        // if editing
        if (editMode) {
            // get task
            long itemId = intent.getLongExtra("itemId", -1);
            item = db.getItem(itemId);
            
            // update UI with task
            itemEditText.setText(item.getItem());
            descriptionEditText.setText(item.getDescription());
            prioritySpinner.setSelection(item.getPriority());
            costSpinner.setSelection(item.getCost());
            colorSpinner.setSelection(item.getColor());

        }
        else  {
            item = new WishListItem();
        }
        
        // set the correct list for the spinner
        long listID;
        if (editMode) {   // edit mode - use same list as selected task
            listID = (int) item.getListId();
        }
        else {            // add mode - use the list for the current tab
            currentTabName = intent.getStringExtra("tab");
            listID = (int) db.getList(currentTabName).getId();
        }
        // subtract 1 from database ID to get correct list position
        int listPosition = (int) listID - 1;
        listSpinner.setSelection(listPosition);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:
                saveToDB();
                this.finish();
                break;
            case R.id.btnCancel:
                this.finish();
                break;
            case R.id.btnDelete:
                deleteFromDB();
                this.finish();
                break;
        }
    }
    
    private void deleteFromDB() {

        // if no task name, exit method
        if (item == null || item.equals("")) {
            return;
        }

        db.deleteItem(item.getId());

    }

    private void saveToDB() {
        // get data from widgets
        int listID = listSpinner.getSelectedItemPosition() + 1;
        String itemName = itemEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        int priority = prioritySpinner.getSelectedItemPosition();
        int cost = costSpinner.getSelectedItemPosition();
        int color = colorSpinner.getSelectedItemPosition();

        // if add mode, create new task
        if (!editMode) {
            item = new WishListItem();
        }

        // if no task name, exit method
        if (item == null || item.equals("")) {
            Log.d("error", "null");
            return;
        }

        // put data in task
        item.setListId(listID);
        item.setItem(itemName);
        item.setDescription(description);
        item.setPriority(priority);
        item.setCost(cost);
        item.setColor(color);

        // update or insert task
        if (editMode) {
            db.updateItem(item);
        }
        else {
            db.insertItem(item);
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            // hide the soft Keyboard
            InputMethodManager imm = (InputMethodManager) 
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveToDB();
            return false;
        }
        return false;
    }
}