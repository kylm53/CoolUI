package com.kkk.coolui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.kkk.coolui.R;

/**
 * 即刻点赞效果
 */
public class LikeView extends View {
    private int likeCount;
    private int color = Color.RED;
    private int width;
    private int height;

    private boolean isLiked = false;
    private boolean isChangeBitmap = false;
    private boolean isAnimate = false;

    private TextPaint paint;
    private float scale = 1;
    private float circleAnimateFraction = 0;
    private float numberAnimateFraction = 0;
    private float shiningAnimateFraction = 0;
    private float shiningDismissAnimateFraction = 0;


    private Bitmap bitmapShining = BitmapFactory.decodeResource(getContext().getResources(),
            R.drawable.ic_messages_like_selected_shining);
    private Bitmap bitmapLike = BitmapFactory.decodeResource(getContext().getResources(),
            R.drawable.ic_messages_like_selected);
    Bitmap bitmapLikeUnselected = BitmapFactory.decodeResource(getContext().getResources(),
    R.drawable.ic_messages_like_unselected);

    private int shiningWidth = bitmapShining.getWidth();
    private int shiningHeight = bitmapShining.getHeight();
    int likeWidth = bitmapLike.getWidth();
    int likeHeight = bitmapLike.getHeight();

    public LikeView(Context context) {
        super(context);
        init(null, 0);
    }

    public LikeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public LikeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.LikeView, defStyle, 0);

        likeCount = a.getInt(
                R.styleable.LikeView_likeCount, 0);
        likeCount = 308;
        color = a.getColor(
                R.styleable.LikeView_color, color);

        a.recycle();

        paint = new TextPaint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        int centerX = width / 2;
        int centerY = height / 2;

        canvas.save();
        canvas.translate(centerX - likeWidth / 2, centerY - likeHeight / 2);
        if (isAnimate) {
            Bitmap bitmap;
            if (isLiked) {
                bitmap = bitmapLikeUnselected;
                if (isChangeBitmap) {
                    bitmap = bitmapLike;
                    canvas.save();
                    canvas.scale(shiningAnimateFraction, shiningAnimateFraction, 14+shiningWidth / 2, shiningHeight/2);
                    canvas.drawBitmap(bitmapShining, 14, 0, paint);
                    canvas.restore();
                }
                canvas.save();
                canvas.scale(scale, scale, 10 + likeWidth / 2, shiningHeight - 15 + likeHeight/2);
                canvas.drawBitmap(bitmap, 10, shiningHeight - 15, paint);
                canvas.restore();

                paint.setColor(Color.argb((int)(0x66 * (1- circleAnimateFraction)), 0xE5, 0x5B, 0x41));
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(5);
                canvas.drawCircle(10 + likeWidth / 2, shiningHeight - 15 + likeHeight/2, likeHeight*0.7f/2 + 18* circleAnimateFraction, paint);


                paint.reset();
                paint.setColor(Color.GRAY);
                paint.setTextSize(40);
                Paint.FontMetrics metrics = paint.getFontMetrics();
                float textHeight = metrics.descent - metrics.ascent;

                float lineHeight = paint.getFontSpacing();

                String fixedNumber = "";
                String preNumber = "";
                String increasedNumber = "";

                int preCount = likeCount - 1;
                String preCountStr = String.valueOf(preCount);
                int numberLength = preCountStr.length();

                int temp = preCount;
                int i = 1;
                while (true) {
                    int divider = 10;
                    if (temp % divider == 9) {
                        temp /= divider;
                        i++;
                    } else {
                        fixedNumber = preCountStr.substring(0, numberLength - i);
                        preNumber = preCountStr.substring(numberLength - i, numberLength);
                        int increased = Integer.parseInt(preNumber) + 1;
                        increasedNumber = String.valueOf(increased);
                        break;
                    }
                }
                float fixedNumberWidth = paint.measureText(fixedNumber);
                canvas.drawText(fixedNumber, likeWidth + 15,
                        shiningHeight + likeHeight - textHeight / 2, paint);

                paint.setAlpha((int) (0xFF * (1-numberAnimateFraction)));
                canvas.drawText(preNumber, likeWidth + 15 + fixedNumberWidth,
                        shiningHeight + likeHeight - textHeight / 2 - lineHeight * numberAnimateFraction, paint);
                paint.setAlpha((int) (0xFF * numberAnimateFraction));
                canvas.drawText(increasedNumber, likeWidth + 15 + fixedNumberWidth,
                        shiningHeight + likeHeight - textHeight / 2 + lineHeight - lineHeight * numberAnimateFraction, paint);

            } else {
                bitmap = bitmapLike;
                if (isChangeBitmap) {
                    bitmap = bitmapLikeUnselected;
                }

                paint.setAlpha((int) (0xFF * (1 - shiningDismissAnimateFraction)));
                canvas.drawBitmap(bitmapShining, 14, 0, paint);
                paint.reset();

                canvas.save();
                canvas.scale(scale, scale, 10 + likeWidth / 2, shiningHeight - 15 + likeHeight/2);
                canvas.drawBitmap(bitmap, 10, shiningHeight - 15, paint);
                canvas.restore();

                paint.reset();
                paint.setColor(Color.GRAY);
                paint.setTextSize(40);
                Paint.FontMetrics metrics = paint.getFontMetrics();
                float textHeight = metrics.descent - metrics.ascent;

                float lineHeight = paint.getFontSpacing();

                String fixedNumber = "";
                String preNumber = "";
                String increasedNumber = "";

                int preCount = likeCount + 1;
                String preCountStr = String.valueOf(preCount);
                int numberLength = preCountStr.length();
                int temp = preCount;
                int i = 1;
                while (true) {
                    int divider = 10;
                    if (temp % divider == 0) {
                        temp /= divider;
                        i++;
                    } else {
                        fixedNumber = preCountStr.substring(0, numberLength - i);
                        preNumber = preCountStr.substring(numberLength - i, numberLength);
                        int increased = Integer.parseInt(preNumber) - 1;
                        increasedNumber = String.valueOf(increased);
                        if (preNumber.startsWith("1")) {
                            increasedNumber = "0" + increasedNumber;
                        }
                        break;
                    }
                }
                float fixedNumberWidth = paint.measureText(fixedNumber);
                canvas.drawText(fixedNumber, likeWidth + 15,
                        shiningHeight + likeHeight - textHeight / 2, paint);

                paint.setAlpha((int) (0xFF * (numberAnimateFraction)));
                canvas.drawText(increasedNumber, likeWidth + 15 + fixedNumberWidth,
                        shiningHeight + likeHeight - textHeight / 2 - lineHeight + lineHeight * numberAnimateFraction, paint);
                paint.setAlpha((int) (0xFF * (1-numberAnimateFraction)));
                canvas.drawText(preNumber, likeWidth + 15 + fixedNumberWidth,
                        shiningHeight + likeHeight - textHeight / 2 + lineHeight * numberAnimateFraction, paint);
                paint.reset();
            }
        } else {
            if (isLiked) {
                canvas.drawBitmap(bitmapShining, 14, 0, paint);
                canvas.drawBitmap(bitmapLike, 10, shiningHeight - 15, paint);
            } else {
                canvas.drawBitmap(bitmapLikeUnselected, 10, shiningHeight - 15, paint);
            }

            paint.reset();
            paint.setColor(Color.GRAY);
            paint.setTextSize(40);
            Paint.FontMetrics metrics = paint.getFontMetrics();
            float textHeight = metrics.descent - metrics.ascent;
            canvas.drawText(String.valueOf(likeCount), likeWidth + 15,
                    shiningHeight + likeHeight - textHeight / 2, paint);
        }
        canvas.translate(-centerX+likeWidth / 2, -centerY+likeHeight / 2);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:
                if (isLiked) {
                    likeCount--;
                } else {
                    likeCount++;
                }
                isLiked = !isLiked;
                animateIncrease();
                break;
            default:

                break;
        }
        return true;
    }

    private int animatorStopCount = 0;
    private void stopAnimator() {
        animatorStopCount ++;
        if(animatorStopCount == 5) {
            isAnimate = false;
            isChangeBitmap = false;
            animatorStopCount = 0;
        }
    }

    private void animateIncrease() {
        final ValueAnimator animatorShining = ValueAnimator.ofFloat(0, 1.3f, 1);
        animatorShining.setInterpolator(new AccelerateInterpolator());

        final ValueAnimator animatorCircle = ValueAnimator.ofFloat(0, 1);
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0.7f, 1);
        ValueAnimator animatorNumber = ValueAnimator.ofFloat(0, 1);
        ValueAnimator animatorShiningDismiss = ValueAnimator.ofFloat(0, 1);
        animator.setInterpolator(new OvershootInterpolator());
        animatorCircle.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(400);
        animatorNumber.setDuration(400);
        animatorCircle.setDuration(300);
        animatorShining.setDuration(200);
        animatorShiningDismiss.setDuration(200);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scale = (float) animation.getAnimatedValue();

                if (0.7f < scale && scale < 0.73f) {
                    isChangeBitmap = true;
                    animatorCircle.start();
                    animatorShining.start();
                }
                invalidate();
            }
        });
        animatorCircle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                circleAnimateFraction = animation.getAnimatedFraction();
            }
        });
        animatorShiningDismiss.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                shiningDismissAnimateFraction = animation.getAnimatedFraction();
            }
        });
        animatorNumber.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                numberAnimateFraction = animation.getAnimatedFraction();
            }
        });
        animatorShining.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                shiningAnimateFraction = (float) animation.getAnimatedValue();
            }
        });

        AnimatorListenerAdapter listenerAdapter = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimate = true;
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                stopAnimator();
            }
        };
        animator.addListener(listenerAdapter);
        animatorNumber.addListener(listenerAdapter);
        animatorShining.addListener(listenerAdapter);
        animatorShiningDismiss.addListener(listenerAdapter);
        animatorCircle.addListener(listenerAdapter);

        animator.start();
        animatorNumber.start();
        animatorShiningDismiss.start();
    }
}
