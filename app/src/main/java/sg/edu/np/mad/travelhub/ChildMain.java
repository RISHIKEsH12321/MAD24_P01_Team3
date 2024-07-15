package sg.edu.np.mad.travelhub;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildMain {

    private String childMainName;
    private List<ChildItem> childItemList;
    private String key;
    //private Map<String, ChildItem> childData;
    private boolean isExpandable;

    public ChildMain(){
        //Added
        this.childItemList = new ArrayList<>();
        isExpandable = false;
    }

    public ChildMain(String childMainName, List<ChildItem> childItemList, String key) {
        this.childMainName = childMainName;
        this.childItemList = childItemList;
        this.key = key;
    }

    public List<ChildItem> getChildItemList() {
        return childItemList;
    }
    public void setChildItemList(List<ChildItem> childItemList) {
        this.childItemList = childItemList;
    }

    public String getChildMainName() {
        return childMainName;
    }

    public void setChildMainName(String childMainName) {
        this.childMainName = childMainName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }
}
