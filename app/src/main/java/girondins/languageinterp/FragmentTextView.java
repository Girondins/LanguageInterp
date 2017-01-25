package girondins.languageinterp;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by Alex on 2015-11-01.
 */
public class FragmentTextView extends Fragment {
    private TextView textView;
    private Button replyButton;
    private Button reListenButton;
    private Controller cont;
    private String fragment;
    private String translatedText;
    private Language lang;
    private AppActivity appAct;
    private TextToSpeech tts;
    private int result;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_textview, container, false);


        translatedText = getArguments().getString("translated");
        lang = (Language)getArguments().getSerializable("lang");
        textView = (TextView)v.findViewById(R.id.tvReply);
        replyButton = (Button)v.findViewById(R.id.replyButtonID);
        reListenButton = (Button)v.findViewById(R.id.listenAgainID);
        textView.setText(translatedText);

        tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                 result = tts.setLanguage(new Locale(lang.getAndroidCD()));
                    if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA){
                        Toast.makeText(getActivity().getApplicationContext(), "Feature is not supported", Toast.LENGTH_LONG).show();
                    }else{
                        tts.speak(translatedText, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }else
                    Toast.makeText(getActivity().getApplicationContext(), "Feature is not supported", Toast.LENGTH_LONG).show();
            }
        });


        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cont.onSpeech(lang,fragment);
            }
        });


        reListenButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA){
                    Toast.makeText(getActivity().getApplicationContext(), "Feature is not supported", Toast.LENGTH_LONG).show();
                }else{
                    tts.speak(translatedText, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });

        return v;
    }



    public void setController(Controller cont){
        this.cont = cont;
    }

    public void setFragment(String fragment){
        this.fragment = fragment;
    }

    public void setAppAct(AppActivity appAct){
        this.appAct = appAct;
    }


}
