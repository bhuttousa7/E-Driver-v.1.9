package com.telogix.telogixcaptain.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.google.gson.Gson;
import com.telogix.telogixcaptain.R;
import com.telogix.telogixcaptain.Enums.UserRole;
import com.telogix.telogixcaptain.Interfaces.ItemSelection;
import com.telogix.telogixcaptain.Pojo.Datum;
import com.telogix.telogixcaptain.Pojo.TokenPojo;
import com.telogix.telogixcaptain.Pojo.singleton;
import com.telogix.telogixcaptain.activities.AssignLoadActivity;
import com.telogix.telogixcaptain.activities.Singleton.CustomData;

import java.util.ArrayList;

public class MyPagerAdapter extends PagerAdapter implements ItemSelection {
    private final TokenPojo datum;
    private final Context context;
    private RecyclerView lstVehicles;
    private AvailableVehiclesAdapter availableVehiclesAdapter;
  public static  ArrayList<Datum> arrayList = new ArrayList<>();
    private String[] tabTitles = new String[]{"Tab1", "Tab2", "Tab3"};
    int tabcount;
    private final ArrayList<Datum> templistvehicles = new ArrayList<>();

    public MyPagerAdapter(Context context, ArrayList<Datum> arrayList, int tabcount) {
        this.context = context;
        MyPagerAdapter.arrayList = arrayList;
        this.tabcount = tabcount;
        String json = singleton.getsharedpreference(context).getString("userData", "");
        Gson gson = new Gson();
        datum = gson.fromJson(json, TokenPojo.class);
        if (UserRole.getRole(datum.getRoleID()).equals(UserRole.SCHEDULER))        // scheduler
        {
            tabTitles = new String[]{"All", "Assigned", "Unassigned"};

        } else if (UserRole.getRole(datum.getRoleID()).equals(UserRole.SUPER_ADMIN))        // scheduler
        {
            tabTitles = new String[]{"All"};


        } else if (UserRole.getRole(datum.getRoleID()).equals(UserRole.PRELOAD_INSPECTOR)) // preload inspector
        {
            tabTitles = new String[]{"Inspect", "Inspected"};
        } else                                       // journey manager
        {
            tabTitles = new String[]{"All", "Assign Route", "Route Assigned", "Unassigned", "Assigned", "Inspected", "Briefed"};

        }
    }

    /*
    This callback is responsible for creating a page. We inflate the layout and set the drawable
    to the ImageView based on the position. In the end we add the inflated layout to the parent
    container .This method returns an object key to identify the page view, but in this example page view
    itself acts as the object key
    */
    @Override
    public Object instantiateItem(ViewGroup container, int selectedposition) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewpager_vehicles, null);


        lstVehicles = view.findViewById(R.id.lstVehicles);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        lstVehicles.setLayoutManager(mLayoutManager);
        lstVehicles.setItemAnimator(new DefaultItemAnimator());
        lstVehicles.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL) {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                // Do not draw the divider
            }
        });

        ///////////////////////////////////////


        if (UserRole.getRole(datum.getRoleID()).equals(UserRole.JM)) {
            selectedposition=selectedposition-1;
            if (selectedposition == 0) {
                templistvehicles.clear();
               templistvehicles.addAll(arrayList);
               // availableVehiclesAdapter = new AvailableVehiclesAdapter(context, templistvehicles, null);
            } else if (selectedposition == 1) {
                templistvehicles.clear();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getStatusID() == 2) {
                        templistvehicles.add(arrayList.get(i));
                    }
                }
              //  availableVehiclesAdapter = new AvailableVehiclesAdapter(context, templistvehicles, null);
                //    viewPager.setAdapter(new MyPagerAdapter(getContext(),unloadedVehicles,tablayout.getTabCount()));
            } else if (selectedposition == 2) {
                templistvehicles.clear();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getStatusID() == 3) {
                        templistvehicles.add(arrayList.get(i));
                    }
                }
              //  availableVehiclesAdapter = new AvailableVehiclesAdapter(context, templistvehicles, null);
                //       viewPager.setAdapter(new MyPagerAdapter(getContext(),unloadedVehicles,tablayout.getTabCount()));

//                            lstVehicles.setAdapter(new AvailableVehiclesAdapter(context, unloadedVehicles, VehiclesFragment.this));

            } else if (selectedposition == 3) {
                // Unassigned
                templistvehicles.clear();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getStatusID() == 1) {
                        templistvehicles.add(arrayList.get(i));
                    }
                }
               // availableVehiclesAdapter = new AvailableVehiclesAdapter(context, templistvehicles, null);
                //      viewPager.setAdapter(new MyPagerAdapter(getContext(),unloadedVehicles,tablayout.getTabCount()));

            } else if (selectedposition == 4) {
                //Assigned
                templistvehicles.clear();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getStatusID() == 2) {
                        templistvehicles.add(arrayList.get(i));
                    }
                }
                availableVehiclesAdapter = new AvailableVehiclesAdapter(context, templistvehicles, null);
                //       viewPager.setAdapter(new MyPagerAdapter(getContext(),unloadedVehicles,tablayout.getTabCount()));

            } else if (selectedposition == 5) {
                //Inspect
                templistvehicles.clear();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getStatusID() == 3) {
                        templistvehicles.add(arrayList.get(i));
                    }
                }
               // availableVehiclesAdapter = new AvailableVehiclesAdapter(context, templistvehicles, null);
                //   viewPager.setAdapter(new MyPagerAdapter(getContext(),unloadedVehicles,tablayout.getTabCount()));

            } else if (selectedposition == 6) {
                //Inspected
                templistvehicles.clear();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getStatusID() == 4) {
                        templistvehicles.add(arrayList.get(i));
                    }
                }

                //   viewPager.setAdapter(new MyPagerAdapter(getContext(),unloadedVehicles,tablayout.getTabCount()));

            }
            //    PagerAdapter.notifyDataSetChanged();
        }
        //lstVehicles.setAdapter(availableVehiclesAdapter);
        availableVehiclesAdapter = new AvailableVehiclesAdapter(context, templistvehicles, null);

        lstVehicles.setAdapter(availableVehiclesAdapter);
        ///////////////////////////////////////
        container.addView(view);
        return view;
    }

    /*
    This callback is responsible for destroying a page. Since we are using view only as the
    object key we just directly remove the view from parent container
    */
    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    /*
    Returns the count of the total pages
    */
    @Override
    public int getCount() {
        return tabcount;
    }

    /*
    Used to determine whether the page view is associated with object key returned by instantiateItem.
    Since here view only is the key we return view==object
    */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }


    @Override
    public void onAssignLoadClick(Datum datum) {
        CustomData.getInstance().vehicleDetails = datum;
        context.startActivity(new Intent(context, AssignLoadActivity.class));
    }

    @Override
    public void onClickCloseSearchView() {

    }
}