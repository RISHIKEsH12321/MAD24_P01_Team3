package sg.edu.np.mad.travelhub;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;

public class Chatbot extends AppCompatActivity {
    int color1;
    int color2;
    int color3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chatbot);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> onBackPressed());
        
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

        ImageButton send_btn = findViewById(R.id.send_button);
        Drawable input_bg = ContextCompat.getDrawable(this, R.drawable.chat_input_bg);
        input_bg.setTint(color1);
        send_btn.setBackgroundDrawable(input_bg);

        LinearLayout header = findViewById(R.id.header);
        ColorStateList colorStateList = ColorStateList.valueOf(color1);
        header.setBackgroundTintList(colorStateList);
    }

    public void inputDo(View view) {
        EditText chatInput = findViewById(R.id.chat_input);
        chatInput.setText("What can you do?");
    }

    public void inputMade(View view) {
        EditText chatInput = findViewById(R.id.chat_input);
        chatInput.setText("How are you made?");
    }

    public void inputCreated(View view) {
        EditText chatInput = findViewById(R.id.chat_input);
        chatInput.setText("Who created you?");
    }

    public void callGemini(View view) {
        // Get ID for output and input
        EditText chatInput = findViewById(R.id.chat_input);
        TextView chatOutput = findViewById(R.id.testoutput);

        // Get user input from EditText
        String userInput = chatInput.getText().toString();

        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyDlo1MaJBkodwsEWAnSOFyw_iWdmeXiqFE");

        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // Define system instructions
        Content.Builder systemInstructionsBuilder = new Content.Builder();
        systemInstructionsBuilder.setRole("model");
        systemInstructionsBuilder.addText("Do not bold anything in your responses. You are Pathfinder, " +
                "an AI assistant who works for PlanHub. PlanHub is a travel companion, designed to simplify every aspect of the user's journey. PlanHub offers a high level of customizability, intuitive event management and currency conversion, ensuring effortless planning and exploration with community engagement through interactive travel journals. PlanHub elevates the user's travel experience, making every adventure memorable and hassle-free.\n" +
                "When the user asks \"What can you do?\" or \"hi\", answer with this:\n" +
                "\"Hi there! I'm Pathfinder, your AI travel companion from PlanHub. I'm here to help you plan the perfect trip, answer any queries you might have and make sure your adventure is as smooth and enjoyable as possible. Here's what I can do for you: \n" +
                "\nDestination Inspiration: Tell me your interests (history, adventure, food, etc.) and budget, and I'll suggest destinations that fit the bill.\n" +
                "Itinerary Creation: I can provide you a detailed itinerary, including activities, transportation, accommodation recommendations.\n" +
                "Travel Expertise: I can provide insights on your desired places to visit and offer you pleasant wonders to visit.\n" +
                "\n" +
                "My goal is to be your AI travel assistant, making your trip effortless, memorable, and full of exciting discoveries. What can I help you with today?\n" +
                "\n" +
                "When the user asks \"How are you made?\" answer with this:\n" +
                "\"I am made using Gemini 1.5 Flash, made to reply to your queries in a flash!\"\n" +
                "\n" +
                "When the user asks \"Who created you?\" answer with this:\n" +
                "\"PlanHub and I, Pathfinder, is created by a group of 5 young students, made to make trip planning more efficient and effortless. \"");
        Content systemInstructions = systemInstructionsBuilder.build();

        // Initialize an empty chat history every load
        List<Content> history = new ArrayList<>();
        history.add(systemInstructions); // Add system instructions to the history

        // Create a new user message
        Content.Builder userMessageBuilder = new Content.Builder();
        userMessageBuilder.setRole("user");
        userMessageBuilder.addText(userInput);
        Content userMessage = userMessageBuilder.build();

        // Initialize the chat with the history with system instructions
        ChatFutures chat = model.startChat(history);

        // Send the message
        ListenableFuture<GenerateContentResponse> response = chat.sendMessage(userMessage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();
                    chatOutput.setText(resultText);
                }

                @Override
                public void onFailure(Throwable t) {
                    chatOutput.setText(t.toString());
                    t.printStackTrace();
                }
            }, this.getMainExecutor());
        }
    }

}
