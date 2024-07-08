package sg.edu.np.mad.travelhub;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseError;

import java.util.List;
import java.util.Map;

public class    FirebaseViewModel extends ViewModel implements FirebaseRepo.OnRealtimeDbTaskComplete {

    private MutableLiveData<Map<String, ParentItem>> parentItemMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ChildMain>> childMainMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<DatabaseError> databaseErrorMutableLiveData = new MutableLiveData<>();
    private FirebaseRepo firebaseRepo;

    public MutableLiveData<Map<String, ParentItem>> getParentItemMutableLiveData() {
        return parentItemMutableLiveData;
    }

    public MutableLiveData<List<ChildMain>> getChildMainMutableLiveData() {
        return childMainMutableLiveData;
    }

    public MutableLiveData<DatabaseError> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public FirebaseViewModel() {
        firebaseRepo = new FirebaseRepo(this);
    }

    public void getAllParentData() {
        firebaseRepo.getAllParentData();
    }
    public void getAllChildMainData(String postId) {
        firebaseRepo.getAllChildMainData(postId);
    }

    @Override
    public void onSuccess(Map<String, ParentItem> parentItemMap) {
        parentItemMutableLiveData.setValue(parentItemMap);
    }

    @Override
    public void onSuccessChildMain(List<ChildMain> childMainList) {
        childMainMutableLiveData.setValue(childMainList);
    }

    @Override
    public void onFailure(DatabaseError error) {
        databaseErrorMutableLiveData.setValue(error);
    }

}
