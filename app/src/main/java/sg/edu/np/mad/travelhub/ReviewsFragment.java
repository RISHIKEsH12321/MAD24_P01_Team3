package sg.edu.np.mad.travelhub;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        SharedPreferences preferences = getActivity().getSharedPreferences("spinner_preferences", MODE_PRIVATE);
        int selectedSpinnerPosition = preferences.getInt("selected_spinner_position", 0);
        String selectedTheme = getResources().getStringArray(R.array.themes)[selectedSpinnerPosition];

        int color1;
        int color2;
        int color3;

        switch (selectedTheme) {
            case "Default":
                color1 = getResources().getColor(R.color.main_orange);
                color2 = getResources().getColor(R.color.main_orange);
                color3 = getResources().getColor(R.color.main_orange_bg);
                break;
            case "Watermelon":
                color1 = getResources().getColor(R.color.wm_green);
                color2 = getResources().getColor(R.color.wm_red);
                color3 = getResources().getColor(R.color.wm_red_bg);
                break;
            case "Neon":
                color1 = getResources().getColor(R.color.nn_pink);
                color2 = getResources().getColor(R.color.nn_cyan);
                color3 = getResources().getColor(R.color.nn_cyan_bg);
                break;
            case "Protanopia":
                color1 = getResources().getColor(R.color.pro_purple);
                color2 = getResources().getColor(R.color.pro_green);
                color3 = getResources().getColor(R.color.pro_green_bg);
                break;
            case "Deuteranopia":
                color1 = getResources().getColor(R.color.deu_yellow);
                color2 = getResources().getColor(R.color.deu_blue);
                color3 = getResources().getColor(R.color.deu_blue_bg);
                break;
            case "Tritanopia":
                color1 = getResources().getColor(R.color.tri_orange);
                color2 = getResources().getColor(R.color.tri_green);
                color3 = getResources().getColor(R.color.tri_green_bg);
                break;
            default:
                color1 = getResources().getColor(R.color.main_orange);
                color2 = getResources().getColor(R.color.main_orange);
                color3 = getResources().getColor(R.color.main_orange_bg);
                break;
        }

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

        TextView reviewsTitle = view.findViewById(R.id.reviewsTitle);
        reviewsTitle.setTextColor(color1);

        return view;
    }
}