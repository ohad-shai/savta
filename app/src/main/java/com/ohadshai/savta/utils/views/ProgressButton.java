package com.ohadshai.savta.utils.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.ohadshai.savta.R;

/**
 * Represents a compound view for a progress button.
 */
public class ProgressButton extends CardView {
    private boolean _isInProgress;
    private OnStartProgressListener _onStartProgressListener;
    private OnStopProgressListener _onStopProgressListener;
    private OnClickListener _onClickListener;
    private LinearLayout _linearLayout;
    private ProgressBar _progress;
    private TextView _txt;
    private boolean _isEnabled;

    //region Constructors

    public ProgressButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton, 0, 0);
        this.init(attributes);
        attributes.recycle();
    }

    public ProgressButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton, defStyleAttr, 0);
        this.init(attributes);
        attributes.recycle();
    }

    //endregion

    //region Public API

    /**
     * Starts to show the progress indicator in the button.
     *
     * @implNote WARNING: Not safe to call directly, let this class to handle the progress interaction, just set a listener to "onStartProgress".
     */
    public void startProgress() {
        if (!_isInProgress) {
            _isInProgress = true;
            Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            _progress.setAnimation(fadeIn);
            _progress.setVisibility(View.VISIBLE);
            _txt.setAnimation(fadeIn);
            _txt.setText(_txt.getText().toString().concat("..."));

            if (_onStartProgressListener != null) {
                _onStartProgressListener.onStartProgress(this);
            }
        }
    }

    /**
     * Stops showing the progress indicator in the button.
     */
    public void stopProgress() {
        if (_isInProgress) {
            _progress.setVisibility(View.GONE);
            _txt.setText(_txt.getText().toString().replace("...", ""));
            _isInProgress = false;

            if (_onStopProgressListener != null) {
                _onStopProgressListener.onStopProgress(this);
            }
        }
    }

    /**
     * Checks whether the progress is showing or not.
     *
     * @return Returns true if the progress is showing, otherwise false.
     */
    public boolean isInProgress() {
        return _isInProgress;
    }

    /**
     * Sets a listener for when the progress has been started, by a call to the .startProgress() function.
     *
     * @param listener The listener to set.
     */
    public void setOnStartProgressListener(OnStartProgressListener listener) {
        _onStartProgressListener = listener;
    }

    /**
     * Sets a listener for when the progress has been stopped, by a call to the .stopProgress() function.
     *
     * @param listener The listener to set.
     */
    public void setOnStopProgressListener(OnStopProgressListener listener) {
        _onStopProgressListener = listener;
    }

    /**
     * Sets a listener for when the button is clicked on, regardless of whether it is enabled or disabled.
     *
     * @param listener The listener to set.
     */
    public void setOnClickListener(OnClickListener listener) {
        _onClickListener = listener;
    }

    /**
     * Do not use this onClick listener since it is being used internally, use the other onClick listener.
     */
    public void setOnClickListener(@Nullable View.OnClickListener l) {
        throw new UnsupportedOperationException("Do not use this onClick listener since it is being used internally, use the other onClick listener.");
    }

    /**
     * Enables or disables the progress button.
     *
     * @param enabled An indicator indicating whether to enable or disable the progress button.
     */
    public void setEnabled(boolean enabled) {
        if (enabled) {
            super.setAlpha(1f);
        } else {
            super.setAlpha(0.5f);
        }
        _linearLayout.setEnabled(enabled);
        _isEnabled = enabled;
    }

    /**
     * Checks whether the progress button is enabled or not.
     *
     * @return Returns true if the progress button is enabled, otherwise false.
     */
    public boolean isEnabled() {
        return _isEnabled;
    }

    /**
     * Checks if can start to progress, means the progress button is currently enabled and not in progress.
     *
     * @return Returns true if the progress button can start to progress, otherwise false.
     */
    public boolean checkCanStartProgress() {
        return (_isEnabled && !_isInProgress);
    }

    //endregion

    //region Private Methods

    /**
     * Initializes all the controls.
     */
    private void init(TypedArray attributes) {
        inflate(getContext(), R.layout.view_progress_button, this);

        _isInProgress = false;
        _onStartProgressListener = null;

        this.setBackgroundResource(attributes.getResourceId(R.styleable.ProgressButton_backgroundCustom, R.color.white));

        _linearLayout = findViewById(R.id.view_progress_button_linearLayout);

        _progress = findViewById(R.id.view_progress_button_progressBar);
        _progress.setVisibility(GONE);

        _txt = findViewById(R.id.view_progress_button_txt);
        _txt.setText(attributes.getString(R.styleable.ProgressButton_text));

        ProgressButton thisBtn = this;
        super.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_onClickListener != null) {
                    _onClickListener.onClick(thisBtn);
                }
            }
        });

        this.setEnabled(attributes.getBoolean(R.styleable.ProgressButton_enabled, true));
    }

    //endregion

    //region Inner Classes

    /**
     * Represents a listener for when the button is clicked on and currently not in progress, in order to start a new progress.
     */
    public interface OnStartProgressListener {
        void onStartProgress(View view);
    }

    /**
     * Represents a listener for when the progress has been stopped, by a call to the .stopProgress() function.
     */
    public interface OnStopProgressListener {
        void onStopProgress(View view);
    }

    /**
     * Represents a listener for when the button is clicked on, regardless of whether it is enabled or disabled.
     */
    public interface OnClickListener {
        void onClick(ProgressButton progressButton);
    }

    //endregion

}
