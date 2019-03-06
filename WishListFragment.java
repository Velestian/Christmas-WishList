package com.gretchen.christmaswishlist;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.util.Log;

public class WishListFragment extends Fragment implements OnClickListener{

    private String currentTabTag;
    private Button btnAdd;
    private Button btnPriority;
    private Button btnCost;
    private TextView gift1;
    private TextView gift2;
    private TextView gift3;
    private TextView gift4;
    private TextView gift5;
    private TextView gift6;
    private TextView gift7;
    private String sortCriteria;
    private String tabIndex1;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)  {
        
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wish_list,
            container, false);
        
        // get references to widgets
        btnAdd = view.findViewById (R.id.btnAdd);
        btnPriority = view.findViewById (R.id.btnPriority);
        btnCost = view.findViewById (R.id.btnCost);
        gift1 = view.findViewById (R.id.tvGift1);
        gift2 = view.findViewById (R.id.tvGift2);
        gift3 = view.findViewById (R.id.tvGift3);
        gift4 = view.findViewById (R.id.tvGift4);
        gift5 = view.findViewById (R.id.tvGift5);
        gift6 = view.findViewById (R.id.tvGift6);
        gift7 = view.findViewById (R.id.tvGift7);


        // get the current tab
        TabHost tabHost = (TabHost) container.getParent().getParent();
        currentTabTag = tabHost.getCurrentTabTag();
        
        // refresh the task list view
        String sortCriteria = "_id";
        refreshWishList(sortCriteria);

        btnAdd.setOnClickListener(this);
        btnPriority.setOnClickListener(this);
        btnCost.setOnClickListener(this);
        gift1.setOnClickListener(this);
        gift2.setOnClickListener(this);
        gift3.setOnClickListener(this);
        gift4.setOnClickListener(this);
        gift5.setOnClickListener(this);
        gift6.setOnClickListener(this);
        gift7.setOnClickListener(this);

        // return the view
        return view;
    }
    
    public void refreshWishList(String sortCriteria) {
        // get task list for current tab from database
        Context context = getActivity().getApplicationContext();
        WishListDB db = new WishListDB(context);
        if (currentTabTag == "Add a List")    {
            Intent intent = new Intent(getActivity().getApplication(), AddList.class);
            startActivity(intent);
        }
        else {
            ArrayList<WishListItem> items = db.getItems(currentTabTag, sortCriteria);
            //hides text edits that aren't used
            //displays in text edit according to search order
            if (!items.isEmpty()) {
                gift1.setText(items.get(0).getItem());
                setColor(items, 0, gift1);
            } else {
                gift1.setVisibility(View.GONE);
            }
            if (items.size() > 1) {
                gift2.setText(items.get(1).getItem());
                setColor(items, 1, gift2);
            } else {
                gift2.setVisibility(View.GONE);
            }
            if (items.size() > 2) {
                gift3.setText(items.get(2).getItem());
                setColor(items, 2, gift3);
            } else {
                gift3.setVisibility(View.GONE);
            }
            if (items.size() > 3) {
                gift4.setText(items.get(3).getItem());
                setColor(items, 3, gift4);
            } else {
                gift4.setVisibility(View.GONE);
            }
            if (items.size() > 4) {
                gift5.setText(items.get(4).getItem());
                setColor(items, 4, gift5);
            } else {
                gift5.setVisibility(View.GONE);
            }
            if (items.size() > 5) {
                gift6.setText(items.get(5).getItem());
                setColor(items, 5, gift6);
            } else {
                gift6.setVisibility(View.GONE);
            }
            if (items.size() > 6) {
                gift7.setText(items.get(6).getItem());
                setColor(items, 6, gift7);
            } else {
                gift7.setVisibility(View.GONE);
            }
        }
    }

    //Sets background image for each gift to chosen color
    public void setColor(ArrayList<WishListItem> list, int listPosition, TextView giftItem)  {
        switch (list.get(listPosition).getColor())  {
            case 0:
                giftItem.setBackgroundResource(R.drawable.ball_red);
                break;
            case 1:
                giftItem.setBackgroundResource(R.drawable.blue_ball);
                break;
            case 2:
                giftItem.setBackgroundResource(R.drawable.ball_light_blue);
                break;
            case 3:
                giftItem.setBackgroundResource(R.drawable.ball_green);
                break;
            case 4:
                giftItem.setBackgroundResource(R.drawable.ball_silver);
                break;
            case 5:
                giftItem.setBackgroundResource(R.drawable.ball_yellow);
                break;
            case 6:
                giftItem.setBackgroundResource(R.drawable.ball_purple);
                break;
        }

    }

    @Override
    public void onClick(View v) {
        long itemID = -1;
        Context context = getActivity().getApplicationContext();
        WishListDB db = new WishListDB(context);
        ArrayList<WishListItem> items = db.getItems(currentTabTag, sortCriteria);

        switch (v.getId()) {
            case R.id.btnAdd:
                addOrEdit(itemID, false);
                break;
            case R.id.tvGift1:
                itemID = items.get(0).getId();
                addOrEdit(itemID, true);
                break;
            case R.id.tvGift2:
                itemID = items.get(1).getId();
                addOrEdit(itemID, true);
                break;
            case R.id.tvGift3:
                itemID = items.get(2).getId();
                addOrEdit(itemID, true);
                break;
            case R.id.tvGift4:
                itemID = items.get(3).getId();
                addOrEdit(itemID, true);
                break;
            case R.id.tvGift5:
                itemID = items.get(4).getId();
                addOrEdit(itemID, true);
                break;
            case R.id.tvGift6:
                itemID = items.get(5).getId();
                addOrEdit(itemID, true);
                break;
            case R.id.tvGift7:
                itemID = items.get(6).getId();
                addOrEdit(itemID, true);
                break;
            case R.id.btnPriority:
                // sort items by priority
                sortCriteria ="priority";
                refreshWishList(sortCriteria);
                break;
            case R.id.btnCost:
                //sort wish items by cost
                sortCriteria ="cost";
                refreshWishList(sortCriteria);
                break;
        }
    }

    public void addOrEdit(long itemID, boolean editMode)  {
        Intent intent = new Intent(getActivity().getApplication(), AddEditActivity.class);
        intent.putExtra("tab", currentTabTag);
        intent.putExtra("itemId", itemID);
        intent.putExtra("editMode", editMode);
        startActivity(intent);
        refreshWishList(sortCriteria);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshWishList(sortCriteria);
    }

}