package girondins.languageinterp;

import android.app.Fragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by Alex on 2015-11-01.
 */
public class SettingsFragment extends Fragment {
    private ImageButton settingsButton;
    private ImageView squareFlagView;
    private Controller cont;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        squareFlagView = (ImageView)v.findViewById(R.id.squareFlagView);
        settingsButton = (ImageButton)v.findViewById(R.id.settingsButton);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsButtonClicked(v);
            }
        });

        return v;
    }

    private void settingsButtonClicked(View v) {
        // säg till controllern, (replaceFragment)
        //till OptionDialog-----> OptionDialog
    }

    public void setController(Controller cont){
        this.cont = cont;
    }

    /*
    public void setImages(Language language){
        // ta ut o sätt till rätt drawable
    }
*/

}