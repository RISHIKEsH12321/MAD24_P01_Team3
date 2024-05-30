package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

public class ViewPlaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_place);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the intent that started this activity
        Intent intent = getIntent();
        // Check if the intent contains an extra with the key "place"
        if (intent.hasExtra("place")) {
            // Retrieve the Place object from the intent
            Place place = (Place) intent.getSerializableExtra("place");

            // Setting the image background
            ImageView backgroundTopPlacePhotoImage = findViewById(R.id.backgroundTopPlacePhotoImage);
            TextView placeName = findViewById(R.id.placeName);
            TextView placeLocation = findViewById(R.id.placeLocation);
            TextView ratingText = findViewById(R.id.ratingText);
            TextView descriptionText = findViewById(R.id.descriptionText);
            RatingBar ratingBar = findViewById(R.id.ratingBar);

            Glide.with(this)
                    .load(place.getImgUrl()) // Load the image URL from the Place object
                    .transform(new CenterCrop(), new FitCenter())
                    .into(backgroundTopPlacePhotoImage);

            // Setting the content of the place details
            placeName.setText(place.getName());
            placeLocation.setText(place.getLocation());
            ratingText.setText(String.valueOf(place.getRating()));
            descriptionText.setText(place.getDescription());
            float rating = (float) place.getRating(); // Example: Rating of 4.5 stars
            ratingBar.setRating(rating);

            ImageButton viewPlaceBackBtn = findViewById(R.id.viewPlaceBackBtn);
            viewPlaceBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            // Setting onClickListener for going to add Events Management Page
           Button addToPlanBtn = findViewById(R.id.addToPlanBtn);
            addToPlanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an Intent to start the ViewPlaceActivity
                    Intent intent = new Intent(ViewPlaceActivity.this, EventManagement.class);
                    // Pass the Place object as an extra in the intent
                    intent.putExtra("place", place);
                    // Start the ViewPlaceActivity
                    startActivity(intent);
                }
            });

        } else {
            // Handle the case where the intent does not contain the "place" extra
            // You may display an error message or finish the activity
            Toast.makeText(this, "No place data found", Toast.LENGTH_SHORT).show();
            finish(); // Finish the activity if no place data is found
        }
    }
}