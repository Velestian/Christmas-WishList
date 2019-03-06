package com.gretchen.christmaswishlist;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.content.Intent;



public class AddList extends Activity implements OnClickListener {

    private EditText name;
    private WishListDB db;
    private Button addButton;
    private Button deleteButton;
    private Button cancelButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_list);

        //get widget and database
        name = findViewById(R.id.etListName);
        db = new WishListDB(this);
        addButton = findViewById(R.id.btnAdd);
        deleteButton = findViewById(R.id.btnDelete);
        cancelButton = findViewById(R.id.btnCancel);


        addButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAdd:
                saveToDB();
                break;
            case R.id.btnCancel:
                break;
            case R.id.btnDelete:
                deleteFromDB();
                break;
        }
        Intent intent = new Intent(this, WishListActivity.class);
        startActivity(intent);
    }

    private void saveToDB() {
        // get data from widgets
        String newList = name.getText().toString();

            db.addList(newList.toUpperCase());
            this.finish();


    }

    private void deleteFromDB() {
        String deleteList = name.getText().toString();

        db.deleteList(deleteList.toUpperCase());

    }
}