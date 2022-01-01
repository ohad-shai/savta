package com.ohadshai.savta.ui.dialogs;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.ohadshai.savta.R;
import com.ohadshai.savta.utils.IntentUtils;

/**
 * Represents a dialog for showing about the application.
 */
public class AboutDialog extends DialogFragment {
    private final String DIALOG_TAG = "dialog_about";
    private final AppCompatActivity _activity;

    private AboutDialog(@NonNull AppCompatActivity activity) {
        _activity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_about, null);

        TextView txtAppVersion = view.findViewById(R.id.txtAppVersion);
        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            txtAppVersion.setText(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView txtProjectGit = view.findViewById(R.id.txtProjectGit);
        txtProjectGit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.startWebBrowser(requireActivity(), getResources().getString(R.string.about_project_git_link));
            }
        });

        TextView txtOhadLinkedIn = view.findViewById(R.id.txtOhadLinkedIn);
        txtOhadLinkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.startWebBrowser(requireActivity(), getResources().getString(R.string.about_ohad_linkedin_link));
            }
        });

        TextView txtOhadGitHub = view.findViewById(R.id.txtOhadGitHub);
        txtOhadGitHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.startWebBrowser(requireActivity(), getResources().getString(R.string.about_ohad_github_link));
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
        builder.setView(view);
        return builder.create();
    }

    //region Public Static API

    /**
     * Makes a new instance of the dialog.
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
     * Shows the dialog.
     */
    public void show() {
        super.show(_activity.getSupportFragmentManager(), DIALOG_TAG);
    }

    //endregion

}
