package in.shadowfax.proswipebutton;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static in.shadowfax.proswipebutton.UiUtils.animateFadeHide;
import static in.shadowfax.proswipebutton.UiUtils.animateFadeShow;
import static in.shadowfax.proswipebutton.UiUtils.dpToPx;

/**
 * Created by shadow-admin on 24/10/17.
 */

public class ProSwipeButton extends RelativeLayout {

    private Context context;
    private View view;
    private GradientDrawable gradientDrawable;
    private RelativeLayout contentContainer;
    private TextView contentTv;
    private ImageView arrow1;
    private ImageView arrow2;
    private LinearLayout arrowHintContainer;
    private ProgressBar progressBar;
    private static final float DEFAULT_TEXT_SIZE = dpToPx(14);
    private static final int BTN_INIT_RADIUS = dpToPx(2);
    private static final int BTN_MORPHED_RADIUS = dpToPx(100);
    private static final int MORPH_ANIM_DURATION = 500;

    //// TODO: 26/10/17 Add touch blocking

    /*
        User configurable settings
     */
    private CharSequence btnText = "BUTTON";
    @ColorInt
    private int textColorInt;
    @ColorInt
    private int bgColorInt;
    @ColorInt
    private int arrowColorInt;
    private float btnRadius = BTN_INIT_RADIUS;
    @Dimension
    private float textSize = DEFAULT_TEXT_SIZE;
    @Nullable
    private OnSwipeListener swipeListener = null;

    public ProSwipeButton(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ProSwipeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setAttrs(context, attrs);
        init();
    }

    public ProSwipeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setAttrs(context, attrs);
        init();
    }

    private void setAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ProSwipeButton,
                0, 0);
        try {
            final String btnString = a.getString(R.styleable.ProSwipeButton_btn_text);
            if (btnString != null)
                btnText = btnString;
            textColorInt = a.getColor(R.styleable.ProSwipeButton_text_color, ContextCompat.getColor(context, android.R.color.white));
            bgColorInt = a.getColor(R.styleable.ProSwipeButton_bg_color, ContextCompat.getColor(context, R.color.proswipebtn_red));
            arrowColorInt = a.getColor(R.styleable.ProSwipeButton_arrow_color, ContextCompat.getColor(context, R.color.proswipebtn_translucent_white));
            btnRadius = a.getFloat(R.styleable.ProSwipeButton_btn_radius, BTN_INIT_RADIUS);
            textSize = a.getDimensionPixelSize(R.styleable.ProSwipeButton_text_size, (int) DEFAULT_TEXT_SIZE);
        } finally {
            a.recycle();
        }
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.view_proswipebtn, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        contentContainer = view.findViewById(R.id.relativeLayout_swipeBtn_contentContainer);
        arrowHintContainer = view.findViewById(R.id.linearLayout_swipeBtn_hintContainer);
        contentTv = view.findViewById(R.id.tv_btnText);
        arrow1 = view.findViewById(R.id.iv_arrow1);
        arrow2 = view.findViewById(R.id.iv_arrow2);

        tintArrowHint();
        contentTv.setText(btnText);
        contentTv.setTextColor(textColorInt);
        contentTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(btnRadius);
        setBackgroundColor(bgColorInt);
        updateBackground();
        setupTouchListener();
    }

    private void setupTouchListener() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Movement logic here
                        if (event.getX() > arrowHintContainer.getWidth() / 2 &&
                                event.getX() + arrowHintContainer.getWidth() / 2 < getWidth() &&
                                (event.getX() < arrowHintContainer.getX() + arrowHintContainer.getWidth() || arrowHintContainer.getX() != 0)) {
                            // snaps the hint to user touch, only if the touch is within hint width or if it has already been displaced
                            arrowHintContainer.setX(event.getX() - arrowHintContainer.getWidth() / 2);
                        }

                        if (arrowHintContainer.getX() + arrowHintContainer.getWidth() > getWidth() &&
                                arrowHintContainer.getX() + arrowHintContainer.getWidth() / 2 < getWidth()) {
                            // allows the hint to go up to a max of btn container width
                            arrowHintContainer.setX(getWidth() - arrowHintContainer.getWidth());
                        }

                        if (event.getX() < arrowHintContainer.getWidth() / 2 &&
                                arrowHintContainer.getX() > 0) {
                            // allows the hint to go up to a min of btn container starting
                            arrowHintContainer.setX(0);
                        }

                        return true;
                    case MotionEvent.ACTION_UP:
                        //Release logic here
                        if (arrowHintContainer.getX() + arrowHintContainer.getWidth() > getWidth() * 0.85) {
                            // swipe completed, fly the hint away!
                            animateFadeHide(context, arrowHintContainer);
                            if (swipeListener != null)
                                swipeListener.onSwipeConfirm();
                            morphToCircle();
                        } else if (arrowHintContainer.getX() <= 0) {
                            // upon click without swipe
                            startFwdAnim();
                        } else {
                            // swipe not completed, pull back the hint
                            animateHintBack();
                        }
                        return true;
                }

                return false;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        startFwdAnim();
    }

    private void animateHintBack() {
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(arrowHintContainer.getX(), 0);
        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (Float) positionAnimator.getAnimatedValue();
                arrowHintContainer.setX(x);
            }
        });

        positionAnimator.setDuration(200);
        positionAnimator.start();
    }

    private void startFwdAnim() {
        TranslateAnimation animation = new TranslateAnimation(0, getMeasuredWidth(), 0, 0);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startHintInitAnim();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        arrowHintContainer.startAnimation(animation);
    }

    /**
     * animate entry of hint from the left-most edge
     */
    private void startHintInitAnim() {
        TranslateAnimation anim = new TranslateAnimation(-arrowHintContainer.getWidth(), 0, 0, 0);
        anim.setDuration(500);
        arrowHintContainer.startAnimation(anim);
    }

    private void morphToCircle() {
        setOnTouchListener(null);
        ObjectAnimator cornerAnimation =
                ObjectAnimator.ofFloat(gradientDrawable, "cornerRadius", BTN_INIT_RADIUS, BTN_MORPHED_RADIUS);

        animateFadeHide(context, contentTv);
        ValueAnimator widthAnimation;
        widthAnimation = ValueAnimator.ofInt(getWidth(), dpToPx(50));
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = contentContainer.getLayoutParams();
                layoutParams.width = val;
                contentContainer.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator heightAnimation = ValueAnimator.ofInt(getHeight(), dpToPx(50));
        heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = contentContainer.getLayoutParams();
                layoutParams.height = val;
                contentContainer.setLayoutParams(layoutParams);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(MORPH_ANIM_DURATION);
        animatorSet.playTogether(cornerAnimation, widthAnimation, heightAnimation);
        animatorSet.start();

        showProgressBar();
    }

    private void morphToRect() {
        setupTouchListener();
        ObjectAnimator cornerAnimation =
                ObjectAnimator.ofFloat(gradientDrawable, "cornerRadius", BTN_MORPHED_RADIUS, BTN_INIT_RADIUS);

        ValueAnimator widthAnimation;
        widthAnimation = ValueAnimator.ofInt(dpToPx(50), getWidth());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = contentContainer.getLayoutParams();
                layoutParams.width = val;
                contentContainer.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator heightAnimation = ValueAnimator.ofInt(dpToPx(50), getWidth());
        heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = contentContainer.getLayoutParams();
                layoutParams.height = val;
                contentContainer.setLayoutParams(layoutParams);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(MORPH_ANIM_DURATION);
        animatorSet.playTogether(cornerAnimation, widthAnimation, heightAnimation);
        animatorSet.start();
    }

    public void updateBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            contentContainer.setBackground(gradientDrawable);
        } else {
            // noinspection deprecation
            contentContainer.setBackgroundDrawable(gradientDrawable);
        }
    }

    private void showProgressBar() {
        progressBar = new ProgressBar(context);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, android.R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        animateFadeHide(context, contentTv);
        contentContainer.addView(progressBar);
    }

    public void showResultIcon(boolean isSuccess) {
        animateFadeHide(context, progressBar);

        final ImageView failureIcon = new ImageView(context);
        RelativeLayout.LayoutParams icLayoutParams =
                new RelativeLayout.LayoutParams(dpToPx(50), dpToPx(50));
        failureIcon.setLayoutParams(icLayoutParams);
        failureIcon.setVisibility(GONE);
        Drawable icon;
        if (isSuccess)
            icon = ContextCompat.getDrawable(context, R.drawable.ic_check_circle_36dp);
        else
            icon = ContextCompat.getDrawable(context, R.drawable.ic_cancel_full_24dp);
        failureIcon.setImageDrawable(icon);
        contentContainer.addView(failureIcon);
        animateFadeShow(context, failureIcon);

        if (!isSuccess) {
            // expand the btn again
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateFadeHide(context, failureIcon);
                    morphToRect();
                    arrowHintContainer.setX(0);
                    animateFadeShow(context, arrowHintContainer);
                    animateFadeShow(context, contentTv);
                }
            }, 1000);
        }
    }

    private void tintArrowHint() {
        arrow1.setColorFilter(arrowColorInt, PorterDuff.Mode.MULTIPLY);
        arrow2.setColorFilter(arrowColorInt, PorterDuff.Mode.MULTIPLY);
    }

    public interface OnSwipeListener {
        void onSwipeConfirm();
    }

    public void setText(CharSequence text) {
        this.btnText = text;
        contentTv.setText(text);
    }

    public CharSequence getText() {
        return this.btnText;
    }

    public void setTextColor(@ColorInt int textColor) {
        this.textColorInt = textColor;
        contentTv.setTextColor(textColor);
    }

    @ColorInt
    public int getTextColor() {
        return this.textColorInt;
    }

    public void setBackgroundColor(@ColorInt int bgColor) {
        this.bgColorInt = bgColor;
        gradientDrawable.setColor(bgColor);
        updateBackground();
    }

    @ColorInt
    public int getBackgroundColor() {
        return this.bgColorInt;
    }

    public void setCornerRadius(float cornerRadius) {
        this.btnRadius = cornerRadius;
    }

    public float getCornerRadius() {
        return this.btnRadius;
    }

    public int getArrowColorRes() {
        return this.arrowColorInt;
    }

    /**
     * Include alpha in arrowColor for transparency (ex: #33FFFFFF)
     */
    public void setArrowColor(int arrowColor) {
        this.arrowColorInt = arrowColor;
        tintArrowHint();
    }

    public void setTextSize(@Dimension float textSize) {
        this.textSize = textSize;
        contentTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    @Dimension
    public float getTextSize() {
        return this.textSize;
    }

    public void setOnSwipeListener(@Nullable OnSwipeListener customSwipeListener) {
        this.swipeListener = customSwipeListener;
    }

}
