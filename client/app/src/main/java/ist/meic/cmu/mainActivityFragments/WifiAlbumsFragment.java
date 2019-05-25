package ist.meic.cmu.mainActivityFragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ist.meic.cmu.R;
import ist.meic.cmu.adapters.AlbumAdapter;
import ist.meic.cmu.asyncTasks.CreateWifiAlbumTask;
import ist.meic.cmu.asyncTasks.ListWifiAlbumsTask;
import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.PicassoClient;

public class WifiAlbumsFragment extends Fragment {

    private static final String TAG = WifiAlbumsFragment.class.getName();
    private AlbumAdapter mAlbumAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_albums, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoggerFactory.log(this.getClass().getSimpleName() + ": Initialized");
        FloatingActionButton fab = getView().findViewById(R.id.albums_fab_button);
        fab.setOnClickListener(v -> createAlbum());
        RecyclerView recyclerView = getView().findViewById(R.id.albums_list);
        mAlbumAdapter = new AlbumAdapter(PicassoClient.getPicasso(), name -> {
            Toast.makeText(getContext(), "Clicked Album " + name, Toast.LENGTH_LONG).show();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new WifiPhotosFragment(name)).commit();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAlbumAdapter);
        loadData();
    }

    private void createAlbum() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Title");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String albumName = input.getText().toString();
            new CreateWifiAlbumTask(new CreateWifiAlbumTask.Callback() {
                @Override
                public void onAlbumCreated() {
                    Toast.makeText(getContext(), "Album Created", Toast.LENGTH_LONG).show();
                    loadData();
                }

                @Override
                public void onError() {

                    Toast.makeText(getContext(), "Error Creating or name already used", Toast.LENGTH_LONG).show();
                }
            }, getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)).execute(albumName);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void loadData() {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Loading");
        dialog.show();

        new ListWifiAlbumsTask(result -> {
            dialog.dismiss();
            if (result.isEmpty()) {
                Toast.makeText(getContext(), "You have no albums", Toast.LENGTH_LONG).show();
            } else {
                mAlbumAdapter.setAlbums(result);
            }
        }, getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)).execute();
    }

}
