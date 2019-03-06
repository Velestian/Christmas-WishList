package com.gretchen.christmaswishlist;
import java.util.ArrayList;

import com.google.tabmanager.TabManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class WishListActivity extends FragmentActivity {
    TabHost tabHost;
    TabManager tabManager;
    WishListDB db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        // get tab manager
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        tabManager = new TabManager(this, tabHost, R.id.realtabcontent);

        // get database
        db = new WishListDB(getApplicationContext());

        // add a tab for each list in the database
        ArrayList<List> lists = db.getLists();
        List newList = new List();
        newList.setName("Add a List");
        lists.add(newList);
        if (lists != null && lists.size() > 0) {
            for (List list : lists) {
                TabSpec tabSpec = tabHost.newTabSpec(list.getName());
                tabSpec.setIndicator(list.getName());
                tabManager.addTab(tabSpec, WishListFragment.class, null);
            }
        }

        // sets current tab to the last tab opened
        if (savedInstanceState != null) {
            if (savedInstanceState.getString("tab") == "Add a List") {
                tabHost.setCurrentTab(0);
            } else {
                tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));

            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", tabHost.getCurrentTabTag());
    }

}