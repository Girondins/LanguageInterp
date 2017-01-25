package girondins.languageinterp;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by Alex on 2015-11-01.
 */
public class CountryFragment extends Fragment {
    private Language lang;
    private ImageButton countryButton;
    private Controller cont;
    private String fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_countyspeak, container, false);

        lang = (Language)getArguments().getSerializable("country");
        countryButton = (ImageButton) v.findViewById(R.id.flagButtonID);
        countryButton.setImageResource(lang.getFlag());
        countryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryButtonClicked();
            }
        });
        return v;
    }

    private void countryButtonClicked() {
        cont.onSpeech(lang, fragment);
    }


    public void setController(Controller cont){
        this.cont = cont;
    }

    public void setFragment(String fragment){
        this.fragment = fragment;
    }


}
