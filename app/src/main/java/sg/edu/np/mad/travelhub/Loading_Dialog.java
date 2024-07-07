package sg.edu.np.mad.travelhub;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

public class Loading_Dialog {
    private Activity activity;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;

    public Loading_Dialog(){};

    Loading_Dialog(Activity myActivity){
        this.activity = myActivity;
    }

    void startLoadingDialog(){
        builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
        builder.setCancelable(false);

        dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    void dismissDialog(){
        dialog.dismiss();
    }
}
