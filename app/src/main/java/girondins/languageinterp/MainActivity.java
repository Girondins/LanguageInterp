package girondins.languageinterp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;

public class MainActivity extends Activity {
    private ImageButton earthButton;
    private RotateAnimation rotation;
    private Controller cont;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earth_layout);
        components();
        registerListeners();
    }

    private void components() {
        earthButton = (ImageButton)findViewById(R.id.earthButton);
        rotation = new RotateAnimation(0,127);
        cont = new Controller(this);
        earthButton.setBackgroundResource(R.drawable.eartha);

    }

    private void registerListeners() {
        earthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                rotation.setFillAfter(true);
                rotation.setDuration(1000);
                earthButton.startAnimation(rotation);*/
                cont.openLanguageDialog();
                earthButton.setBackgroundResource(R.drawable.eartha);
            // koppla till controller
                // ok i Option = new intent starta ny activity
            }
        });
    }


}
