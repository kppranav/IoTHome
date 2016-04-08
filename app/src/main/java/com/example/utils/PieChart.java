package com.example.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by uvionics on 20/2/16.
 */
public class PieChart {
    com.github.mikephil.charting.charts.PieChart mChart;
    Context context;
    Typeface tf;
    String category;
    float val;

    public PieChart(Context context, String category, float val) {
        this.context = context;
        this.category = category;
        this.val = val;
    }


   public com.github.mikephil.charting.charts.PieChart setupGraph( com.github.mikephil.charting.charts.PieChart mChart) {

       mChart.setUsePercentValues(true);
       mChart.setDescription("");
       mChart.setExtraOffsets(5, 10, 5, 5);

       mChart.setDragDecelerationFrictionCoef(0.95f);

     /*  tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

       mChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
       mChart.setCenterText(generateCenterSpannableText());*/


       mChart.setDrawHoleEnabled(true);
       mChart.setHoleColorTransparent(true);

       mChart.setTransparentCircleColor(Color.WHITE);
       mChart.setTransparentCircleAlpha(110);

       mChart.setHoleRadius(58f);
       mChart.setTransparentCircleRadius(61f);

       mChart.setDrawCenterText(true);

       mChart.setRotationAngle(0);

       mChart.setRotationEnabled(true);
       mChart.setHighlightPerTapEnabled(true);

       mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

       setData(category, val);

       Legend l = mChart.getLegend();
       l.setEnabled(false);

       return mChart;
   }

    private void setData(String category, float val) {
        ArrayList<Entry> yVal = new ArrayList<Entry>();
        yVal.add(new Entry(val, 0));
        yVal.add(new Entry(100-val, 1));

        ArrayList<String> xVal = new ArrayList<String>();
        xVal.add(category);
        xVal.add("others");

        PieDataSet dataSet = new PieDataSet(yVal, "Power Consumption");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(xVal, dataSet);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(tf);
        mChart.setData(data);

        mChart.highlightValues(null);

        mChart.invalidate();

    }
}
