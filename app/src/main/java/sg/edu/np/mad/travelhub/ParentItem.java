package sg.edu.np.mad.travelhub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParentItem {
    //private String postId;
    private String parentName, parentDescription, parentImage, parentUser;
    //private List<ChildItem> childItemList;
    private Map<String, ChildMain> childData;

    //prob init parentuser here
    public ParentItem(){this.childData = new HashMap<>();}

    public ParentItem(String parentName, String parentDescription, String parentImage, String parentUser, Map<String, ChildMain> childData) {
        this.parentName = parentName;
        this.parentDescription = parentDescription;
        this.parentImage = parentImage;
        this.parentUser = parentUser;
        this.childData = childData;
    }

    public Map<String, ChildMain> getChildData() {
        return childData;
    }

    public void setChildData(Map<String, ChildMain> childData) {

        this.childData = childData;
    }

    public String getParentName() {
        return parentName;
    }

    public String getParentDescription() {
        return parentDescription;
    }

    public void setParentDescription(String parentDescription) {
        this.parentDescription = parentDescription;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentImage() {
        return parentImage;
    }

    public void setParentImage(String parentImage) {
        this.parentImage = parentImage;
    }

    public String getParentUser() {
        return parentUser;
    }

    public void setParentUser(String parentUser) {
        this.parentUser = parentUser;
    }

}