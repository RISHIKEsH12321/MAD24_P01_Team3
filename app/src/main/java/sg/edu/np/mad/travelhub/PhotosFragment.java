package sg.edu.np.mad.travelhub;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public class PhotosFragment extends Fragment {
    private ArrayList<String> placePhotos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placePhotos = getArguments().getStringArrayList("placePhotos");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.photosRV);

        if (placePhotos != null && !placePhotos.isEmpty()) {
            // Set up RecyclerView with the adapter if there are photos
            Place_Photos_RecyclerView_Adapter adapter = new Place_Photos_RecyclerView_Adapter(getContext(), placePhotos);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        } else {
            // Handle the case where there are no photos
            recyclerView.setVisibility(View.GONE);
            View noPhotosMessage = view.findViewById(R.id.no_photos_message); // A TextView or other view for message
            noPhotosMessage.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
