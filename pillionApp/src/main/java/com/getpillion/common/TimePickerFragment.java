package com.getpillion.common;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.getpillion.R;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.sql.Time;
import java.util.Date;

public class TimePickerFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {
    private EditText v;
    public Time time = null;
    private int style = -1;

    private void setEditTextValue() {
        if (time.getHours() > 12)
            v.setText(
                    (time.getHours() - 12) + ":" +
                            String.format("%02d", time.getMinutes() ) + " PM"
            );
        else if (time.getHours() == 0)
            v.setText(
                    "12:" +
                    String.format("%02d", time.getMinutes() ) + " AM"
            );
        else
            v.setText(
                    time.getHours() + ":" +
                            String.format("%02d", time.getMinutes() ) + " AM"
            );
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        //Toast.makeText(MainActivity.this, "new time:" + hourOfDay + "-" + minute, Toast.LENGTH_LONG).show();
        time = Time.valueOf(hourOfDay + ":" + minute + ":00");
        setEditTextValue();
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);

        TypedArray a = activity.obtainStyledAttributes(attrs, R.styleable.TimePickerFragment);

        try {
            String timeInfo = a.getText(R.styleable.TimePickerFragment_value).toString();
            if (timeInfo != null)
                time = Time.valueOf(timeInfo);
            else
                time = new Time((new Date()).getTime());

            //style = a.getInt(R.styleable.TimePickerFragment_style,-1);
        }catch (Exception e) { //nothing doing
            time = new Time((new Date()).getTime());
        }

        a.recycle();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
       v = (EditText) inflater.inflate(R.layout.picker_edittext,container,false);
       if (getActivity().getClass().getSimpleName().equals("NewRouteActivityOnStart") /*style > -1*/ )
           v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

       setEditTextValue();

        v.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent event) {
               if (event.getAction() != MotionEvent.ACTION_UP)
                   return true;
               final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(TimePickerFragment.this,
                       time.getHours() , time.getMinutes(),
                       false, false);
               timePickerDialog.setCloseOnSingleTapMinute(false);
               timePickerDialog.show(getFragmentManager(), "some_time_picker_tag");
               return true;
           }
       });
       return v;
    }


/*    public void onResume()
    {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        //window.setGravity(Gravity.CENTER);
    }
*/



}