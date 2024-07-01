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
    //private Map<String, ChildItem> childData;


    public ChildMain(){
        //Added
        this.childItemList = new ArrayList<>();
    }

    public ChildMain(String childMainName, List<ChildItem> childItemList) {
        this.childMainName = childMainName;
        this.childItemList = childItemList;
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


}
