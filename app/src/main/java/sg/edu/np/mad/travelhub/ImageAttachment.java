package sg.edu.np.mad.travelhub;

import android.net.Uri;

import java.io.Serializable;

public class ImageAttachment implements Serializable {
    private String EventId;
    private String ImageId;
    private String URI;
    private String exampleDrawable;

    public ImageAttachment(String s, Uri u, String exampleDrawable) {
        this.EventId = s;
        this.URI = String.valueOf(u);
        this.exampleDrawable = exampleDrawable;
    }

    public ImageAttachment() {}

    public String getEventId() {
        return EventId;
    }

    public void setEventId(String eventId) {
        EventId = eventId;
    }

    public String getImageId() {
        return ImageId;
    }

    public void setImageId(String imageId) {
        ImageId = imageId;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getExampleDrawable() {
        return exampleDrawable;
    }

    public void setExampleDrawable(String exampleDrawable) {
        this.exampleDrawable = exampleDrawable;
    }

    @Override
    public String toString() {
        return "ImageAttachment{" +
                "EventId='" + EventId + '\'' +
                ", URI=" + URI +
                ", ImageId=" + ImageId +
                ", Drawable=" + exampleDrawable +
                '}';
    }
}
