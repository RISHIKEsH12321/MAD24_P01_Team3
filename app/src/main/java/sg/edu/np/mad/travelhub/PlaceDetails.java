package sg.edu.np.mad.travelhub;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.Serializable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetails implements Serializable{
    private String name;
    private String editorialSummary;
    private double rating;
    private String address;
    private List<String> photos;
    private List<PlaceReview> reviews;

    // Getters and Setters
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
}
