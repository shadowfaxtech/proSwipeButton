package in.shadowfax.proswipebutton;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by shadow-admin on 24/10/17.
 */

public class UiUtils {

    public static void animateFadeHide(Context context, View view) {
        if (view != null && view.getVisibility() == View.VISIBLE) {
            Animation animFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);

            view.startAnimation(animFadeOut);
            view.setVisibility(View.GONE);
        }
    }

    public static void animateFadeShow(Context context, View view) {
        if (view.getVisibility() != View.VISIBLE) {
            Animation animFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);

            view.startAnimation(animFadeIn);
            view.setVisibility(View.VISIBLE);
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}
