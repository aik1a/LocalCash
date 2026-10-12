package com.aik1a.localcash.presentation;

import android.view.View;

final class Pantalla {
    private Pantalla() { }
    static void aplicarMargenes(View root) {
        int horizontal = root.getPaddingLeft();
        int vertical = root.getPaddingTop();
        root.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(horizontal + insets.getSystemWindowInsetLeft(),
                    vertical + insets.getSystemWindowInsetTop(),
                    horizontal + insets.getSystemWindowInsetRight(),
                    vertical + insets.getSystemWindowInsetBottom());
            return insets;
        });
        root.requestApplyInsets();
    }
}
