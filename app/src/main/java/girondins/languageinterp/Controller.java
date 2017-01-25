package girondins.languageinterp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Girondins on 22/10/15.
 */
public class Controller {
    private Activity activity;
    private ArrayList<Language> languages = new ArrayList<Language>();
    private Language selectedForeign;
    private Language selectedNative;
    private Translator translate;
    private String deviceLanguage;


    public Controller(Activity activity){
        this.activity = activity;
        initiateLanguages();
        translate = new Translator(this);
        getLangSettings();
        getNativeLanguage();
        getForeignLanguage();
        if (activity instanceof AppActivity) {
            initiateFragments();
        }
        Log.d("Searching Language", deviceLanguage);
    }

    public void initiateFragments() {
        AppActivity appAct = (AppActivity)activity;
        Bundle nativeInfo = new Bundle();
        nativeInfo.putSerializable("country", selectedNative);
        CountryFragment nativeCountry = new CountryFragment();
        nativeCountry.setArguments(nativeInfo);
        nativeCountry.setController(this);
        nativeCountry.setFragment("native");

        Bundle foreignInfo = new Bundle();
        foreignInfo.putSerializable("country", selectedForeign);
        CountryFragment foreignCountry = new CountryFragment();
        foreignCountry.setArguments(foreignInfo);
        foreignCountry.setController(this);
        foreignCountry.setFragment("foreign");

        SettingsFragment settingLay = new SettingsFragment();

        appAct.setForeign(foreignCountry,false);
        appAct.setNative(nativeCountry, false);
        appAct.setSetting(settingLay,false);

    }


    public void getForeignLanguage() {
        String foreignLanguage;
        SharedPreferences sp = activity.getSharedPreferences("selectedLanguages", Activity.MODE_PRIVATE);
        foreignLanguage = sp.getString("foreignLanguage", null);
            if(selectedForeign == null) {
                for (int i = 0; i < languages.size(); i++) {
                    if (languages.get(i).getCountry().equals(foreignLanguage)) {
                        selectedForeign = languages.get(i);
                    }
                }
            }
    }

    public void getNativeLanguage() {
        for (int i = 0; i < languages.size(); i++) {
            if (languages.get(i).getCountry().equals(deviceLanguage)) {
                selectedNative = languages.get(i);
                Log.d("NativeGet", selectedNative.getCountry());
            }
        }
    }

    public void getLangSettings() {
        SharedPreferences sp = activity.getSharedPreferences("Controller", Activity.MODE_PRIVATE);
        deviceLanguage = sp.getString("selectedNative", Locale.getDefault().getDisplayLanguage().toUpperCase().trim());
    }

    private void initiateLanguages() {
        Resources res = activity.getResources();
        InputStream is = res.openRawResource(R.raw.langs);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String readLine = null;

        try {
            readLine = br.readLine();
            while (readLine != null) {
                System.out.println(readLine);
                String[] split = readLine.split("/");
                languages.add(new Language(split[0], split[2], split[1],split[3], activity));
                readLine = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        languages.get(0).setSelected(true);

    }

    public void openLanguageDialog() {
        LanguageSelectDialog languageSelect = new LanguageSelectDialog();
        languageSelect.setController(this, languages);
        languageSelect.show(activity.getFragmentManager(), "SelectDialogFrag");
    }

    public void onConfirmedLanguage() {
        findSelected();
        SharedPreferences sp = activity.getSharedPreferences("selectedLanguages", Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("foreignLanguage", selectedForeign.getCountry());
        edit.apply();
        Intent intent = new Intent(activity, AppActivity.class);
        intent.putExtra("native", selectedNative);
        intent.putExtra("foreign", selectedForeign);
        activity.startActivity(intent);
    }

    public void onChangedForeignLanguage() {
        SharedPreferences sp = activity.getSharedPreferences("selectedLanguages", Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("foreignLanguage", selectedForeign.getCountry());
        edit.apply();
    }

    public void onChangedNativeLanguage() {

    }

    public void removeAllSelected() {
        for (int i = 0; i < languages.size(); i++) {
            languages.get(i).setSelected(false);
            Log.d("Controller", "All set to false");
        }
    }

    public void isSelected(String language) {
        for (int i = 0; i < languages.size(); i++) {
            Log.d("Controller", "Finding " + languages.get(i).getCountry());
            if (languages.get(i).getCountry().equals(language)) {
                Log.d("Controller", languages.get(i).getCountry() + " Set to true");
                languages.get(i).setSelected(true);
            }
        }
    }

    public void findSelected() {
        for (int i = 0; i < languages.size(); i++) {
            if (languages.get(i).isSelected() == true) {
                selectedForeign = languages.get(i);
            }
        }
    }

    public void setLanguages(Language nativeLang, Language foreingLang) {
        selectedNative = nativeLang;
        selectedForeign = foreingLang;
    }

    public void translatedResult(String result, String fragment) {
        if (fragment.equals("native")) {
            translate.translate(selectedNative, selectedForeign, result, fragment);
        } else
            translate.translate(selectedForeign, selectedNative, result, fragment);

    }



    public void onSpeech(Language lang,String fragment){
        AppActivity appAct = (AppActivity) activity;
        appAct.onSpeech(lang,fragment);
    }

    public void setTextToSpeech(String fragment,String translatedText){
        AppActivity appAct = (AppActivity)activity;
        if(fragment.equals("native")){
            Bundle info = new Bundle();
            FragmentTextView fragText = new FragmentTextView();
            info.putSerializable("lang", selectedForeign);
            info.putString("translated", translatedText);
            fragText.setArguments(info);
            fragText.setFragment("foreign");
            fragText.setAppAct(appAct);
            fragText.setController(this);
            appAct.setForeign(fragText, false);
        }else{
            Bundle info = new Bundle();
            FragmentTextView fragText = new FragmentTextView();
            info.putSerializable("lang", selectedNative);
            info.putString("translated", translatedText);
            fragText.setArguments(info);
            fragText.setFragment("native");
            fragText.setAppAct(appAct);
            fragText.setController(this);
            appAct.setNative(fragText, false);
        }


    }



    public void setVoiceToTextLang(String fragment,String textToEdit){
        AppActivity appAct = (AppActivity)activity;
        if(fragment.equals("native")){
            Bundle info = new Bundle();
            FragmentEditText fragEdit = new FragmentEditText();
            info.putSerializable("lang", selectedNative);
            info.putString("translate", textToEdit);
            fragEdit.setArguments(info);
            fragEdit.setFragment(fragment);
            fragEdit.setController(this);
            appAct.setNative(fragEdit,false);
        }else{
            Bundle info = new Bundle();
            FragmentEditText fragEdit = new FragmentEditText();
            info.putSerializable("lang", selectedForeign);
            info.putString("translate", textToEdit);
            fragEdit.setArguments(info);
            fragEdit.setFragment(fragment);
            fragEdit.setController(this);
            appAct.setForeign(fragEdit,false);
        }

    }

    public void setLangButton(String fragment){
        AppActivity appAct = (AppActivity)activity;

        if(fragment.equals("native")){
            Bundle nativeInfo = new Bundle();
            nativeInfo.putSerializable("country", selectedNative);
            CountryFragment nativeCountry = new CountryFragment();
            nativeCountry.setArguments(nativeInfo);
            nativeCountry.setController(this);
            nativeCountry.setFragment("native");
            appAct.setNative(nativeCountry,false);
        }else{
            Bundle foreignInfo = new Bundle();
            foreignInfo.putSerializable("country", selectedForeign);
            CountryFragment foreignCountry = new CountryFragment();
            foreignCountry.setArguments(foreignInfo);
            foreignCountry.setController(this);
            foreignCountry.setFragment("foreign");
            appAct.setForeign(foreignCountry,false);
        }

    }




    public void viewTranslation(String trans,String fragment){
        activity.runOnUiThread(new viewTranslationThread(trans,fragment));
    }


    private class viewTranslationThread implements Runnable {
        String trans,fragment;
        private viewTranslationThread(String trans,String fragment){
            this.trans = trans;
            this.fragment = fragment;
        }

        @Override
        public void run() {
            Controller.this.setTextToSpeech(fragment,trans);
        }


    }



}
