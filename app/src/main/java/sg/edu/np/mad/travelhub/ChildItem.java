package sg.edu.np.mad.travelhub;

public class ChildItem {

    private String childName, childDescription, childImage;

    public ChildItem(){}

    public ChildItem(String childName, String childDescription, String childImage) {
        this.childName = childName;
        this.childDescription = childDescription;
        this.childImage = childImage;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChildDescription() {
        return childDescription;
    }

    public void setChildDescription(String childDescription) {
        this.childDescription = childDescription;
    }

    public String getChildImage() {
        return childImage;
    }

    public void setChildImage(String childImage) {
        this.childImage = childImage;
    }
}