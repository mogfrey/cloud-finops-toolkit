package com.mogfrey.payoneergoal;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import android.content.Context;

public class MainActivity extends Activity {

    private final int BG = Color.rgb(18,18,18);
    private final int CARD = Color.rgb(26,26,26);
    private final int TEXT = Color.rgb(250,250,250);
    private final int MUTED = Color.rgb(176,176,176);
    private final int BORDER = Color.rgb(70,70,70);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(Color.rgb(12,12,12));

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(BG);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setPadding(0,0,0,dp(88));

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(28), dp(20), dp(28), dp(28));
        page.setBackgroundColor(BG);

        page.addView(topHeader());
        addSpace(page, 38);
        page.addView(text("Total funds", 23, MUTED, false));
        addSpace(page, 14);

        TextView total = text("40,000.00 USD", 46, TEXT, false);
        total.setLetterSpacing(0.02f);
        page.addView(total);
        addSpace(page, 28);

        page.addView(currencyCard("🇺🇸", "USD balance", "40,000.00 USD"));
        addSpace(page, 14);
        page.addView(currencyCard("🇪🇺", "EUR balance", "0.00 EUR"));
        addSpace(page, 14);
        page.addView(currencyCard("🇬🇧", "GBP balance", "0.00 GBP"));

        addSpace(page, 34);
        page.addView(text("Latest transactions", 23, MUTED, false));
        addSpace(page, 22);

        page.addView(datePill("24 Jun 2026"));
        page.addView(transactionRow("Annual account fee", "-29.95 USD", "Completed"));
        addSpace(page, 22);

        page.addView(datePill("20 Jun 2025"));
        page.addView(transactionRow("Annual account fee", "-29.95 USD", "Completed"));
        addSpace(page, 22);

        page.addView(datePill("16 Jun 2024"));
        page.addView(transactionRow("Annual account fee", "-29.95 USD", "Completed"));

        addSpace(page, 30);
        TextView footer = text("Goal visualization • Static offline dashboard", 13, Color.rgb(105,105,105), false);
        footer.setGravity(Gravity.CENTER);
        page.addView(footer);

        scroll.addView(page, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));

        root.addView(scroll, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        View nav = bottomNav();
        FrameLayout.LayoutParams navLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, dp(86), Gravity.BOTTOM);
        root.addView(nav, navLp);

        setContentView(root);
    }

    private View topHeader() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        LogoView logo = new LogoView(this);
        row.addView(logo, new LinearLayout.LayoutParams(dp(48), dp(48)));
        addSpaceHorizontal(row, 14);

        TextView name = text("Geoffrey Mogendi", 30, TEXT, true);
        name.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(name, new LinearLayout.LayoutParams(0, dp(52), 1f));

        TextView profile = text("♙", 35, TEXT, false);
        profile.setGravity(Gravity.CENTER);
        row.addView(profile, new LinearLayout.LayoutParams(dp(48), dp(52)));

        FrameLayout bellWrap = new FrameLayout(this);
        TextView bell = text("♢", 35, TEXT, false);
        bell.setGravity(Gravity.CENTER);
        bellWrap.addView(bell, new FrameLayout.LayoutParams(dp(56), dp(56), Gravity.CENTER));

        TextView badge = text("25", 15, Color.WHITE, true);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(rounded(Color.rgb(239,92,54), dp(22), 0, 0));
        FrameLayout.LayoutParams badgeLp = new FrameLayout.LayoutParams(dp(40), dp(32), Gravity.TOP | Gravity.END);
        bellWrap.addView(badge, badgeLp);
        row.addView(bellWrap, new LinearLayout.LayoutParams(dp(66), dp(58)));
        return row;
    }

    private View currencyCard(String flag, String label, String amount) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(28), dp(28), dp(28), dp(28));
        card.setBackground(rounded(CARD, dp(28), dp(2), BORDER));

        card.addView(text(label, 25, TEXT, false));
        addSpace(card, 18);

        LinearLayout valueRow = new LinearLayout(this);
        valueRow.setOrientation(LinearLayout.HORIZONTAL);
        valueRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView flagView = text(flag, 37, TEXT, false);
        flagView.setGravity(Gravity.CENTER);
        valueRow.addView(flagView, new LinearLayout.LayoutParams(dp(62), dp(54)));
        addSpaceHorizontal(valueRow, 10);

        TextView value = text(amount, 37, TEXT, true);
        valueRow.addView(value, new LinearLayout.LayoutParams(-2,-2));
        card.addView(valueRow);
        card.setMinimumHeight(dp(148));
        return card;
    }

    private View datePill(String date) {
        TextView pill = text(date, 23, TEXT, true);
        pill.setGravity(Gravity.CENTER);
        pill.setPadding(dp(12), dp(8), dp(12), dp(8));
        pill.setBackground(rounded(Color.rgb(46,46,46), dp(10), 0, 0));
        pill.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(52)));
        return pill;
    }

    private View transactionRow(String label, String amount, String status) {
        LinearLayout wrap = new LinearLayout(this);
        wrap.setOrientation(LinearLayout.VERTICAL);
        wrap.setPadding(0, dp(20), 0, 0);

        LinearLayout line = new LinearLayout(this);
        line.setOrientation(LinearLayout.HORIZONTAL);
        line.setGravity(Gravity.CENTER_VERTICAL);

        TextView left = text(label, 24, TEXT, true);
        line.addView(left, new LinearLayout.LayoutParams(0, dp(42), 1f));

        TextView right = text(amount, 24, TEXT, true);
        right.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        line.addView(right, new LinearLayout.LayoutParams(dp(190), dp(42)));

        wrap.addView(line);
        wrap.addView(text(status, 22, MUTED, false));
        return wrap;
    }

    private View bottomNav() {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER_VERTICAL);
        nav.setPadding(dp(16), dp(4), dp(16), dp(4));
        nav.setBackgroundColor(Color.rgb(14,14,14));

        nav.addView(navItem("⌂", "Home", true), new LinearLayout.LayoutParams(0, -1, 1f));
        nav.addView(navItem("↔", "Activity", false), new LinearLayout.LayoutParams(0, -1, 1f));
        nav.addView(navItem("▣", "Actions", false), new LinearLayout.LayoutParams(0, -1, 1f));
        nav.addView(navItem("▤", "Cards", false), new LinearLayout.LayoutParams(0, -1, 1f));
        return nav;
    }

    private View navItem(String symbol, String label, boolean active) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);

        TextView icon = text(symbol, 30, active ? Color.rgb(64,207,204) : TEXT, false);
        icon.setGravity(Gravity.CENTER);
        item.addView(icon, new LinearLayout.LayoutParams(-1, dp(42)));

        TextView name = text(label, 16, TEXT, active);
        name.setGravity(Gravity.CENTER);
        item.addView(name, new LinearLayout.LayoutParams(-1, dp(28)));
        return item;
    }

    private TextView text(String value, int sp, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextSize(sp);
        v.setTextColor(color);
        v.setTypeface(android.graphics.Typeface.DEFAULT, bold ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        v.setIncludeFontPadding(false);
        return v;
    }

    private GradientDrawable rounded(int color, int radius, int stroke, int strokeColor) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(radius);
        if (stroke > 0) g.setStroke(stroke, strokeColor);
        return g;
    }

    private void addSpace(LinearLayout parent, int hDp) {
        Space s = new Space(this);
        parent.addView(s, new LinearLayout.LayoutParams(1, dp(hDp)));
    }

    private void addSpaceHorizontal(LinearLayout parent, int wDp) {
        Space s = new Space(this);
        parent.addView(s, new LinearLayout.LayoutParams(dp(wDp), 1));
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private static class LogoView extends View {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF oval = new RectF();

        public LogoView(Context c) { super(c); }

        @Override
        protected void onDraw(Canvas c) {
            super.onDraw(c);
            float w = getWidth(), h = getHeight();
            float pad = Math.min(w,h) * 0.12f;
            oval.set(pad,pad,w-pad,h-pad);
            SweepGradient shader = new SweepGradient(
                    w/2f, h/2f,
                    new int[]{
                            Color.rgb(249,139,32),
                            Color.rgb(234,65,74),
                            Color.rgb(138,61,193),
                            Color.rgb(49,130,221),
                            Color.rgb(68,198,155),
                            Color.rgb(249,139,32)
                    }, null);
            p.setShader(shader);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(4f, w * 0.09f));
            p.setStrokeCap(Paint.Cap.ROUND);
            c.drawOval(oval,p);
            p.setShader(null);
        }
    }
}
