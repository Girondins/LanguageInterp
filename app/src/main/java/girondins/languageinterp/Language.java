package girondins.languageinterp;

import android.app.Activity;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by Girondins on 20/10/15.
 */
public class Language implements Serializable {
    private String country;
    private String androidCD;
    private String transCD;
    private int imageSource;
    private boolean selected;

    public Language(String country,String androidCD,String flag,String transCD,Activity activity){
        this.country = country.toUpperCase();
        this.androidCD = androidCD;
        this.transCD = transCD;
        int id = activity.getResources().getIdentifier(flag, "drawable", activity.getPackageName());
        imageSource = id;
        Log.d("Languageclass",imageSource+ "");
    }

    public int getFlag(){
        return this.imageSource;
    }

    public String getCountry(){
        return this.country;
    }

    public boolean isSelected(){
        return selected;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public String getAndroidCD(){
        return this.androidCD;
    }

    public String getTransCD(){
        return this.transCD;
    }




}
