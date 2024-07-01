package sg.edu.np.mad.travelhub;

import android.media.Image;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class UserViewHolder extends RecyclerView.ViewHolder {
    CardView userDetails;
    ImageView profImage;
    TextView id, name;

    public UserViewHolder(View view){
        super(view);
        profImage = view.findViewById(R.id.UivProfImage);
        name = view.findViewById(R.id.UtvName);
        id = view.findViewById(R.id.UtvId);
        userDetails = view.findViewById(R.id.userDetails);
    }
}
