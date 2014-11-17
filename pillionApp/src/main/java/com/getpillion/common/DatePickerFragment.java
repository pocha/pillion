package com.getpillion.common;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.getpillion.R;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends Fragment implements OnDateSetListener {
    private EditText v;
    public Date date;
    final Calendar calendar = Calendar.getInstance();

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        //Toast.makeText(getActivity(), "new date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
        calendar.set(year, month, day);
        date = calendar.getTime();
        setDateField();
    }

    public void setDate(Long dateLong){
        date = new Date(dateLong);
        calendar.setTime(date);
        setDateField();
    }

    private void setDateField(){
        v.setText(calendar.get(Calendar.DAY_OF_MONTH) + " " +
                new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)] + " " +
                calendar.get(Calendar.YEAR));
    }

    /*private SherlockFragmentActivity parentActivity;
    @Override
    public void onAttach(Activity activity){
        parentActivity = (SherlockFragmentActivity)activity;
    }*/

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);

        TypedArray a = activity.obtainStyledAttributes(attrs, R.styleable.PickerFragment);

        try {
            String dateInfo = a.getText(R.styleable.PickerFragment_value).toString();
            if (dateInfo != null) {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInfo);
            }
            //style = a.getInt(R.styleable.TimePickerFragment_style,-1);
        }catch (Exception e) { //nothing doing
            date = new Date();
        }
        calendar.setTime(date);

        a.recycle();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
       v = (EditText) inflater.inflate(R.layout.picker_edittext,container,false);
       setDateField();

        v.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent event) {
               if (event.getAction() != MotionEvent.ACTION_UP)
                   return true;
               DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(DatePickerFragment.this,
                       calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                       false);

               datePickerDialog.setYearRange(1985, 2028);
               datePickerDialog.setCloseOnSingleTapDay(/*isCloseOnSingleTapDay()*/false);
               //datePickerDialog.show(parentActivity.getSupportFragmentManager(),"some_tag");
               datePickerDialog.show(getFragmentManager(),"some_tag");
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