package com.example.tomovico.stuffcollector;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

public class ExitActivityDialog extends DialogFragment {

    public ExitActivityDialog() {
        Log.e("ExitActivityDialog", "Konstruktor ExitActivityDialog");
    }


    // Definicija interfejsa pomocu koga vracam evente nazad u host activity
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Instanca interfejsa
    NoticeDialogListener mListner;

    // Instanciram NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("ExitAD", "Usao u onAttach");
        try {
            mListner = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Kreiram AlertDialog Builder objrkat
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Log.e("onCreateDialog", "Usao u onCreateDialog");
        // Postavljam osnovne parametre AlertDialoga
        builder.setTitle(R.string.alert_dialog_name);
        Log.e("onCreateDialog", "Prosao setTitle");
        builder.setMessage(R.string.alert_dialog_msg);
        Log.e("onCreateDialog", "Prosao setMessage");
        builder.setPositiveButton(R.string.alert_pozitive_msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("ExitActivityDialog", "onClick POSITIVE");
                mListner.onDialogPositiveClick(ExitActivityDialog.this);
            }
        });
        Log.e("onCreateDialog", "Prosao setPositiveButton");

        builder.setNegativeButton(R.string.alert_negative_msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("ExitActivityDialog", "onClick NEGATIVE");
                //if (dialog != null)
                //  dialog.dismiss();
            }
        });
        Log.e("onCreateDialog", "Prosao setNegativeButton");
        return builder.create();
    }
}

