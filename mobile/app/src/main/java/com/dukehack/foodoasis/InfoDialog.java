package com.dukehack.foodoasis;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class InfoDialog extends DialogFragment {

    public static final String TITLE = "dataKey";

    public static InfoDialog newInstance(String data) {

        InfoDialog frag = new InfoDialog();
        Bundle args = new Bundle();
        args.putString("data", data);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String mDataRecieved = getArguments().getString("data","defaultTitle");

        try {
            JSONObject jsonObject = new JSONObject(mDataRecieved);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.alert_layout, null);

            TextView mTextView = (TextView) view.findViewById(R.id.zipcode_county);
            mTextView.setText(jsonObject.getString("zipcode") + " " + jsonObject.getJSONObject("zipInfo").getString("county"));

            mTextView = (TextView) view.findViewById(R.id.zip_pop);
            mTextView.setText("Zip Population: " + jsonObject.getJSONObject("zipInfo").getString("est_population"));

            mTextView = (TextView) view.findViewById(R.id.county_per_capita_income);
            mTextView.setText("County Per Capita Income: $" + jsonObject.getJSONObject("countyInfo").getString("per_capita_income"));

            mTextView = (TextView) view.findViewById(R.id.county_medium_household_income);
            mTextView.setText("County Medium Household Income: $" + jsonObject.getJSONObject("countyInfo").getString("medium_household_income"));

            mTextView = (TextView) view.findViewById(R.id.nearest_bus_stop_distance);
            mTextView.setText("Distance to Nearest Bus Stop: " + jsonObject.getString("closest_stop_distance_miles").substring(0, 4) + " miles");

            mTextView = (TextView) view.findViewById(R.id.nearest_bus_stop_location);
            JSONObject bus_stop = jsonObject.getJSONObject("bus_stop");
            mTextView.setText("Nearest Stop: " + bus_stop.getString("corner") + " corner of " + bus_stop.getString("stop_name"));

            setCancelable(true);
            builder.setView(view);
            Dialog dialog = builder.create();

            return dialog;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new AlertDialog.Builder(getActivity()).create();
    }
}