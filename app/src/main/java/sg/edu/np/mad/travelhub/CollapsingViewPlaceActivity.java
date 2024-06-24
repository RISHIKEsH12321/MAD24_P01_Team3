package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CollapsingViewPlaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collapsing_view_place);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FrameLayout bottomSheet = findViewById(R.id.bottomSheet);

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setDraggable(true);
        bottomSheet.post(() ->{
            behavior.setPeekHeight(1260, true);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        TabLayout tabLayout = findViewById(R.id.PlaceDetailsTabs);
        ViewPager2 viewPager = findViewById(R.id.fragmentTab);
        viewPager.setUserInputEnabled(false);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // To actually get back the current behavior state of the bottomsheet
//                int currentState = behavior.getState();
//                bottomSheet.post(() ->{
//                    if (currentState == BottomSheetBehavior.STATE_COLLAPSED){
//                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    } else{
//                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                    }
//                });
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // auto expand the bottomsheet for better viewing
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        PDFragmentAdapter adapter = new PDFragmentAdapter(this);

        // Create AboutFragment and pass description
        AboutFragment aboutFragment = new AboutFragment();
        Bundle aboutBundle = new Bundle();
        aboutBundle.putString("description", "getPlaceDescription()"); // Assuming getPlaceDescription() returns the description string
        aboutFragment.setArguments(aboutBundle);
        adapter.addFragment(aboutFragment, "About");

        // Create ReviewsFragment and pass the reviews
        PlaceReview matthewHoganReview = new PlaceReview(
                "Matthew Hogan",
                "https://www.google.com/maps/contrib/108522641964881996448/reviews",
                "https://lh3.googleusercontent.com/a/ACg8ocJEWau_bviyS1BEzMEjDU8cso2nchWRKl27EKnx4g4aaGjHZw=s128-c0x00000000-cc-rp-mo",
                1,
                "in the last week",
                "I put it to Google that you are knowing and willingly allowing price gouging plumbing businesses to be on top of the search bar when they search my business name. I put it to Google that some people can not even find our web page when former clients search our company, just other companies that use us and then charge the client 400% to 700% what we charge them appear above us or we are not found at all.\n\nI put it to Google that you take around $100,000 to $400,000 in advertising revenue per month from one single plumbing company and I put it to you that you know there are many other plumbing companies that act in a similar way.\n\nI put it to Google that you are knowingly and willingly price gouging Australian citizens through entities that pay advertising revenue to Google.\n\nPlease contact us.",
                1718780823,
                false
        );
        PlaceReview floYeowReview = new PlaceReview(
                "Flo Yeow",
                "https://www.google.com/maps/contrib/101597484841229205770/reviews",
                "https://lh3.googleusercontent.com/a-/ALV-UjXUjt81LVGVwQ1JNG41FQpkYJO_Uc7z5vfFdS_wh94R2CMvK8TxrdM=s128-c0x00000000-cc-rp-mo-ba8",
                5,
                "a year ago",
                "This is the Sales side of the couple of offices that Google has in Sydney. The engineering side is nicer but this is nice too.\n\nStraightforward set up, in a single column building, stairs and lift in the middle, and everything else around it. Professional baristas at The Press are really lovely people, and when I visited the dining hall was serving Carbonara on a cheese wheel. Make sure you stand at the rooftop balcony and enjoy the breeze and view of the surroundings.\n\nWith such gorgeous amenities and benefits, what's there not to like about working here.",
                1670547584,
                false
        );
        PlaceReview elaineGoldsackReview = new PlaceReview(
                "Elaine Goldsack",
                "https://www.google.com/maps/contrib/110432054954844321941/reviews",
                "https://lh3.googleusercontent.com/a/ACg8ocIuV4oLmL0srTzmeunV7J0vF5qNtSrEhUQQZtK1lQWnHtD7ww=s128-c0x00000000-cc-rp-mo",
                5,
                "2 weeks ago",
                "I’m so happy I booked in with Pilar for a colour analysis. I always knew Autumn colours suited me best but I never knew exactly what shades to buy or how to wear them together. Pilar was a joy to be with. She’s so friendly and she knows her stuff. She instantly put me at ease. Not only can I shop more confidently knowing what shades suit me best but she’s also made me realise how wearing the right colour for me can completely lift my face. I love make up but when I’m wearing a colour that is right for me, I’m finding myself having a lot more make up free days.",
                1717439195,
                false
        );
        PlaceReview phillipMcCallumReview = new PlaceReview(
                "Phillip McCallum",
                "https://www.google.com/maps/contrib/100072662673282433425/reviews",
                "https://lh3.googleusercontent.com/a-/ALV-UjUbamfbGAzJ_EVbEedEyylwzMcKywDFC_UQENKXL8gllwN64kY=s128-c0x00000000-cc-rp-mo-ba5",
                1,
                "in the last week",
                "Trying to talk to google, I'm 64 and find google YTMusic frustrating for podcasts.\nYTM keeps giving me and playing music or podcasts I don't want. I have downloaded the podcaste I listen too.\nHard to get podcast without going to music.\nGoogle podcast worked well why change it.",
                1718593189,
                false
        );
        PlaceReview hassanBashirReview = new PlaceReview(
                "Hassan Bashir",
                "https://www.google.com/maps/contrib/112090586083780071235/reviews",
                "https://lh3.googleusercontent.com/a/ACg8ocI1-S_ECylGva-XSM7OqDhgXS17GWcKNmS0h1sbN_IICkvmDQ=s128-c0x00000000-cc-rp-mo-ba4",
                1,
                "3 months ago",
                "I reported a fake review on my google page to google a while ago, and it still has not been actioned. I have a record of 5-star reviews on my business, and all of a sudden, someone I never dealt with leaves me a negative review, and there is nothing that I can do about.\nI know google doesn't look at these reviews, and they don't care, but I just want to put my frustration out there.",
                1709165920,
                false
        );

        ArrayList<PlaceReview> placeReviewList = new ArrayList<>();
        placeReviewList.add(matthewHoganReview);
        placeReviewList.add(floYeowReview);
        placeReviewList.add(elaineGoldsackReview);
        placeReviewList.add(phillipMcCallumReview);
        placeReviewList.add(hassanBashirReview);
        ReviewsFragment reviewsFragment = new ReviewsFragment();
        Bundle reviewBundle = new Bundle();
        reviewBundle.putParcelableArrayList("placeReviewsList", placeReviewList);
        reviewsFragment.setArguments(reviewBundle);
        adapter.addFragment(reviewsFragment, "Reviews");



        // Create PhotosFragment and pass photo URLs
        PhotosFragment photosFragment = new PhotosFragment();
        Bundle photosBundle = new Bundle();
        ArrayList<String> photoUrls = new ArrayList<>();
        photoUrls.add("https://buffer.com/library/content/images/size/w1200/2023/10/free-images.jpg");
        photoUrls.add("https://cdn.prod.website-files.com/62d84e447b4f9e7263d31e94/6399a4d27711a5ad2c9bf5cd_ben-sweet-2LowviVHZ-E-unsplash-1.jpeg");
        photoUrls.add("https://img-cdn.pixlr.com/image-generator/history/65bb506dcb310754719cf81f/ede935de-1138-4f66-8ed7-44bd16efc709/medium.webp");
        photosBundle.putStringArrayList("placePhotos", photoUrls); // Assuming getPlacePhotos() returns ArrayList<String>
        photosFragment.setArguments(photosBundle);
        adapter.addFragment(photosFragment, "Photos");

        viewPager.setAdapter(adapter);

        // Set the default tab to AboutFragment (position 0)
        viewPager.setCurrentItem(0, false); // 'false' means no smooth scroll

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getPageTitle(position));
        }).attach();

        ImageButton viewPlaceBackBtn = findViewById(R.id.viewPlaceBackBtn);
        viewPlaceBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AppCompatButton addToPlanBtn = findViewById(R.id.addToPlanBtn);
        addToPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // Create an Intent to start the EventManagementActivity
//                Intent intent = new Intent(ViewPlaceActivity.this, EventManagement.class);
//                // Pass the Place object as an extra in the intent
//                intent.putExtra("place", place);
//                // Start the ViewPlaceActivity
//                startActivity(intent);
                Log.d("AddToBtn", "CLICKED");
            }
        });
    }

    private void refreshScrollViewLayout(){
        ScrollView scrollView = findViewById(R.id.viewPlaceSV);

        // Wait until the ScrollView is laid out to get its height
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Adjust the height to wrap_content
                scrollView.getLayoutParams().height = ScrollView.LayoutParams.WRAP_CONTENT;
                scrollView.requestLayout(); // Refresh layout
            }
        });
    }
}
