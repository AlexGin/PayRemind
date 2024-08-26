package android.alexgin.payremind;
/**
 * DialogFragment with a simple cancel/confirm dialog and message.
 *
 * Activities using this dialog must implement OnDialogChoiceListener.
 */
import java.lang.String;

import android.util.Log;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ClearDialogFragment extends DialogFragment {
    private static final String TAG = "ClearDialogFragment";
    private static final String ARG_MODE_OF_CLEAR = "ModeOfClear";
    /**
     * Interface for receiving dialog events
     */
    public interface OnDialogChoiceListener {
        /**
         * Triggered when the user presses the cancel button
         */
        public void onDialogCanceled();

        /**
         * Triggered when the user presses the confirm button
         */
        public void onDialogConfirmed(int n_mode);
    }

    private int mModeOfErasing; // 0-nothing; 1-only flags; 2-all erase

    private OnDialogChoiceListener mListener;

    /**
     * Creates a new instance of the fragment and sets the arguments
     *
     * @param contentResourceId int to use for the content such as R.string.dialog_text
     * @param confirmResourceId int to use for the confirm button such as R.string.confirm
     * @return new ClearDialogFragment instance
     */
    public static ClearDialogFragment newInstance(int contentResourceId, int confirmResourceId) {
        ClearDialogFragment fragment = new ClearDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MODE_OF_CLEAR, contentResourceId);

        fragment.setArguments(args);
        return fragment;
    }

    public ClearDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args == null) {
            throw new IllegalStateException("No arguments set, use the"
                    + " newInstance method to construct this fragment");
        }

        mModeOfErasing = args.getInt(ARG_MODE_OF_CLEAR);
        Log.d(TAG, "ClearDialogFragment: onCreate");
    }

    public static ClearDialogFragment newInstance(int n_mode) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MODE_OF_CLEAR, n_mode);

        Log.d(TAG, "ClearDialogFragment: newInstance");
        ClearDialogFragment fragment = new ClearDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    ///@NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "ClearDialogFragment: onCreateDialog-1");
        boolean b_mode_all = (mModeOfErasing == 2);
        String sMessage = b_mode_all ? getString(R.string.erase_all) :
                getString(R.string.erase_only_flags);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(sMessage)
            .setTitle(R.string.erase)
            .setIcon(R.drawable.my_dog)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // Send the positive button event back to the host activity
                    mListener.onDialogConfirmed(mModeOfErasing);
                }
            })
            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // Send the negative button event back to the host activity
                    mListener.onDialogCanceled();
                }
            }
        );
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDialogChoiceListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}