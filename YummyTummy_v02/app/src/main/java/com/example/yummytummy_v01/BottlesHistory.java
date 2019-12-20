package com.example.yummytummy_v01;

import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BottlesHistory extends Fragment {

    private BottlesHistoryViewModel mViewModel;
    private View root;
    Bundle args ;
    String name;
    User user;

    public static BottlesHistory newInstance() {
        return new BottlesHistory();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.bottles_history_fragment, container, false);
        args= getActivity().getIntent().getExtras();
        name = args.getString("USERNAME");
        user = User.createUser(name);
        init();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(BottlesHistoryViewModel.class);
        // TODO: Use the ViewModel
    }

    public void init() {
        Toast.makeText(getActivity(), name, Toast.LENGTH_LONG).show();

        if (user != null) {
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1f);
            TableLayout stk = (TableLayout) root.findViewById(R.id.table_main);
            TableRow tbrow0 = new TableRow(getActivity());
            TextView tv0 = new TextView(getActivity());
            tv0.setText(" Child name");
            tv0.setTextColor(Color.parseColor("#000000"));
            tv0.setLayoutParams(params);
            tbrow0.addView(tv0);
            TextView tv1 = new TextView(getActivity());
            tv1.setText(" Water in mL");
            tv1.setTextColor(Color.parseColor("#000000"));
            tv1.setLayoutParams(params);
            tbrow0.addView(tv1);
            TextView tv2 = new TextView(getActivity());
            tv2.setText(" Scoops ");
            tv2.setTextColor(Color.parseColor("#000000"));
            tv2.setLayoutParams(params);
            tbrow0.addView(tv2);
            TextView tv3 = new TextView(getActivity());
            tv3.setText(" Date/time ");
            tv3.setTextColor(Color.parseColor("#000000"));
            tv3.setLayoutParams(params);
            tbrow0.addView(tv3);
            stk.addView(tbrow0);

            ArrayList<BottleHistory> bottles = user.getMyList();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            for (int i = 0; i < bottles.size(); i++) {
               // Toast.makeText(getActivity(), "bottle #"+i+": "+bottles.get(i).getScoops()+" "+bottles.get(i).getSonName()+" "+bottles.get(i).getWaterAmount()+" "+bottles.get(i).getPreperationDate().toString(), Toast.LENGTH_LONG).show();

                TableRow tbrow = new TableRow(getActivity());
                TextView t1v = new TextView(getActivity());
                t1v.setText(bottles.get(i).getSonName());
                t1v.setTextColor(Color.parseColor("#000000"));
                t1v.setGravity(Gravity.CENTER);
                t1v.setLayoutParams(params);
                tbrow.addView(t1v);
                TextView t2v = new TextView(getActivity());
                t2v.setText(bottles.get(i).getWaterAmount() + " mL");
                t2v.setTextColor(Color.parseColor("#000000"));
                t2v.setGravity(Gravity.CENTER);
                t2v.setLayoutParams(params);
                tbrow.addView(t2v);
                TextView t3v = new TextView(getActivity());
                t3v.setText(bottles.get(i).getScoops() + "");
                t3v.setTextColor(Color.parseColor("#000000"));
                t3v.setGravity(Gravity.CENTER);
                t3v.setLayoutParams(params);
                tbrow.addView(t3v);
                TextView t4v = new TextView(getActivity());
                t4v.setText(dateFormat.format(bottles.get(i).getPreperationDate()));
                t4v.setTextColor(Color.parseColor("#000000"));
                t4v.setGravity(Gravity.CENTER);
                t4v.setLayoutParams(params);
                tbrow.addView(t4v);
                stk.addView(tbrow);
            }

        }
        else{
            Toast.makeText(getActivity(), "failed to retrieve user data", Toast.LENGTH_LONG).show();
        }
    }
}
