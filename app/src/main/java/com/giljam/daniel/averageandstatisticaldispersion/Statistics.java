package com.giljam.daniel.averageandstatisticaldispersion;

import java.util.List;
import java.util.Collections;

class Statistics {
    static float getAverage(List<Float> numbersList) {
        int listLength = numbersList.size();
        float sum = 0;
        for (float number : numbersList) {
            sum += number;
        }
        return sum / listLength;
    }
    static float getMedian(List<Float> numbersList) {
        List<Float> sortedList = numbersList;
        Collections.sort(sortedList);
        int listLength = numbersList.size();
        switch (listLength % 2) {
            case 0:
                return (numbersList.get(listLength / 2 - 1) + numbersList.get(listLength / 2)) / 2;
            default:
                return numbersList.get(listLength / 2);
        }
    }
    static double getStandardDeviation(List<Float> numbersList) {
        float diffSum = 0;
        float average = getAverage(numbersList);
        int listLength = numbersList.size();
        for (float number : numbersList) {
            diffSum += Math.pow((number - average), 2);
        }
        return Math.sqrt(diffSum/listLength);
    }
}