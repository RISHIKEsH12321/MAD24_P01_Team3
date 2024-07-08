package sg.edu.np.mad.travelhub;

import android.media.Image;
import android.net.Uri;

import java.io.Serializable;

public class ImageAttachment implements Serializable {
    String EventId;
    String ImageId;
    String URI;
    String exampleDrawable;

    public ImageAttachment(String s, Uri u,String exampleDrawable){
        this.EventId = s;
        this.URI = String.valueOf(u);
        this.exampleDrawable=exampleDrawable;
    }
    public ImageAttachment(){}

    @Override
    public String toString() {
        return "ImageAttachment{" +
                "EventId='" + EventId + '\'' +
                ", URI=" + URI +
                ", IMAGEId=" + ImageId +
                ", Drawable=" + exampleDrawable +
                '}';
    }
}
