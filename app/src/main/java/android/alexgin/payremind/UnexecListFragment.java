package android.alexgin.payremind;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.util.Log;
import android.widget.TextView;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class UnexecListFragment extends Fragment {

    private static final String DIALOG_ITEM_TODO = "DialogItemToDo";

    private static final String TAG = "UnexecListFragment";

    private ItemUnexecAdapter mItemsAdapter;
    private ListView mListView;

    public static UnexecListFragment getNewInstance(Object obj) {
        UnexecListFragment fragment = new UnexecListFragment();
        if (obj != null) {
            Bundle args = new Bundle();
            args.putParcelableArrayList(MainActivity.ARG_RESULT_CODE, (ArrayList<Pay>) obj);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        // For testing purposes:
        PaymentLab lab = PaymentLab.get(getActivity());
        // List<Pay> lstRaw = lab.getPayments();
        // ArrayList<Pay> list = new ArrayList<Pay>(lstRaw);

        List<UUID> idPayList = PRApplication.INSTANCE.PrefetchUnexecutedPays();
        ArrayList<Pay> list = new ArrayList<Pay>();
        for (UUID idRaw : idPayList)
        {
            Pay p = lab.getPay(idRaw);
            list.add(p);
        }
        // Create the array adapter to bind the array to the listview
        mItemsAdapter = new ItemUnexecAdapter(getActivity(), list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unexec_list_fragment, container, false);
        Log.d(TAG, "onCreateView_0");

        // Get references to UI widgets
        mListView = (ListView)view.findViewById(R.id.unexec_list_view);

        // Bind the array adapter to the listview.
        mListView.setAdapter(mItemsAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String strOut = String.format("(I=%d, L=%d)", i, l);
                Log.d(TAG, "onItemClick: The Unexec-item out=" + strOut);
                openItemFragment(i);
            }
        });
       
        return view;
    }

    private void openItemFragment(int n_position)
    {
        Pay item = mItemsAdapter.getPayByPosition(n_position);
        PRApplication.INSTANCE.getRouter().navigateTo(MainActivity.PAYMENT_SCREEN, (Object)item);
    }

    // see:
    // https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
    private class ItemUnexecAdapter extends ArrayAdapter<Pay> {

        public ItemUnexecAdapter(Context context, ArrayList<Pay> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Pay itemPay = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_unexec, parent, false);
            }
            // Lookup view for data population
            TextView tvText = (TextView) convertView.findViewById(R.id.tv_text);
            TextView tvId = (TextView) convertView.findViewById(R.id.tv_id);
            // Populate the data into the template view using the data object
            tvText.setText(itemPay.getTitle());
            String sInfo = String.format("День платежа: %d", itemPay.getDayOfMonth());
            tvId.setText(sInfo);
            // Return the completed view to render on screen
            return convertView;
        }

        public Pay getPayByPosition(int position) {
            Pay itemPay = getItem(position);
            return itemPay;
        }
    }
}

