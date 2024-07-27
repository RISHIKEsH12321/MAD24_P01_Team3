package sg.edu.np.mad.travelhub;

import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.startActivity;
import static java.lang.Integer.parseInt;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ViewEventAdapter extends RecyclerView.Adapter<ViewEventAdapter.ViewEventHolder> {
    public static final String REQUEST_CODE = "123";
    private List<CompleteEvent> data;
    private OnItemClickListener listener;
    private Context context;
    FirebaseAuth mAuth;

    static final int EditBtn = R.id.ve_pm_edit;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        listener = clickListener;
    }

    public ViewEventAdapter(Context context, List<CompleteEvent> data) {
        this.context = context;
        this.data = data;
    }


    @NonNull
    @Override
    public ViewEventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ve_complete_event_layout, parent, false);
        return new ViewEventHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewEventHolder holder, int position) {
        CompleteEvent event = data.get(position);
        holder.id.setText(event.eventID);
        holder.name.setText(event.eventName);
        holder.category.setText(event.category);

        // Clear previous dynamic views before adding new ones
        holder.itineaary.removeAllViews();
        holder.items.removeAllViews();
        holder.notes.removeAllViews();
        holder.reminder.removeAllViews();

        // Add itinerary events
        if (event.itineraryEventList != null){ // Throws error if no events
            for (ItineraryEvent itineraryEvent: event.itineraryEventList){
                // Inflate the custom layout
                View itineraryView = LayoutInflater.from(holder.itineaary.getContext())
                        .inflate(R.layout.ve_itinerary_layout, holder.itineaary, false);

                // Get references to the views in the custom layout
                TextView itTime = itineraryView.findViewById(R.id.itTime);
                TextView itName = itineraryView.findViewById(R.id.itName);

                // Set the data for the views
                String startTime = convertTo12HourFormat(itineraryEvent.startHour + ":" + itineraryEvent.startMin);
                String endTime = convertTo12HourFormat(itineraryEvent.endHour + ":" + itineraryEvent.endMin);
//                String timeText = String.format("%s - %s", startTime, endTime);
                String timeText = startTime + " - " + endTime;
                Log.d("TIMESETTING", "startTime: " + startTime + "\n" + "endTime: " + endTime);
                Log.d("TIMESETTING", "timeText: " + timeText);
                itTime.setText(timeText);

                itName.setText(itineraryEvent.eventName);
                itName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(holder.itineaary.getContext())
                                .setTitle("Notes")
                                .setMessage(itineraryEvent.eventNotes)
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    }
                });

                // Add the custom layout to the parent layout
                holder.itineaary.addView(itineraryView);
            }
        }

        //Add Images
        if (event.attachmentImageList != null){
            Log.d("IMAGEATTACHMENTINIMAGES", String.valueOf(event.attachmentImageList.size()));
            for (ImageAttachment imageAttachment: event.attachmentImageList){
                Log.d("IMAGEATTACHMENTINIMAGES", "GOes in loop");
                ImageView imageView = populateImages(imageAttachment);
                Log.d("IMAGEATTACHMENTINIMAGES", "CREATED AND ADDED A IMAGE. ID: " + imageAttachment.ImageId);
                holder.images.addView(imageView);
            }
        }

        //Add Items
        if (event.toBringItems!=null) {
            //Initialize adapter for item
            VEItemAdapter itemAdapter = new VEItemAdapter(context, event.toBringItems);

            LinearLayoutManager itemsLayoutManager = new LinearLayoutManager(context);

            holder.items.setLayoutManager(itemsLayoutManager);
            holder.items.setItemAnimator(new DefaultItemAnimator());
            holder.items.setAdapter(itemAdapter);
        }

        // Add notes
        if (event.notesList != null){
            for (String notes: event.notesList){
                // Inflate the custom layout
                View notesView = LayoutInflater.from(holder.notes.getContext())
                        .inflate(R.layout.ve_notes_layout, holder.notes, false);

                // Get references to the views in the custom layout
                TextView note = notesView.findViewById(R.id.ve_notes);
                note.setText(notes);

                // Add the custom layout to the parent layout
                holder.notes.addView(notesView);
            }
        }

        // Add reminders
        if (event.reminderList != null){
            for (Reminder reminder: event.reminderList){
                // Inflate the custom layout
                View reminderView = LayoutInflater.from(holder.reminder.getContext())
                        .inflate(R.layout.ve_reminder_layout, holder.reminder, false);

                // Get references to the views in the custom layout
                TextView reminderTime = reminderView.findViewById(R.id.ve_reminderTime);
                TextView reminderText = reminderView.findViewById(R.id.ve_reminderText);
                reminderText.setText(reminder.reminderTitle);
                reminderTime.setText(convertTo12HourFormat(reminder.reminderTime));

                // Add the custom layout to the parent layout
                holder.reminder.addView(reminderView);

            }
        }

        //Expand & Contract Content
        holder.expandArrow.setOnClickListener(view ->{
            // If the CardView is already expanded, set its visibility
            // to gone and change the expand less icon to expand more.
            if (holder.hiddenView.getVisibility() == View.VISIBLE) {
                // The transition of the hiddenView is carried out by the TransitionManager class.
                // Here we use an object of the AutoTransition Class to create a default transition
                AutoTransition transition = new AutoTransition();
                // Set the duration of the transition
                transition.setDuration(50); // Duration in milliseconds (e.g., 300ms)

                TransitionManager.beginDelayedTransition(holder.cardview, transition);
                holder.hiddenView.setVisibility(View.GONE);
                holder.expandArrow.setImageResource(R.drawable.baseline_expand_more_24);
            }

            // If the CardView is not expanded, set its visibility to
            // visible and change the expand more icon to expand less.
            else {
                AutoTransition transition = new AutoTransition();
                transition.setDuration(300); // Duration in milliseconds (e.g., 300ms)

                TransitionManager.beginDelayedTransition(holder.cardview, transition);
                holder.hiddenView.setVisibility(View.VISIBLE);
                holder.expandArrow.setImageResource(R.drawable.baseline_expand_less_24);
            }
        });


        // Popup Menu for deleteImg, editImg, and generateQrCode
        // Long click listener for showing popup menu
        //Display PopupMenu
        holder.veShowPM.setOnLongClickListener(v -> {
            popUpMenu(v, event, position);
            return true;
        });
    }

    private void popUpMenu(View view, CompleteEvent event, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.ve_popupmenu);

        Log.d("popUpMenu", "popUpMenu: " + popupMenu.getMenu());
        // Use reflection to force icons to show
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            Log.d("popUpMenu", "popUpMenu: ID OF CLICKED ITEM :" + item.getItemId());
            if (item.getItemId() == (R.id.ve_pm_edit)){
                Intent editIntent = new Intent(context, EventManagement.class);
                editIntent.putExtra("CompleteEvent", event);
                editIntent.putExtra("purpose", "Edit");
                context.startActivity(editIntent);
                Log.d("popUpMenu", "EDIT EVENT IS CALLED IN POPUPMENU");
                return true;
            }
            else if (item.getItemId() == (R.id.ve_pm_share)){
                showQrCode(event); //Create and Display QR Code
                Log.d("popUpMenu", "SHARE EVENT IS CALLED IN POPUPMENU");
                return true;
            }
            else if (item.getItemId() == (R.id.ve_pm_delete)){
                    listener.onItemClick(position); // Assuming listener handles delete
                    Log.d("popUpMenu", "DELETE EVENT IS CALLED IN POPUPMENU");
                    return true;
            }
            else if (item.getItemId() == (R.id.ve_pm_firebase)){
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    Log.d("popUpMenu", "currentUser: " + currentUser);
                    if (currentUser!= null){
                        String userId = currentUser.getUid();
                        pushEventToFirebase(event,userId);
                        Log.d("popUpMenu", "currentUser: Not NULL");
                    }else{
                        pushEventToFirebase(event,null);
                        Log.d("popUpMenu", "currentUser: NULL");
                    }
                    Log.d("popUpMenu", "Store Event in Database");
                    return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private ImageView populateImages(ImageAttachment image) {
//       ImageAttachment imageAttachment = new ImageAttachment();
        boolean x = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;


        String fileUri = (image.URI);
        ImageView imageView = new ImageView(context);

        //Displaying the drawble not the image URI
        // Use Glide to load the image into the ImageView
        Glide.with(context)
                .load(fileUri)
                .apply(new RequestOptions().override(LinearLayout.LayoutParams.WRAP_CONTENT, 100)) // Set height to 100dp
                .into(imageView);

        // Set layout parameters for ImageView
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                200 // Set height to 100dp
        );
        imageView.setLayoutParams(layoutParams);

        imageView.setOnClickListener(v -> {
            // Ensure that context is valid and accessible
            if (context == null) {
                Log.e("AlertDialog", "Context is null, cannot inflate dialog.");
                return;
            }

            // Inflate the custom layout for the alert dialog
            View dialogView = LayoutInflater.from(context).inflate(R.layout.em_image_dialog, null);

            // Get the ImageView from the custom layout
            ImageView fullSizeImageView = dialogView.findViewById(R.id.EMfullSizeImageView);

            // Load full-size image into the ImageView using Glide
            Glide.with(context)
                    .load(fileUri)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("AlertDialog", "Failed to load image into ImageView: " + e);
                            return false; // Return false to allow Glide to handle any further requests.
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("AlertDialog", "Image loaded successfully into ImageView.");
                            return false; // Return false to allow Glide to handle any further requests.
                        }
                    })
                    .into(fullSizeImageView);

            // Create and configure the AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(dialogView);
            builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

            // Show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
        return imageView;
    }


    public static String convertTo12HourFormat(String time24) {
        try {
            // Create a SimpleDateFormat object for the 24-hour format
            SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm", Locale.getDefault());

            // Parse the 24-hour time string to a Date object
            Date date = sdf24.parse(time24);

            // Create a SimpleDateFormat object for the 12-hour format with AM/PM
            SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a", Locale.getDefault());

            // Format the Date object to a 12-hour time string
            return sdf12.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
    }

    //Method to call database to delete event based on ID
    public void deleteEvent(int eventId, int position) {
        DatabaseHandler dbHandler = new DatabaseHandler(context, null, null, 1);
        dbHandler.deleteEventById(eventId);

        // Remove item from data list and notify adapter
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
    }

    private void pushEventToFirebase(CompleteEvent event, String userID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        // Create a map to hold the fields you want to include in the database
        Map<String, Object> eventMap = new HashMap<>();
//        eventMap.put("eventID", event.eventID);
        eventMap.put("EventName", event.eventName);
        eventMap.put("Date", event.date);
        eventMap.put("Category", event.category);
//        eventMap.put("notesList", event.notesList);
//        eventMap.put("reminderList", event.reminderList);

        // Convert nested objects to maps
        List<Map<String, Object>> notesList = new ArrayList<>();
        if (event.notesList!= null){
            for (String note : event.notesList) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("notes", note);
                notesList.add(itemMap);
            }
        }

        eventMap.put("Notes", notesList);

        List<Map<String, Object>> reminderList = new ArrayList<>();
        if(event.reminderList!=null){
            for (Reminder reminder : event.reminderList) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("reminderTitle", reminder.reminderTitle);
                itemMap.put("reminderTime", reminder.reminderTime);
                reminderList.add(itemMap);
            }
        }

        eventMap.put("reminders", reminderList);

        List<Map<String, Object>> toBringItemsList = new ArrayList<>();
        if(event.toBringItems!=null){
            for (ToBringItem item : event.toBringItems) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("itemName", item.itemName);
                toBringItemsList.add(itemMap);
            }
        }

        eventMap.put("toBringItems", toBringItemsList);

        List<Map<String, Object>> itineraryEventList = new ArrayList<>();
        if(event.itineraryEventList!= null){
            for (ItineraryEvent itineraryEvent : event.itineraryEventList) {
                Map<String, Object> itineraryEventMap = new HashMap<>();
                itineraryEventMap.put("eventName", itineraryEvent.eventName);
                itineraryEventMap.put("eventNotes", itineraryEvent.eventNotes);
                itineraryEventMap.put("startHour", itineraryEvent.startHour);
                itineraryEventMap.put("startMin", itineraryEvent.startMin);
                itineraryEventMap.put("endHour", itineraryEvent.endHour);
                itineraryEventMap.put("endMin", itineraryEvent.endMin);
                itineraryEventList.add(itineraryEventMap);
            }
        }

        eventMap.put("itineraryEventList", itineraryEventList);

        List<Map<String, Object>> attachmentImageList = new ArrayList<>();
        if (event.attachmentImageList != null) {
            for (ImageAttachment attachment : event.attachmentImageList) {
                Map<String, Object> attachmentMap = new HashMap<>();
                attachmentMap.put("uri", attachment.URI);
                attachmentImageList.add(attachmentMap);
            }
        }
        eventMap.put("attachmentImageList", attachmentImageList);

        // Create a unique key for the new event and set the value
        String key = databaseReference.child("Event").push().getKey();
        Log.d("TOFIREBASE", "Add event to firebase.");
        Log.d("FirebaseAuth", "pushEventToFirebase: " + key);
        if (userID == null){
            Log.d("TOFIREBASE", "No User ID");
            return;
        }
        if (key != null) {
            databaseReference.child("Event").child(key).child("users").setValue(userID).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("TOFIREBASE", "User ID set successfully.");
                } else {
                    Log.e("TOFIREBASE", "Failed to set user ID.", task.getException());
                }
            });

            databaseReference.child("Event").child(key).child("eventDetails").setValue(eventMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("TOFIREBASE", "Event details set successfully.");
                } else {
                    Log.e("TOFIREBASE", "Failed to set event details.", task.getException());
                }
            });
        } else {
            Log.e("TOFIREBASE", "Failed to create a unique key for the event.");
        }
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    private void showQrCode(CompleteEvent event){
        if (context instanceof ViewEvents) {
            String jsonData = event.CompleteEventToJsonConverter(event); // This method converts event to JSON string
            ((ViewEvents) context).showQrCodeFragment(jsonData);
            Log.d("QR CODE JSON", jsonData);
        }
    }

    public static class ViewEventHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView id;
        TextView category;
        LinearLayout veShowPM;
        LinearLayout itineaary;
        RecyclerView items;
        LinearLayout reminder;
        LinearLayout notes;
        LinearLayout images;
        ImageView expandArrow;
        LinearLayout hiddenView;
        CardView cardview;

        public ViewEventHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            id = itemView.findViewById(R.id.VEEventID);
//            deleteImg = itemView.findViewById(R.id.VEDelteEvent);
//            editImg = itemView.findViewById(R.id.VEEditEvent);
//            generateQrCode = itemView.findViewById(R.id.VECreateQRCode);
            name = itemView.findViewById(R.id.VEEventName);
            category = itemView.findViewById(R.id.VEEventCategory);
            itineaary = itemView.findViewById(R.id.VEEventItinerary);
            items = itemView.findViewById(R.id.VEEventItems);
            notes = itemView.findViewById(R.id.VEEventNote);
            reminder = itemView.findViewById(R.id.VEEventRemidners);
            images = itemView.findViewById(R.id.VEEventImages);
            expandArrow = itemView.findViewById(R.id.VEarrow_button);
            hiddenView = itemView.findViewById(R.id.VEhidden_view);
            cardview = itemView.findViewById(R.id.VEbase_cardview);
            veShowPM = itemView.findViewById(R.id.ve_showPM);
        }
    }
}
