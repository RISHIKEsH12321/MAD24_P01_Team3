package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Favorites extends Fragment {
    View view;
    List<PlaceDetails> placeDetailsList = new ArrayList<>();
    private favoritePlaceRVAdapter adapter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler mainHandler = new Handler(Looper.getMainLooper());
    // Firebase references
    private FirebaseDatabase db;
    private DatabaseReference myRef;
    private FirebaseUser fbuser;
    private String uid;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.favoriteRV);
        int spanCount = 2; // 2 columns

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        // Initialize the adapter and assign it to the instance variable
        adapter = new favoritePlaceRVAdapter(getContext(), placeDetailsList);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase
        db = FirebaseDatabase.getInstance();
        fbuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbuser != null) {
            uid = fbuser.getUid();
            myRef = db.getReference("Favourites").child(uid);
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Fetch data from Firebase
        fetchFavoritePlaces();

        return view;
    }

    private void fetchFavoritePlaces() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                placeDetailsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlaceDetails place = snapshot.getValue(PlaceDetails.class);
                    if (place != null) {
                        placeDetailsList.add(place);
                        Log.d("Place", place.getName());
                    }
                }
                Log.d("PlaceListSize", String.valueOf(placeDetailsList.size())); // Log list size
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Code to update UI components
                        adapter.notifyDataSetChanged();
                        if (placeDetailsList.isEmpty()){
                            TextView noFavouritePlace = view.findViewById(R.id.noFavouritePlace);
                            noFavouritePlace.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
