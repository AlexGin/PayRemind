package android.alexgin.payremind;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.graphics.Bitmap;
import androidx.fragment.app.DialogFragment;
import android.util.Log;

public class PhotoViewFragment extends DialogFragment {

    private static final String ARG_PHOTO = "photo";
    private static final String TAG = "PhotoViewFragment";

    private String mStrPhotoFilePath;
    private ImageView mPhotoImageView;

    public static PhotoViewFragment newInstance(String strPhoteFileName) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO, strPhoteFileName);

        // Log.d(TAG, "PhotoViewFragment: newInstance fn=" + strPhoteFileName);
        PhotoViewFragment fragment = new PhotoViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_photo_view, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "PhotoViewFragment: onViewCreated");

        mStrPhotoFilePath = (String) getArguments().getSerializable(ARG_PHOTO);

        Log.d(TAG, "onViewCreated: file-path=" + mStrPhotoFilePath);

        mPhotoImageView = (ImageView) view.findViewById(R.id.image_view);
        updatePhotoView();
    }

    private void updatePhotoView() {
        if (mPhotoImageView == null) {
            mPhotoImageView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mStrPhotoFilePath, getActivity());
            mPhotoImageView.setImageBitmap(bitmap);
        }
    }
}
