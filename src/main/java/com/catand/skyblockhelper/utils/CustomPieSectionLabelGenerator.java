package com.catand.skyblockhelper.utils;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.data.general.PieDataset;

import java.text.DecimalFormat;

public class CustomPieSectionLabelGenerator extends StandardPieSectionLabelGenerator {
    private double threshold;

    public CustomPieSectionLabelGenerator(double threshold) {
        super("{0}");
        this.threshold = threshold;
    }

    @Override
    public String generateSectionLabel(PieDataset dataset, Comparable key) {
        String result = null;
        if (dataset != null) {
            double value = dataset.getValue(key).doubleValue();
            double total = calculateTotal(dataset);
            if (value / total >= threshold) {
                result = super.generateSectionLabel(dataset, key);
            }
        }
        return result;
    }

    private double calculateTotal(PieDataset dataset) {
        double total = 0.0;
        for (int i = 0; i < dataset.getItemCount(); i++) {
            total += dataset.getValue(i).doubleValue();
        }
        return total;
    }
}