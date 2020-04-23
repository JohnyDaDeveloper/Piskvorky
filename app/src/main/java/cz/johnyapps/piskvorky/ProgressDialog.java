package cz.johnyapps.piskvorky;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.StringRes;

public class ProgressDialog {
    private Context context;
    private AlertDialog dialog;

    public ProgressDialog(Context context) {
        this.context = context;
    }

    public void show(@StringRes int message) {
        show(context.getString(message));
    }

    public void show(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.dialog_progress)
                .setCancelable(false);

        dialog = builder.create();
        dialog.show();

        TextView messageTextView = dialog.findViewById(R.id.messageTextView);
        messageTextView.setText(message);
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
