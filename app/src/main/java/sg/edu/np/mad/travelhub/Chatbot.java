package sg.edu.np.mad.travelhub;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Chatbot extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private EditText chatInput;
    private GenerativeModelFutures model;
    private List<ChatMessage> messageList;
    private ChatAdapter chatAdapter;

    int color1;
    int color2;
    int color3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        // Initialize views
        chatInput = findViewById(R.id.chat_input);
        messageList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.chat_recyclerview);
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton send_btn = findViewById(R.id.send_button);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    scrollToBottom();
                    callGemini(view);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Initialize GenerativeModel
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyDlo1MaJBkodwsEWAnSOFyw_iWdmeXiqFE"); // Replace with your API key
        model = GenerativeModelFutures.from(gm);

        // Setup UI and colors (omitted for brevity)
        setupUI();

        // Insert images
        ImageButton attach_btn = findViewById(R.id.attach_btn);
        attach_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        // Set color of the status bar
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#DFDFDF"));

        // Back button
        ImageView closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> onBackPressed());

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

        Button prompt1 = findViewById(R.id.button_prompt1);
        prompt1.setBackgroundTintList(ColorStateList.valueOf(color1));
        Button prompt2 = findViewById(R.id.button_prompt2);
        prompt2.setBackgroundTintList(ColorStateList.valueOf(color1));
        Button prompt3 = findViewById(R.id.button_prompt3);
        prompt3.setBackgroundTintList(ColorStateList.valueOf(color1));
        Drawable attach_bg = ContextCompat.getDrawable(this, R.drawable.chat_input_bg);
        attach_bg.setTint(color1);
        attach_btn.setBackgroundDrawable(attach_bg);
        Drawable input_bg = ContextCompat.getDrawable(this, R.drawable.chat_input_bg);
        input_bg.setTint(color1);
        send_btn.setBackgroundDrawable(input_bg);
        GradientDrawable borderDrawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.chat_msg_bg);
        int strokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        borderDrawable.setStroke(strokeWidth, color2);

        LinearLayout header = findViewById(R.id.header);
        ColorStateList colorStateList = ColorStateList.valueOf(color1);
        header.setBackgroundTintList(colorStateList);
    }

    private void setupUI() {
        // Setup your UI components, colors, and other initializations here
        // Example: Setting up buttons, text colors, etc.
    }

    public void inputDo(View view) {
        chatInput.setText("What can you do?");
    }

    public void inputMade(View view) {
        chatInput.setText("How are you made?");
    }

    public void inputCreated(View view) {
        chatInput.setText("Who created you?");
    }

    public void callGemini(View view) throws FileNotFoundException {
        // Get user input from EditText
        String userInput = chatInput.getText().toString();

        // Define system instructions
        Content.Builder systemInstructionsBuilder = new Content.Builder();
        systemInstructionsBuilder.setRole("model");
        systemInstructionsBuilder.addText("Do not bold any of your answers. You are Pathfinder, an AI assistant who works for PlanHub. PlanHub is a travel companion, designed to simplify every aspect of the user's journey. PlanHub offers a high level of customizability, intuitive event management and currency conversion, ensuring effortless planning and exploration with community engagement through interactive travel journals. PlanHub elevates the user's travel experience, making every adventure memorable and hassle-free.\n" +
                "When the user asks \"What can you do?\" or \"hi\", answer with this:\n" +
                "\"Hi there! I'm Pathfinder, your AI travel companion from PlanHub. I'm here to help you plan the perfect trip, answer any queries you might have and make sure your adventure is as smooth and enjoyable as possible. Here's what I can do for you:\n" +
                "\n" +
                "Destination Inspiration: Tell me your interests (history, adventure, food, etc.) and budget, and I'll suggest destinations that fit the bill.\n" +
                "Itinerary Creation: I can provide you a detailed itinerary, including activities, transportation, accommodation recommendations.\n" +
                "Travel Expertise: I can provide insights on your desired places to visit and offer you pleasant wonders to visit.\n" +
                "\n" +
                "My goal is to be your AI travel assistant, making your trip effortless, memorable, and full of exciting discoveries. What can I help you with today?\"\n" +
                "\n" +
                "When the user asks \"How are you made?\" answer with this:\n" +
                "\"I am made using Gemini 1.5 Flash, made to reply to your queries as fast as possible!\"\n" +
                "\n" +
                "When the user asks \"Who created you?\" answer with this:\n" +
                "\"PlanHub and I, Pathfinder, is created by a group of 5 young students, made to make trip planning more efficient and effortless. \""); // Add your system instructions here
        Content systemInstructions = systemInstructionsBuilder.build();

        // Initialize an empty chat history every load
        List<Content> history = new ArrayList<>();
        history.add(systemInstructions);

        // Create a new user message
        Content.Builder userMessageBuilder = new Content.Builder();
        userMessageBuilder.setRole("user");
        userMessageBuilder.addText(userInput);

        // Add image bitmap to the message
        if (selectedImageUri != null) {
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(selectedImageUri);
            Drawable drawable = Drawable.createFromStream(inputStream, selectedImageUri.toString());
            Bitmap image = ((BitmapDrawable) drawable).getBitmap();
            userMessageBuilder.addImage(image);
        }

        Content userMessage = userMessageBuilder.build();
        messageList.add(new ChatMessage(userInput, true));
        chatAdapter.notifyDataSetChanged();

        // Initialize the chat with the history with system instructions
        ChatFutures chat = model.startChat(history);

        // Send message
        ListenableFuture<GenerateContentResponse> response = chat.sendMessage(userMessage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();
                    messageList.add(new ChatMessage(resultText, false));
                    chatAdapter.notifyDataSetChanged();
                    RecyclerView recyclerView = findViewById(R.id.chat_recyclerview);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            }, this.getMainExecutor());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            try {
                // Convert URI to Bitmap
                Bitmap bitmap = getBitmapFromUri(selectedImageUri);

                // Handle text input with the image
                String userInput = chatInput.getText().toString();

                // Create a new user message with both text and image bitmap
                Content.Builder userMessageBuilder = new Content.Builder();
                userMessageBuilder.setRole("user");
                userMessageBuilder.addText(userInput);
                userMessageBuilder.addImage(bitmap); // Add image bitmap to the message
                Content userMessage = userMessageBuilder.build();

                // Send the message
                ChatFutures chat = model.startChat(new ArrayList<>()); // Initialize chat without history
                ListenableFuture<GenerateContentResponse> response = chat.sendMessage(userMessage);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                        @Override
                        public void onSuccess(GenerateContentResponse result) {
                            String resultText = result.getText();
                            messageList.add(new ChatMessage(resultText, false));
                            chatAdapter.notifyDataSetChanged();
                            scrollToBottom();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                        }
                    }, this.getMainExecutor());
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws FileNotFoundException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        return BitmapFactory.decodeStream(inputStream);
    }

    private void scrollToBottom() {
        RecyclerView recyclerView = findViewById(R.id.chat_recyclerview);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }
}

