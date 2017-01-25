package girondins.languageinterp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girondins on 20/10/15.
 */
public class LanguageSelectDialog extends DialogFragment{
    private LayoutInflater inflater;
    private View v;
    private ListView languages;
    private ArrayList<Language> countries;
    private Controller cont;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.languageselectdialog, null);
        languages = (ListView)v.findViewById(R.id.languageListID);
        languages.setAdapter(new LanguageAdapter(getActivity(), countries,cont));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.selectLanguage);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cont.onConfirmedLanguage();
            }
        });
        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();
    }



    public void setController(Controller cont, ArrayList<Language> countries){
        this.cont = cont;
        this.countries = countries;
    }

}
