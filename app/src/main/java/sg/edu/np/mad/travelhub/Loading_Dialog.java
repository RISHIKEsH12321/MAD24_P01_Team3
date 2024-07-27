package sg.edu.np.mad.travelhub;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

public class Loading_Dialog {
    private Context context;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    int color1;
    int color2;
    int color3;

    public Loading_Dialog(){}

    public Loading_Dialog(Context context) {
        this.context = context;
    }

    public void startLoadingDialog() {
        SharedPreferences preferences = context.getSharedPreferences("spinner_preferences", Context.MODE_PRIVATE);
        int selectedSpinnerPosition = preferences.getInt("selected_spinner_position", 0);
        String[] themes = context.getResources().getStringArray(R.array.themes);
        String selectedTheme = themes[selectedSpinnerPosition];

        switch (selectedTheme) {
            case "Default":
                color1 = context.getResources().getColor(R.color.main_orange);
                color2 = context.getResources().getColor(R.color.main_orange);
                color3 = context.getResources().getColor(R.color.main_orange_bg);
                break;
            case "Watermelon":
                color1 = context.getResources().getColor(R.color.wm_green);
                color2 = context.getResources().getColor(R.color.wm_red);
                color3 = context.getResources().getColor(R.color.wm_red_bg);
                break;
            case "Neon":
                color1 = context.getResources().getColor(R.color.nn_pink);
                color2 = context.getResources().getColor(R.color.nn_cyan);
                color3 = context.getResources().getColor(R.color.nn_cyan_bg);
                break;
            case "Protanopia":
                color1 = context.getResources().getColor(R.color.pro_purple);
                color2 = context.getResources().getColor(R.color.pro_green);
                color3 = context.getResources().getColor(R.color.pro_green_bg);
                break;
            case "Deuteranopia":
                color1 = context.getResources().getColor(R.color.deu_yellow);
                color2 = context.getResources().getColor(R.color.deu_blue);
                color3 = context.getResources().getColor(R.color.deu_blue_bg);
                break;
            case "Tritanopia":
                color1 = context.getResources().getColor(R.color.tri_orange);
                color2 = context.getResources().getColor(R.color.tri_green);
                color3 = context.getResources().getColor(R.color.tri_green_bg);
                break;
            default:
                color1 = context.getResources().getColor(R.color.main_orange);
                color2 = context.getResources().getColor(R.color.main_orange);
                color3 = context.getResources().getColor(R.color.main_orange_bg);
                break;
        }

        builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.loading_dialog, null);

        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(color1)); // Change color here

        builder.setView(dialogView);
        builder.setCancelable(false);

        dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public boolean isDialogShowing() {
        return dialog != null && dialog.isShowing();
    }
}