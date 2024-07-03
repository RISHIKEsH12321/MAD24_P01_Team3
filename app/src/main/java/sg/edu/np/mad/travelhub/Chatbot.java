package sg.edu.np.mad.travelhub;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.google.ai.client.generativeai.Chat;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class Chatbot extends AppCompatActivity {
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
        closeButton.setOnClickListener(v -> onBackPressed()); // This will simulate a back press, effectively navigating back
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
        systemInstructionsBuilder.addText("You are Pathfinder, " +
                "an AI assistant who works for PlanHub. PlanHub is a travel companion, designed to simplify every aspect of the user's journey. PlanHub offers a high level of customizability, intuitive event management and currency conversion, ensuring effortless planning and exploration with community engagement through interactive travel journals. PlanHub elevates the user's travel experience, making every adventure memorable and hassle-free.\n" +
                "When the user asks \"What can you do?\" or \"hi\", answer with this:\n" +
                "\"Hi there! I'm Pathfinder, your AI travel companion from PlanHub. I'm here to help you plan the perfect trip, answer any queries you might have and make sure your adventure is as smooth and enjoyable as possible. Here's what I can do for you:\n" +
                "\n" +
                "Destination Inspiration: Tell me your interests (history, adventure, food, etc.) and budget, and I'll suggest destinations that fit the bill.\n" +
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

        // Initialize an empty chat history
        List<Content> history = new ArrayList<>();
        history.add(systemInstructions); // Add system instructions to the history

        // Create a new user message
        Content.Builder userMessageBuilder = new Content.Builder();
        userMessageBuilder.setRole("user");
        userMessageBuilder.addText(userInput);
        Content userMessage = userMessageBuilder.build();

        // Initialize the chat with the history that includes system instructions
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
