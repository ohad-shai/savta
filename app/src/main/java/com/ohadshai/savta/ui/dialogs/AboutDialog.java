package com.ohadshai.savta.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.ohadshai.savta.R;

/**
 * Represents a dialog for showing about the application.
 */
public class AboutDialog extends DialogFragment {
    private final AppCompatActivity _activity;

    private AboutDialog(@NonNull AppCompatActivity activity) {
        _activity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_about, null);

        ImageButton imgBtnClose = view.findViewById(R.id.dialog_about_img_btn_close);
        imgBtnClose.setOnClickListener(v -> {
            dismiss();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
        builder.setView(view);
        return builder.create();
    }

    //region Public Static API

    /**
     * Makes a new instance of the "About" dialog.
     *
     * @param activity The caller activity.
     * @return Returns the instance of the dialog.
     */
    public static AboutDialog make(@NonNull AppCompatActivity activity) {
        return new AboutDialog(activity);
    }

    //endregion

    //region Public API

    /**
     * Shows the "About" dialog.
     *
     * @return Returns the instance of the dialog.
     */
    public AboutDialog show() {
        this.show(_activity.getSupportFragmentManager(), "dialog_about");
        return this;
    }

    //endregion

}
