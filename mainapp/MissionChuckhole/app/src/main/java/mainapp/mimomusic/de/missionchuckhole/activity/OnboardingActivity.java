package mainapp.mimomusic.de.missionchuckhole.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ViewFlipper;

import mainapp.mimomusic.de.missionchuckhole.R;


/**
 * Class that handles showing Onboarding sites
 */
public class OnboardingActivity extends Activity {

    /**
     * minimum swipe distance to get to the next site
     */
    private static final int SWIPE_MIN_DISTANCE = 120;

    /**
     * max swipe distance to get to the next site
     */
    private static final int SWIPE_MAX_OFF_PATH = 250;

    /**
     * swipe threshold velocisty
     */
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    /**
     * ViewFlipper to swipe between sites
     */
    ViewFlipper viewSwitcher;

    /**
     * GestureDetector to handle swipes
     */
    private GestureDetector gestureDetector;

    /**
     * Listener to detect swipes
     */
    View.OnTouchListener gestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewSwitcher = (ViewFlipper) findViewById(R.id.viewFlipper);

        ImageButton ibOnboarding1 = (ImageButton) findViewById(R.id.btn_onboarding_1);
        ibOnboarding1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                viewSwitcher.setInAnimation(getApplicationContext(), R.anim.in_from_right);
                viewSwitcher.setOutAnimation(getApplicationContext(), R.anim.out_to_left);

                viewSwitcher.showNext();
            }
        });

        ImageButton ibOnboarding2 = (ImageButton) findViewById(R.id.btn_onboarding_2);
        ibOnboarding2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                viewSwitcher.setInAnimation(getApplicationContext(), R.anim.in_from_right);
                viewSwitcher.setOutAnimation(getApplicationContext(), R.anim.out_to_left);

                viewSwitcher.showNext();
            }
        });

        ImageButton ibOnboardingDone = (ImageButton) findViewById(R.id.btn_onboarding_3);
        ibOnboardingDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                OnboardingActivity.this.finish();
            }
        });
        gestureDetector = new GestureDetector(OnboardingActivity.this, new ChuckGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
    }

    class ChuckGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                    // right to left swipe
                    viewSwitcher.setInAnimation(getApplicationContext(), R.anim.in_from_right);
                    viewSwitcher.setOutAnimation(getApplicationContext(), R.anim.out_to_left);

                    viewSwitcher.showNext();
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                    // left to right swipe
                    viewSwitcher.setInAnimation(getApplicationContext(), R.anim.in_from_left);
                    viewSwitcher.setOutAnimation(getApplicationContext(), R.anim.out_to_right);

                    viewSwitcher.showPrevious();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
