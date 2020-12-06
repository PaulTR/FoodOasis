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

            TextView mTextView = (TextView) view.findViewById(R.id.textview);
            /*
            {zipcode=80220,
            closest_stop_distance_miles=0.2617848502494964,
            bus_stop={zip=80220, distance_from_street=125, lng=-104.922242, city=Denver, county=Denver County, stop_name=6th Ave & Holly St, stopname_uid=6th Ave & Holly St (Stop No. 11554),
                on_street=E 6th Av S, routes=6, at_street=Holly St, corner=SE, lat=39.725292, direction=E},
            zipInfo={latitude=39.73, county=Denver County, est_population=33250, longitude=-104.92},
            countyInfo={total_income=46612315000, per_capita_income=67256, medium_household_income=61038, population=693060}}
             */
            mTextView.setText(jsonObject.getJSONObject("bus_stop").getString("corner"));
            setCancelable(true);

            builder.setView(view);
            Dialog dialog = builder.create();

//            dialog.getWindow().setBackgroundDrawable(
//                    new ColorDrawable(Color.TRANSPARENT));

            return dialog;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new AlertDialog.Builder(getActivity()).create();
    }
}