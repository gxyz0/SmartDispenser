package com.example.smartdispenser.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;

public class ViewUtil {
    public static void hideOneInputManager(Fragment fragment, View view){

        if (fragment.getView() == null) {
            return; // Fragment的视图尚未创建或已被销毁
        }
        InputMethodManager imm = (InputMethodManager) fragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
