package com.vansuita.passwordvault.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class PasswordStrengthView extends View {

    protected static final int COLOR_FAIL = Color.parseColor("#e74c3c");
    protected static final int COLOR_WEAK = Color.parseColor("#e67e22");
    protected static final int COLOR_STRONG = Color.parseColor("#2ecc71");

    protected int mMinWidth, mMinHeight;
    protected Paint mIndicatorPaint, mGuidePaint;
    protected int mIndicatorHeight, mIndicatorWidth, mCurrentScore;

    /**
     * Used to define that the indicator should only be looking
     * for a weak password. The bare minimum is used here to let
     * the user continue.
     */
    public static final int STRENGTH_WEAK = 0;

    /**
     * A fairly strict rule for generating a password. It encourages a password that is
     * less easy to crack.
     */
    public static final int STRENGTH_MEDIUM = 1;

    /**
     * A strong algorithm that encourages very strong passwords that should be fairly long, with
     * non-alphanumeric, numbers, and upper case.
     */
    public static final int STRENGTH_STRONG = 2;

    private int mStrengthRequirement = -1;
    protected String mPassword;

    public PasswordStrengthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMinHeight = 80;
        mMinWidth = 300;

        // Create and style the paint used for drawing the guide on the indicator
        mGuidePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGuidePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mGuidePaint.setColor(Color.BLACK);
        // Create and style paint for indicator
        mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * This view can determine if the password entered by the user is acceptable for
     * use by your use case. This is based on the strength requirement you have set.
     *
     * @return True if requirement has been met
     */
    public boolean isStrengthRequirementMet() {
        return (mCurrentScore >= 10);
    }

    /**
     * Change the strength requirement of the password entered by the user. This will also
     * re-check the password already entered against these new requirements.
     *
     * @param requiredStrength Use the public constants of this class to set
     */
    public void setStrengthRequirement(int requiredStrength) {
        if (requiredStrength >= 0 && requiredStrength <= 2) {
            mStrengthRequirement = requiredStrength;
            if (mPassword != null && mPassword.length() > 0) {
                generatePasswordScore();
                // Update view with new score
                invalidate();
                requestLayout();
            }
        } else {
            throw new IndexOutOfBoundsException("Input out of expected range");
        }
    }

    /**
     * Update the password string to check strength of
     *
     * @param passwordString String representation of user-input
     */
    public void setPassword(String passwordString) {
        if (passwordString != null && passwordString.length() > 0) {
            mPassword = passwordString;
            generatePasswordScore();
            // Update view with new score
            invalidate();
            requestLayout();
        } else {
            mPassword = "";
            mCurrentScore = 0;
            invalidate();
            requestLayout();
        }
    }

    /**
     * Private convenience method for adding to the password score
     *
     * @param score Amount to be added to current score
     */
    protected void addToPasswordScore(int score) {
        // Limit max score
        if ((mCurrentScore + score) > 20) {
            mCurrentScore = 20;
        } else {
            mCurrentScore = mCurrentScore + score;
        }
    }

    /**
     * Call this to determine the current strength requirement set on the algorithm
     *
     * @return Int representation of the current strength set for the indicator
     */
    public int getStrengthRequirement() {
        return mStrengthRequirement;
    }

    /**
     * Generate a score based on the password. The password will already need to be stored
     * as a class member before running this.
     */
    protected void generatePasswordScore() {
        mCurrentScore = 0;
        int upperCase = getUppercaseCount(mPassword);
        int nonAlpha = getNonAlphanumericCount(mPassword);
        int numbers = getNumberCount(mPassword);
        switch (mStrengthRequirement) {
            case STRENGTH_WEAK:
                addToPasswordScore(mPassword.length() * 2);
                addToPasswordScore(upperCase * 2);
                addToPasswordScore(nonAlpha * 2);
                addToPasswordScore(numbers * 2);
                break;

            case STRENGTH_MEDIUM:
                addToPasswordScore(mPassword.length());
                addToPasswordScore(upperCase);
                addToPasswordScore(nonAlpha * 2);
                addToPasswordScore(numbers);
                break;

            case STRENGTH_STRONG:
                addToPasswordScore(mPassword.length() / 2);
                // Cut the score in half to make this a very high requirement
                addToPasswordScore(upperCase);
                addToPasswordScore(nonAlpha);
                addToPasswordScore(numbers);
                break;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        int paddingX = getPaddingLeft();
        int paddingY = getPaddingTop();
        mIndicatorHeight = h - paddingY;
        mIndicatorWidth = w - paddingX;
    }

    /**
     * The standard parts of the onMeasure needed to create the password strength
     * indicator. Subclasses should call super.onMeasure, but also need to set
     * the minimum height and width in the constructor.
     *
     * @param widthMeasureSpec  The measurement given by the system
     * @param heightMeasureSpec The measurement given by the system
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Set minimum space for the view to do it's thing
        int minW = getPaddingLeft() + getPaddingRight() + mMinWidth;
        int w = resolveSizeAndState(minW, widthMeasureSpec, 1);
        // And give it enough height so it's visible
        int minH = mMinHeight + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(minH, heightMeasureSpec, 0);
        // Feed these back into UIKit
        setMeasuredDimension(w, h);
    }

    /**
     * Set the colour of the indicator {@code Paint} to one that is appropriate
     * for the strength of the password.
     */
    protected void generateIndicatorColor() {
        int color = COLOR_FAIL;
        if (mCurrentScore >= 18) {
            color = COLOR_STRONG;
        } else if (mCurrentScore >= 10) {
            color = COLOR_WEAK;
        }
        mIndicatorPaint.setColor(color);
    }

    /**
     * Quick method to determine how many of the characters in a given string are upper case
     *
     * @param stringToCheck The string to examine
     * @return Number of upper case characters
     */
    protected int getUppercaseCount(String stringToCheck) {
        int score = 0;
        int loops = stringToCheck.length() - 1;
        for (int i = 0; i <= loops; i++) {
            if (Character.isUpperCase(stringToCheck.charAt(i))) {
                score++;
            }
        }
        return score;
    }

    /**
     * A convenience method to determine how many characters in the given String aren't
     * letters or numbers.
     *
     * @param stringToCheck
     * @return Number of characters that aren't numbers or letters
     */
    protected int getNonAlphanumericCount(String stringToCheck) {
        int score = 0;
        int loops = stringToCheck.length() - 1;
        for (int i = 0; i <= loops; i++) {
            if (!Character.isLetter(stringToCheck.charAt(i)) &&
                    !Character.isDigit(stringToCheck.charAt(i))) {
                score++;
            }
        }
        return score;
    }

    /**
     * A convenience method for returning the count of numbers in a given String.
     *
     * @param stringToCheck
     * @return The numbers of digits in the String
     */
    protected int getNumberCount(String stringToCheck) {
        int score = 0;
        int loops = stringToCheck.length() - 1;
        for (int i = 0; i <= loops; i++) {
            if (Character.isDigit(stringToCheck.charAt(i))) {
                score++;
            }
        }
        return score;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        generateIndicatorColor();
        // Default to full width
        int indWidth = mIndicatorWidth;
        // If score, leave it as full - can cause it to become
        // less than full width in this calculation
        if (mCurrentScore < 20) indWidth = (mIndicatorWidth / 20) * mCurrentScore;
        // Draw indicator
        canvas.drawRect(
                getPaddingLeft(),
                getPaddingTop(),
                indWidth,
                mIndicatorHeight,
                mIndicatorPaint
        );
    }

    public void setPasswordHolder(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setPassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}