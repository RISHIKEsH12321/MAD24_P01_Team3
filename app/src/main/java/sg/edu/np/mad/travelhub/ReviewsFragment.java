package sg.edu.np.mad.travelhub;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<PlaceReview> reviewsList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve arguments containing reviews list
        if (getArguments() != null) {
            reviewsList = getArguments().getParcelableArrayList("placeReviewsList");
        } else {
            reviewsList = new ArrayList<>(); // Initialize empty list if no arguments found
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        recyclerView = view.findViewById(R.id.reviewsRV);
        View noReviewsMessage = view.findViewById(R.id.no_reviews_message);

        if (reviewsList != null && !reviewsList.isEmpty()) {
            // Set up RecyclerView with the adapter if there are reviews
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            Place_Reviews_Recyclerview_Adapter adapter = new Place_Reviews_Recyclerview_Adapter(getContext(), reviewsList);
            recyclerView.setAdapter(adapter);

            // Hide the no reviews message
            noReviewsMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            // Show the no reviews message and hide the RecyclerView
            recyclerView.setVisibility(View.GONE);
            noReviewsMessage.setVisibility(View.VISIBLE);
        }

        return view;
    }
}