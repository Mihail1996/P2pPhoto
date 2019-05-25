package ist.meic.cmu.mainActivityFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dropbox.core.v2.users.FullAccount;

import ist.meic.cmu.R;
import ist.meic.cmu.asyncTasks.GetTokenTask;
import ist.meic.cmu.asyncTasks.dropbox.GetCurrentAccountTask;
import ist.meic.cmu.utils.DropboxClientFactory;
import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.PicassoClient;

public class InfoFragment extends Fragment {
    private static final String TAG = InfoFragment.class.getName();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoggerFactory.log(this.getClass().getSimpleName()+": Initialized");
        final SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        new GetTokenTask(new GetTokenTask.Callback() {
            @Override
            public void onSuccess(String token) {
                sharedPref.edit().putString("token", token).apply();
                initAndLoadData(token);
            }

            @Override
            public void onError() {
                Toast.makeText(getContext(), "Error Retrieving the Token", Toast.LENGTH_LONG).show();
            }
        }, sharedPref).execute();

    }

    private void initAndLoadData(String accessToken) {
        DropboxClientFactory.init(accessToken);
        PicassoClient.init(getContext());
        loadData(accessToken);
    }

    protected void loadData(final String token) {
        new GetCurrentAccountTask(DropboxClientFactory.getClient(), new GetCurrentAccountTask.Callback() {
            @Override
            public void onComplete(FullAccount result) {
                ((TextView) getView().findViewById(R.id.info_frag_text)).setText(result.getName().getDisplayName() + "\n" + result.getEmail() + "\n" + token);
            }

            @Override
            public void onError(Exception e) {
                Log.e(getClass().getSimpleName(), "Failed to get account details.", e);
            }
        }).execute();
    }


}

