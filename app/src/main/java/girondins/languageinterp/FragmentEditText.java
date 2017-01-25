package girondins.languageinterp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Alex on 2015-11-01.
 */
public class FragmentEditText extends Fragment {
    private EditText textView;
    private Button comfimButton;
    private Button speakAgainBtn;
    private Controller cont;
    private String fragment;
    private Language lang;
    private String textEdit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edittext, container, false);

        lang = (Language)getArguments().getSerializable("lang");
        textEdit = getArguments().getString("translate");
        textView = (EditText)v.findViewById(R.id.TextView);
        comfimButton = (Button)v.findViewById(R.id.comfirmButton);
        speakAgainBtn = (Button)v.findViewById(R.id.SpeakAgainButton);
        textView.setText(textEdit);
        comfimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onComfirm();
            }
        });

        speakAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });

        return v;
    }


    private void onComfirm() {
        cont.setLangButton(fragment);
        cont.translatedResult(textView.getText().toString(), fragment);
    }


    private void onCancel() {
       cont.onSpeech(lang, fragment);
    }

    public void setController(Controller cont){
        this.cont = cont;
    }

    public void setFragment(String fragment){
        this.fragment = fragment;
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}