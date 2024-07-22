package sg.edu.np.mad.travelhub;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetails implements Parcelable {
    private @Nullable String placeXid;
    private String placeId; // New field
    private String name;
    private String editorialSummary;
    private double rating;
    private String address;
    private List<String> photos;
    private List<PlaceReview> reviews;
    private @Nullable String description;
    private boolean history;

    // Constructors
    public PlaceDetails() {
        // Default constructor required for Parcelable
    }

    protected PlaceDetails(Parcel in) {
        placeXid = in.readString();
        placeId = in.readString(); // Read placeId
        name = in.readString();
        editorialSummary = in.readString();
        rating = in.readDouble();
        address = in.readString();
        photos = in.createStringArrayList();
        reviews = in.createTypedArrayList(PlaceReview.CREATOR);
        description = in.readString();
        history = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeXid);
        dest.writeString(placeId); // Write placeId
        dest.writeString(name);
        dest.writeString(editorialSummary);
        dest.writeDouble(rating);
        dest.writeString(address);
        dest.writeStringList(photos);
        dest.writeTypedList(reviews);
        dest.writeString(description);
        dest.writeByte((byte) (history ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlaceDetails> CREATOR = new Creator<PlaceDetails>() {
        @Override
        public PlaceDetails createFromParcel(Parcel in) {
            return new PlaceDetails(in);
        }

        @Override
        public PlaceDetails[] newArray(int size) {
            return new PlaceDetails[size];
        }
    };

    // Getters and setters
    public String getPlaceXid() {
        return placeXid;
    }

    public void setPlaceXid(@Nullable String placeXid) {
        this.placeXid = placeXid;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEditorialSummary() {
        return editorialSummary;
    }

    public void setEditorialSummary(String editorialSummary) {
        this.editorialSummary = editorialSummary;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public List<PlaceReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<PlaceReview> reviews) {
        this.reviews = reviews;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public boolean isHistory() {
        return history;
    }

    public void setHistory(boolean history) {
        this.history = history;
    }
}
