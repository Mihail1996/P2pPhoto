package ist.meic.cmu.mainActivityFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ist.meic.cmu.R;
import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.PicassoClient;


public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LoggerFactory.log(this.getClass().getSimpleName() + ": Initialized");
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button settingsButton = getView().findViewById(R.id.settings_apply_button);
        settingsButton.setOnClickListener((v -> {
            EditText text = getView().findViewById(R.id.settings_inputText);
            PicassoClient.init(getContext(), Integer.parseInt(text.getText().toString()));
        }));

    }
}