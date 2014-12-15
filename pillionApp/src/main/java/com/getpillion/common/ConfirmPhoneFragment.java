package com.getpillion.common;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getpillion.R;
import com.getpillion.models.User;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;

public class ConfirmPhoneFragment extends Fragment {

    @InjectView(R.id.error)
    TextView error;
    @InjectView(R.id.success)
    TextView success;
    @InjectView(R.id.warning)
    TextView warning;
    SharedPreferences sharedPref;
    Editor sharedPrefEditor;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        sharedPref = getActivity().getApplicationContext().getSharedPreferences(
                Constant.PREFS_NAME, 0);
        sharedPrefEditor = sharedPref.edit();

        View v = inflater.inflate(R.layout.fragment_confirm_phone, null);
        ButterKnife.inject(this, v);

        User user = User.findById(User.class,sharedPref.getLong("userId",0L));
        String phoneNo = user.phone;
        if ( phoneNo != null){
           displayMessage("Confirmed Phone Number - " + phoneNo, "success");
        }
        return v;
    }

    @NotEmpty(messageId = R.string.non_empty_field)
    @InjectView(R.id.phone)
    EditText phone;

    @OnClick(R.id.confirm) void confirmPhone(View v){
        if (Helper.fieldsHaveErrors(getActivity()))
            return;

        final String phoneNo = phone.getText().toString();
        sharedPrefEditor.putString("UnconfirmedPhoneNo",phoneNo);
        String randomHash = Long.toHexString(Double.doubleToLongBits(Math.random()));
        sharedPrefEditor.putString("UnconfirmedPhoneNoHash",randomHash);
        sharedPrefEditor.commit();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, randomHash, null, null);
            displayMessage("SMS sent. The app will now wait for 1 minute to see if the sms comes back. Standby Sargent", "warning");

            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        for (int i=0; i < 60; i += 5 ){
                            String phoneNo = sharedPref.getString("ConfirmedPhoneNo",null);
                            if ( phoneNo != null){
                                return true;
                            }
                            Thread.sleep(5000);
                        }

                    } catch (Exception e) {
                       e.printStackTrace();
                       return null;
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if (result == null) {
                        displayMessage("Error happened while waiting for the SMS. Please choose 'Talk to Founders' option in the menu & let us know.","error");
                    }
                    else if (result) {
                        displayMessage("Confirmed Phone Number - " + phoneNo,"success");
                        Toast.makeText(getActivity(),"Congrats. Phone number confirmed",Toast.LENGTH_LONG).show();
                    }
                    else {
                        displayMessage("The sms never came :(. Sorry but we do not take your claim that the number belongs to you. Mind trying again ?","error");
                    }
                }
            }.execute();

        } catch (Exception e) {
            displayMessage("SMS sending failed. Please try again.","error");
            e.printStackTrace();
        }
    }

    private void displayMessage(String msg, String type){
        error.setVisibility(View.GONE);
        warning.setVisibility(View.GONE);
        success.setVisibility(View.GONE);

        if (type.equals("error")){
            error.setText(msg);
            error.setVisibility(View.VISIBLE);
        }
        else if (type.equals("warning")){
            warning.setText(msg);
            warning.setVisibility(View.VISIBLE);
        }
        else {
            success.setVisibility(View.VISIBLE);
            success.setText(msg);
        }
    }


}