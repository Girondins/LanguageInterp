package girondins.languageinterp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girondins on 22/10/15.
 */
public class AppActivity extends Activity {
    private Controller cont;
    private Language selectedForeign;
    private Language selectedNative;
    private int result;
    private String text;
    private String fragment;
    private Language lang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComp();

    }
    public void initComp(){
        cont = new Controller(this);
        Intent i = getIntent();
        selectedNative = (Language) i.getSerializableExtra("native");
        selectedForeign = (Language) i.getSerializableExtra("foreign");
        cont.setLanguages(selectedNative, selectedForeign);

    }



    public void onSpeech(Language toSpeak,String fragment) {
        this.fragment = fragment;
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        Intent detailsIntent =  new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
        sendOrderedBroadcast(
                detailsIntent, null, new LanguageDetailsChecker(), null, Activity.RESULT_OK, null, null);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, toSpeak.getAndroidCD());
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak");
        try {
            startActivityForResult(speechIntent, 1);
        }catch (ActivityNotFoundException a){
            Toast.makeText(this,"shurt up",Toast.LENGTH_LONG).show();
        }


    }

    public class LanguageDetailsChecker extends BroadcastReceiver
    {
        private List<String> supportedLanguages;

        private String languagePreference;

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Bundle results = getResultExtras(true);
            if (results.containsKey(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE))
            {
                languagePreference =
                        results.getString(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE);
            }
            if (results.containsKey(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES))
            {
                supportedLanguages =
                        results.getStringArrayList(
                                RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1&&resultCode==RESULT_OK&&data!=null){
           ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String trans = result.get(0);
            Log.d("TextSpeech",trans);
            cont.setVoiceToTextLang(fragment,trans);

        }
    }



    public void setForeign(Fragment frag, boolean backstack){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        ft.replace(R.id.foreignLayout, frag);
        if(backstack){
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    public void setSetting(Fragment frag, boolean backstack){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.settingsLayout, frag);
        if(backstack){
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    public void setNative(Fragment frag, boolean backstack){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        ft.replace(R.id.nativeLayout, frag);
        if(backstack){
            ft.addToBackStack(null);
        }
        ft.commit();
    }

}
