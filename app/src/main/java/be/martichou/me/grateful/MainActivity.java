package be.martichou.me.grateful;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    LinearLayout grateful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grateful = findViewById(R.id.g2);
        grateful.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View grateful_image = findViewById(R.id.grateful_image);
                Intent intent = new Intent(MainActivity.this, ShowActivity.class);

                Pair<View, String> pair1 = Pair.create(grateful_image, ViewCompat.getTransitionName(grateful_image));

                ActivityOptions activity = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pair1);
                startActivity(intent, activity.toBundle());
            }
        });
    }
}
