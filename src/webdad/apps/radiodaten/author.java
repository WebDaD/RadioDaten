package webdad.apps.radiodaten;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class author extends Activity {
	 @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {	
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.author);
	        final Button btn = (Button) findViewById(R.id.btn_close);
	        btn.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					finish();
				}
			});
	        
	        String text="(c) by Dominik Sigmund\nWebDaD.eu\n2012\n\nFor Errors:\ndominik.sigmund@webdad.eu";
	        
	        
	        final TextView txt = (TextView) findViewById(R.id.txt_author);
	        txt.setText(text);
	    }

}
