package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Message extends AppCompatActivity {
    FirebaseUser firebaseUser;
    TextView otheruseridview;
    ImageButton sendMessageBtn;
    ImageView backBtn;
    EditText messageInput;
    RecyclerView recyclerView;
    String messageroomsenderId, messageroomreceiverId, otherUserId, currentUserId, otherUserUid, currentuserUid;
    FirebaseDatabase db;
    DatabaseReference dbReferenceSender, dbReferenceReceiver;
    MessageAdapter messageAdapter;
    private List<UserMessage> messages;
    int color1;
    int color2;
    int color3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Msgmain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Change themes
        SharedPreferences preferences = getSharedPreferences("spinner_preferences", MODE_PRIVATE);
        int selectedSpinnerPosition = preferences.getInt("selected_spinner_position", 0);
        String selectedTheme = getResources().getStringArray(R.array.themes)[selectedSpinnerPosition];

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

        LinearLayout header = findViewById(R.id.header);
        ColorStateList colorStateList = ColorStateList.valueOf(color1);
        header.setBackgroundTintList(colorStateList);

        //get intent extra
        Intent intent = getIntent();
        otherUserUid = intent.getStringExtra("userUid");

        //use intent extra to get the other user's id
        db = FirebaseDatabase.getInstance();
        String getIdPath = "Users/" + otherUserUid;
        DatabaseReference getIdRef = db.getReference(getIdPath);
        getIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userObject = snapshot.getValue(User.class);
                    if (userObject!=null) {
                        otherUserId = userObject.getId();
                        otheruseridview.setText(otherUserId);
                    }
                    else {
                        Log.w("UserNotFound", "User object not found in database");
                    }
                }
                else {
                    Log.d("NoUserDataFound", "No user data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Error retrieving user data", error.toException());
            }
        });


        //get current user's Id
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentuserUid = firebaseUser.getUid();
        getIdPath = "Users/" + currentuserUid;
        getIdRef = db.getReference(getIdPath);
        getIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userObject = snapshot.getValue(User.class);
                    if (userObject!=null) {
                        currentUserId = userObject.getId();
                    }
                    else {
                        Log.w("UserNotFound", "User object not found in database");
                    }
                }
                else {
                    Log.d("NoUserDataFound", "No user data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Error retrieving user data", error.toException());
            }
        });

        //define views
        otheruseridview = findViewById(R.id.user_id);
        sendMessageBtn = findViewById(R.id.send_button);
        backBtn = findViewById(R.id.close_button);
        messageInput = findViewById(R.id.chat_input);
        recyclerView = findViewById(R.id.chat_recyclerview);

        sendMessageBtn.setBackgroundTintList(colorStateList);
        //recyclerview
        messageAdapter = new MessageAdapter(this);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set logic for back button first to go back to search
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //make a messageroom id
        if (otherUserUid!=null) {
            messageroomsenderId = currentuserUid + otherUserUid;
            messageroomreceiverId = otherUserUid + currentuserUid;
            //define db references
            dbReferenceSender = FirebaseDatabase.getInstance().getReference("Message").child(messageroomsenderId);
            dbReferenceReceiver = FirebaseDatabase.getInstance().getReference("Message").child(messageroomreceiverId);
        }


        if (dbReferenceSender != null) {
            dbReferenceSender.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<UserMessage> messages = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        UserMessage userMessage = dataSnapshot.getValue(UserMessage.class);
                        messages.add(userMessage);
                    }

                    messageAdapter.clear();
                    for (UserMessage msg : messages) {
                        messageAdapter.add(msg);
                    }
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        //send message
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString();
                //if message without spaces is not empty, send
                if (!message.trim().isEmpty()) {
                    createMessage(message);
                }
                else {
                    Toast.makeText(Message.this, "Message cannot be empty", Toast.LENGTH_SHORT);
                }
            }
        });

    }
    private void createMessage(String message) {
        //make a random message id
        String messageId = dbReferenceSender.push().getKey(); // use push() because it will be chronological, resulting in correct order of msgs
        UserMessage userMessage = new UserMessage(messageId, message, currentuserUid);
        messageAdapter.add(userMessage);

        dbReferenceSender.child(messageId).setValue(userMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Message.this, "Failed to send message", Toast.LENGTH_SHORT);
                    }
        });
        dbReferenceReceiver.child(messageId).setValue(userMessage);
        recyclerView.scrollToPosition(messageAdapter.getItemCount()-1);
        messageInput.setText("");


    }
}