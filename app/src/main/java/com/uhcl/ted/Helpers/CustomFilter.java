package com.uhcl.ted.Helpers;

import android.widget.Filter;

import com.uhcl.ted.Model.Doctor;

import java.util.ArrayList;

/**
 * Created by LENOVO on 12-04-2018.
 */

class CustomFilter extends Filter {
    DoctorAdapter adapter;
    ArrayList<Doctor> filterList;

    public CustomFilter(ArrayList<Doctor> filterList, DoctorAdapter adapter) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    //filtering doctors
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        //checking validity constraints
        if (constraint != null && constraint.length() > 0) {
            //CHANGE TO UPPER
            constraint = constraint.toString().toUpperCase();
            //store filtered doctors
            ArrayList<Doctor> filteredDoctors = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                //condition checking
                if (filterList.get(i).getSpec().toUpperCase().contains(constraint)) {
                    //add doctor to filter doctor list
                    filteredDoctors.add(filterList.get(i));
                }
            }
            results.count = filteredDoctors.size();
            results.values = filteredDoctors;
        } else {
            results.count = filterList.size();
            results.values = filterList;

        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.itemList = (ArrayList<Doctor>) results.values;
        //refresh adapter view
        adapter.notifyDataSetChanged();
    }
}
