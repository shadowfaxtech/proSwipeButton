package smart.ui.proswipebutton_app;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import smart.ui.proswipebutton.ProSwipeButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ProSwipeButton proSwipeBtn = findViewById(R.id.proswipebutton_main);
        final ProSwipeButton proSwipeBtnError = findViewById(R.id.proswipebutton_main_error);
        final ProSwipeButton reverseProSwipeBtnError = findViewById(R.id.proswipebutton_main_reverse);
        proSwipeBtn.setSwipeDistance(0.5f);

        proSwipeBtn.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        proSwipeBtn.showResultIcon(true, false);
                    }
                }, 2000);
            }
        });

        proSwipeBtnError.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        proSwipeBtnError.showResultIcon(false, true);
                    }
                }, 2000);
            }
        });

        reverseProSwipeBtnError.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reverseProSwipeBtnError.showResultIcon(true, true);
                    }
                }, 2000);
            }
        });
    }
}
