package girondins.languageinterp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girondins on 20/10/15.
 */
public class LanguageAdapter extends ArrayAdapter<Language> {
    private LayoutInflater inflater;
    private List<Language> languageList = new ArrayList<>();
    private int selectedPos;
    private Controller cont;

    public LanguageAdapter(Context context, List<Language> languages,Controller cont) {
        super(context,R.layout.language_listview,languages);
        this.cont = cont;
        this.languageList = languages;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView language;
        ImageView countryFlag;
        RadioButton rb;

        if(convertView==null){
            convertView = inflater.inflate(R.layout.language_listview,parent,false);
        }
        language = (TextView)convertView.findViewById(R.id.languageTVID);
        countryFlag = (ImageView)convertView.findViewById(R.id.flagID);
        rb = (RadioButton)convertView.findViewById(R.id.radioButtonID);
        rb.setChecked(position == selectedPos);
        rb.setTag(position);
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPos = (Integer) v.getTag();
                cont.removeAllSelected();
                cont.isSelected(languageList.get(selectedPos).getCountry());
                notifyDataSetChanged();
            }
        });

        Language lang = languageList.get(position);
        language.setText(lang.getCountry());
        countryFlag.setImageResource(lang.getFlag());


        return convertView;
    }

}
