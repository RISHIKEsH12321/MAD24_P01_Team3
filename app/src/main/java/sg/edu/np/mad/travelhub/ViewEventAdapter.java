package sg.edu.np.mad.travelhub;

import static java.lang.Integer.parseInt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewEventAdapter extends RecyclerView.Adapter<ViewEventAdapter.ViewEventHolder> {
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String REQUEST_CODE = "123";
    private List<CompleteEvent> data;
    private OnItemClickListener listener;
    private Context context;
    FirebaseAuth mAuth;

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
                //Populate Itinerary Event
                LinearLayout container = new LinearLayout(holder.itineaary.getContext());
                container.setOrientation(LinearLayout.HORIZONTAL);
                TextView itTime = new TextView(holder.itineaary.getContext());
                TextView itName = new TextView(holder.itineaary.getContext());

                itTime.setBackgroundResource(R.drawable.rounded_border);
                itTime.setPadding(12, 8, 12, 8);
                itTime.setTextColor(ContextCompat.getColor(holder.itineaary.getContext(), R.color.white));
                itTime.setTextSize(16);
                itName.setTextSize(20);
                LinearLayout.LayoutParams itTimeParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                itTimeParams.setMargins(16, 8, 16, 8);
                itTime.setLayoutParams(itTimeParams);


                String timeText = String.format("%s:%s - %s:%s",
                        itineraryEvent.startHour,
                        itineraryEvent.startMin,
                        itineraryEvent.endHour,
                        itineraryEvent.endMin);
                itTime.setText(timeText);

                itName.setText(itineraryEvent.eventName);
                itName.setPadding(20,0,0,0);
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

                container.addView(itTime);
                container.addView(itName);
                //Add itinerary event to display
                holder.itineaary.addView(container);
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
                TextView it = new TextView(holder.notes.getContext());
                it.setText(notes);
                it.setTextAppearance(context, R.style.BulletPoints);
                it.setTextSize(25);
                // Set the drawable (Arrow Sign)
                Drawable bulletDrawable = ContextCompat.getDrawable(context, R.drawable.round_arrow_right);
                it.setCompoundDrawablesWithIntrinsicBounds(bulletDrawable, null, null, null);
                it.setGravity(Gravity.CENTER_VERTICAL);
                holder.notes.addView(it);
            }
        }

        // Add reminders
        if (event.reminderList != null){
            for (Reminder reminder: event.reminderList){
                TextView it = new TextView(holder.reminder.getContext());
                it.setText(reminder.reminderTitle + " : " +  reminder.reminderTime);
                it.setTextAppearance(context, R.style.BulletPoints);
                it.setTextSize(25);
                // Set the drawable programmatically (Arrow Sign)
                Drawable bulletDrawable = ContextCompat.getDrawable(context, R.drawable.round_arrow_right);
                it.setCompoundDrawablesWithIntrinsicBounds(bulletDrawable, null, null, null);
                it.setGravity(Gravity.CENTER_VERTICAL);

                holder.reminder.addView(it);
            }
        }

        //Expand & Contract Content
        holder.expandArrow.setOnClickListener(view ->{
            // If the CardView is already expanded, set its visibility

            // to gone and change the expand less icon to expand more.

            if (holder.hiddenView.getVisibility() == View.VISIBLE) {

                // The transition of the hiddenView is carried out by the TransitionManager class.

                // Here we use an object of the AutoTransition Class to create a default transition

                TransitionManager.beginDelayedTransition(holder.cardview, new AutoTransition());

                holder.hiddenView.setVisibility(View.GONE);

                holder.expandArrow.setImageResource(R.drawable.baseline_expand_more_24);

            }



            // If the CardView is not expanded, set its visibility to

            // visible and change the expand more icon to expand less.

            else {

                TransitionManager.beginDelayedTransition(holder.cardview, new AutoTransition());

                holder.hiddenView.setVisibility(View.VISIBLE);

                holder.expandArrow.setImageResource(R.drawable.baseline_expand_less_24);

            }


        });

        //Delete Event Clicker
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.getLayoutPosition());
            }
        });

        //Edit Event Clicker
        holder.editImg.setOnClickListener(v -> {
            Intent editIntent = new Intent(context, EventManagement.class);
            editIntent.putExtra("CompleteEvent", event);
            editIntent.putExtra("purpose", "Edit");
            context.startActivity(editIntent);
        });

        holder.generateQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notify activity to display QrCodeFragment
                if (context instanceof ViewEvents) {
                    String jsonData = event.CompleteEventToJsonConverter(event); // Assuming this method converts event to JSON string
                    ((ViewEvents) context).showQrCodeFragment(jsonData);
                    Log.d("QR CODE JSON", jsonData);
                }
            }
        });

        // Popup Menu for deleteImg, editImg, and generateQrCode
        // Long click listener for showing popup menu
        holder.veShowPM.setOnLongClickListener(v -> {
            popUpMenu(v, event, position);
            return true;
        });
//        holder.deleteImg.setOnClickListener(showPopupMenuListener);
//        holder.editImg.setOnClickListener(showPopupMenuListener);
//        holder.generateQrCode.setOnClickListener(showPopupMenuListener);


    }

    private void popUpMenu(View view, CompleteEvent event, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.ve_popupmenu);

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
            switch (item.getItemId()) {
                // Case statements are gotten from logged ids due to unknown error.
                case 2131362611: // Edit Event
                    Intent editIntent = new Intent(context, EventManagement.class);
                    editIntent.putExtra("CompleteEvent", event);
                    editIntent.putExtra("purpose", "Edit");
                    context.startActivity(editIntent);
                    Log.d("popUpMenu", "EDIT EVENT IS CALLED IN POPUPMENU");
                    return true;

                case 2131362612:
                    showQrCode(event); //Create and Display QR Code
                    Log.d("popUpMenu", "SHARE EVENT IS CALLED IN POPUPMENU");
                    return true;

                case 2131362610: //Delete Event
                    listener.onItemClick(position); // Assuming listener handles delete
                    Log.d("popUpMenu", "DELETE EVENT IS CALLED IN POPUPMENU");
                    return true;

                case 2131362649: //Store Event in Database
//                    pushEventToFirebase(event);
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String userId = currentUser.getUid();
                    pushEventToFirebase(event,userId);
                    Log.d("popUpMenu", String.valueOf(event.notesList));
                    return true;

                default:
                    return false;
            }
//            return false;
        });
        popupMenu.show();
    }



    private ImageView populateImages(ImageAttachment image) {

//        ImageAttachment imageAttachment = new ImageAttachment();
        String fileUri = image.URI;

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
            // Inflate the custom layout for the alert dialog
            View dialogView = LayoutInflater.from(context).inflate(R.layout.em_image_dialog, null);

            // Get the ImageView from the custom layout
            ImageView fullSizeImageView = dialogView.findViewById(R.id.EMfullSizeImageView);

            // Load full-size image into the ImageView using Glide
            Glide.with(context)
                    .load(fileUri)
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
        for (String note : event.notesList) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("notes", note);
            notesList.add(itemMap);
        }
        eventMap.put("Notes", notesList);

        List<Map<String, Object>> reminderList = new ArrayList<>();
        for (Reminder reminder : event.reminderList) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("reminderTitle", reminder.reminderTitle);
            itemMap.put("reminderTime", reminder.reminderTime);
            reminderList.add(itemMap);
        }
        eventMap.put("reminders", reminderList);

        List<Map<String, Object>> toBringItemsList = new ArrayList<>();
        for (ToBringItem item : event.toBringItems) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("itemName", item.itemName);
            toBringItemsList.add(itemMap);
        }
        eventMap.put("toBringItems", toBringItemsList);

        List<Map<String, Object>> itineraryEventList = new ArrayList<>();
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
        eventMap.put("itineraryEventList", itineraryEventList);

        List<Map<String, Object>> attachmentImageList = new ArrayList<>();
        for (ImageAttachment attachment : event.attachmentImageList) {
            Map<String, Object> attachmentMap = new HashMap<>();
            attachmentMap.put("uri", attachment.URI);
            attachmentImageList.add(attachmentMap);
        }
        eventMap.put("attachmentImageList", attachmentImageList);

        // Create a unique key for the new event and set the value
        String key = databaseReference.child("Event").push().getKey();
        Log.d("FirebaseAuth", "pushEventToFirebase: " + key);
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
            String jsonData = event.CompleteEventToJsonConverter(event); // Assuming this method converts event to JSON string
            ((ViewEvents) context).showQrCodeFragment(jsonData);
            Log.d("QR CODE JSON", jsonData);
        }

    }

    public static class ViewEventHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView id;
        TextView category;
        ImageView deleteImg;
        ImageView editImg;
        ImageView generateQrCode;
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
            deleteImg = itemView.findViewById(R.id.VEDelteEvent);
            editImg = itemView.findViewById(R.id.VEEditEvent);
            generateQrCode = itemView.findViewById(R.id.VECreateQRCode);
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
