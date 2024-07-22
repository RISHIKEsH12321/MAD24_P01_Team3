package sg.edu.np.mad.travelhub;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableBoolean implements Parcelable {
    private boolean value;

    public ParcelableBoolean(boolean value) {
        this.value = value;
    }

    protected ParcelableBoolean(Parcel in) {
        value = in.readByte() != 0;
    }

    public static final Creator<ParcelableBoolean> CREATOR = new Creator<ParcelableBoolean>() {
        @Override
        public ParcelableBoolean createFromParcel(Parcel in) {
            return new ParcelableBoolean(in);
        }

        @Override
        public ParcelableBoolean[] newArray(int size) {
            return new ParcelableBoolean[size];
        }
    };

    public boolean getValue() {
        return value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (value ? 1 : 0));
    }
}
