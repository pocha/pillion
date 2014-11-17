package com.getpillion.common;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.getpillion.R;

import java.sql.Time;
import java.text.DateFormatSymbols;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class TimeDateFilterFragment extends DialogFragment implements OnDateSetListener {

    @InjectView(R.id.startTime) EditText startTime;
    @InjectView(R.id.startTimeAMPM)
    Spinner startTimeAMPM;
    @InjectView(R.id.endTime) EditText endTime;
    @InjectView(R.id.endTimeAMPM)
    Spinner endTimeAMPM;
    @InjectView(R.id.date) EditText date;

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";

    public Long startTimeLong = null;
    public Long endTimeLong = null;
    public Long dateLong = null;

    private Calendar calendar = Calendar.getInstance();

    private DatePickerDialog datePickerDialog;
    //final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);

    @OnClick(R.id.date) void onDateFieldClick(View v) {
            //datePickerDialog.setVibrate(/*isVibrate()*/ true);
            datePickerDialog.setYearRange(1985, 2028);
            datePickerDialog.setCloseOnSingleTapDay(/*isCloseOnSingleTapDay()*/false);
            datePickerDialog.show(getFragmentManager(), DATEPICKER_TAG);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        //Toast.makeText(getActivity(), "new date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
        date.setText(day + " " + new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)] + " " + year);
        calendar.set(year,month,day);
        dateLong = calendar.getTime().getTime();
    }


    public TimeDateFilterFragment(){}

    private EditText parentView;
    public static TimeDateFilterFragment newInstance(EditText clickedView){
        TimeDateFilterFragment p = new TimeDateFilterFragment();
        p.parentView = clickedView;
        return p;
    }
    public View myOnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        View view = inflater.inflate(R.layout.fragment_date_time_filter,container);
        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);

        ButterKnife.inject(this, view);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.AM_PM, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTimeAMPM.setAdapter(adapter);
        endTimeAMPM.setAdapter(adapter);

        //getDialog().setTitle("Filter by journey start time");
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder b =  new AlertDialog.Builder(getActivity())
                .setTitle("Filter by journey start time")
                .setPositiveButton("ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (startTimeAMPM.getSelectedItem().toString() == "AM") {
                                    startTimeLong = Time.valueOf(startTime.getText().toString()+":00:00").getTime();
                                }
                                else {
                                    startTimeLong = Time.valueOf(
                                                        (startTime.getText().toString() + 12)
                                                                +":00:00"
                                                    ).getTime();
                                }
                                if (endTimeAMPM.getSelectedItem().toString() == "AM") {
                                    endTimeLong = Time.valueOf(endTime.getText().toString()+":00:00").getTime();
                                }
                                else {
                                    endTimeLong = Time.valueOf(
                                            (endTime.getText().toString() + 12)
                                                    +":00:00"
                                    ).getTime();
                                }

                                parentView.setText(startTime.getText().toString() + startTimeAMPM.getSelectedItem().toString() +
                                        " - " + endTime.getText().toString() + endTimeAMPM.getSelectedItem().toString() +
                                        " on " + date.getText().toString());
                                dialog.dismiss();
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );
        b.setView(this.myOnCreateView(getActivity().getLayoutInflater(),null,savedInstanceState));
        return b.create();
    }






/*    public void onResume()
    {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        //window.setGravity(Gravity.CENTER);
    }
*/



}