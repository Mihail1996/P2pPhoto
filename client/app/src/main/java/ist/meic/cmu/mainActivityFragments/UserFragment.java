package ist.meic.cmu.mainActivityFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import ist.meic.cmu.R;
import ist.meic.cmu.asyncTasks.GetUserTask;
import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.PicassoClient;


public class UserFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LoggerFactory.log(this.getClass().getSimpleName() + ": Initialized");
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoggerFactory.log(this.getClass().getSimpleName() + ": Initialized");
        final SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Button findUser = getView().findViewById(R.id.findUserButton);
        TextInputEditText username = getView().findViewById(R.id.userInputNameText);
        findUser.setOnClickListener(v ->
                new GetUserTask(sharedPref, username.getText().toString(), new GetUserTask.Callback() {
                    @Override
                    public void onSuccess(String response) {
                        username.setText("");
                        TextView textView = getView().findViewById(R.id.userTextView);
                        textView.setText(response);
                    }

                    @Override
                    public void onError() {
                    }
                }).execute());
    }
}