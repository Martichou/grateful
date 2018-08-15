package be.martichou.me.grateful;

import android.app.Activity;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionListenerAdapter;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.shimmer.ShimmerFrameLayout;

public class ShowActivity extends Activity {

    private ShimmerFrameLayout mShimmerViewContainer;
    private LinearLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        main = findViewById(R.id.main);

        main.setAlpha(0.2f);

        Transition sharedElementEnterTransition = getWindow().getSharedElementEnterTransition();
        sharedElementEnterTransition.addListener(new TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);
                main.animate().alpha(1f).setDuration(450);
                super.onTransitionEnd(transition);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!(mShimmerViewContainer.getVisibility() == View.GONE)) {
            mShimmerViewContainer.startShimmer();
        }
    }

    @Override
    public void onPause() {
        if(mShimmerViewContainer.isShimmerStarted()) {
            mShimmerViewContainer.stopShimmer();
        }
        super.onPause();
    }
}
