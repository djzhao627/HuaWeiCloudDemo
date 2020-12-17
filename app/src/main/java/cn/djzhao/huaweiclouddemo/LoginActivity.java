package cn.djzhao.huaweiclouddemo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, View.OnFocusChangeListener, OnKeyboardVisibilityListener {

    private RelativeLayout rootView;
    private TextView topTitleTV;
    private TextView mainTitleTV;
    private TextView logWithIAMTV;
    private TextView accountHintTV;
    private TextView passwordHintTV;
    private TextView confirmBtn;
    private ImageView topBackBtn;
    private ImageView clearAccountTextIV;
    private ImageView clearPassTextIV;
    private ImageView changeVisibleIV;
    private EditText accountET;
    private EditText passwordET;

    private static final String TAG = "LoginActivity";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initEvent();
        initData();
    }

    private void initView() {
        rootView = findViewById(R.id.login_root_view_rl);
        topTitleTV = findViewById(R.id.login_top_title_tv);
        mainTitleTV = findViewById(R.id.login_title_tv);
        logWithIAMTV = findViewById(R.id.change_login_method_tv);
        accountHintTV = findViewById(R.id.account_input_top_hint_tv);
        passwordHintTV = findViewById(R.id.password_input_top_hint_tv);
        topBackBtn = findViewById(R.id.login_top_back_iv);
        clearAccountTextIV = findViewById(R.id.clear_account_text_iv);
        clearPassTextIV = findViewById(R.id.clear_pass_text_iv);
        changeVisibleIV = findViewById(R.id.password_visible_iv);
        accountET = findViewById(R.id.account_input_et);
        passwordET = findViewById(R.id.password_input_et);
        confirmBtn = findViewById(R.id.do_login_btn);
    }

    private void initEvent() {
        rootView.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        topBackBtn.setOnClickListener(this);
        clearAccountTextIV.setOnClickListener(this);
        clearPassTextIV.setOnClickListener(this);
        changeVisibleIV.setOnClickListener(this);
        accountET.addTextChangedListener(this);
        passwordET.addTextChangedListener(this);
        accountET.setOnFocusChangeListener(this);
        passwordET.setOnFocusChangeListener(this);
        setKeyboardVisibilityListener(this);
    }

    private void initData() {

    }

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    private void changeView() {
        logWithIAMTV.setVisibility(View.GONE);
        showOrHideClearBtn();
    }

    private void hideKeyBoardAndShowView() {
        InputMethodManager manager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        if (manager != null) {
            manager.hideSoftInputFromWindow(accountET.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        topTitleTV.setVisibility(View.GONE);
        mainTitleTV.setVisibility(View.VISIBLE);
        logWithIAMTV.setVisibility(View.VISIBLE);
        clearAccountTextIV.setVisibility(View.GONE);
        clearPassTextIV.setVisibility(View.GONE);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Log.d(TAG, "HARDKEYBOARDHIDDEN_NO");
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            clearFocus();
            Log.d(TAG, "HARDKEYBOARDHIDDEN_YES");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_root_view_rl) {
            clearFocus();
        } else if (id == R.id.do_login_btn) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.clear_pass_text_iv) {
            passwordET.setText("");
            clearPassTextIV.setVisibility(View.GONE);
        } else if (id == R.id.login_top_back_iv) {
            finish();
        } else if (id == R.id.clear_account_text_iv) {
            accountET.setText("");
            clearAccountTextIV.setVisibility(View.GONE);
        } else if (id == R.id.password_visible_iv) {
            if (passwordET.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                changeVisibleIV.setImageResource(R.drawable.ic_eye_open);
            } else {
                passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                changeVisibleIV.setImageResource(R.drawable.ic_eye_close);
            }
        }
    }

    private void clearFocus() {
        accountET.clearFocus();
        passwordET.clearFocus();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        validConfirmBtn();
        showOrHideHint();
        showOrHideClearBtn();
    }

    private void showOrHideClearBtn() {
        if (accountET.isFocused() && accountET.getText().toString().length() > 0) {
            clearAccountTextIV.setVisibility(View.VISIBLE);
        } else {
            clearAccountTextIV.setVisibility(View.GONE);
        }
        if (passwordET.isFocused() && passwordET.getText().toString().length() > 0) {
            clearPassTextIV.setVisibility(View.VISIBLE);
        } else {
            clearPassTextIV.setVisibility(View.GONE);
        }
    }

    private void showOrHideHint() {
        if (accountET.getText().toString().length() == 1 && accountHintTV.getVisibility() == View.INVISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_down_up_show);
            accountHintTV.setAnimation(animation);
            accountHintTV.setVisibility(View.VISIBLE);
            accountHintTV.startAnimation(animation);
        } else if (accountET.getText().toString().length() == 0 && accountHintTV.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_up_down_hide);
            accountHintTV.setAnimation(animation);
            accountHintTV.setVisibility(View.INVISIBLE);
            accountHintTV.startAnimation(animation);
        }
        if (passwordET.getText().toString().length() == 1 && passwordHintTV.getVisibility() == View.INVISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_down_up_show);
            passwordHintTV.setAnimation(animation);
            passwordHintTV.setVisibility(View.VISIBLE);
            passwordHintTV.startAnimation(animation);
        } else if (passwordET.getText().toString().length() == 0 && passwordHintTV.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_up_down_hide);
            passwordHintTV.setAnimation(animation);
            passwordHintTV.setVisibility(View.INVISIBLE);
            passwordHintTV.startAnimation(animation);
        }
    }

    private void validConfirmBtn() {
        confirmBtn.setEnabled(accountET.getText().toString().length() > 0 && passwordET.getText().toString().length() > 5);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            changeView();
        } else {
            hideKeyBoardAndShowView();
        }
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        if (!visible) {
            clearFocus();
        }
    }
}