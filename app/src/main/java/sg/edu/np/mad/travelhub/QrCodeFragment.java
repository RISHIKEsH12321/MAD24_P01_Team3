package sg.edu.np.mad.travelhub;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.load.ImageHeaderParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.HashMap;
import java.util.Map;

public class QrCodeFragment extends Fragment {

    private static final String ARG_JSON_DATA = "json_data";
    private String jsonData;


    public QrCodeFragment() {
        // Required empty public constructor
    }

    public static QrCodeFragment newInstance(String jsonData) {
        QrCodeFragment fragment = new QrCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_JSON_DATA, jsonData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jsonData = getArguments().getString(ARG_JSON_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qr_code, container, false);
        ImageButton backButton = view.findViewById(R.id.QRbackButton);
        ImageView imageView = view.findViewById(R.id.QrCodeDisplay);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close or pop the fragment here
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Generate QR Code based on jsonData
        if (jsonData != null) {
            Bitmap bitmap = generateQRCode(jsonData);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            Log.d("QR CODE JSON", String.valueOf("onCreateView: " + bitmap != null));
        }


        return view;
    }

    private Bitmap generateQRCode(String data) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 500, 500, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

}