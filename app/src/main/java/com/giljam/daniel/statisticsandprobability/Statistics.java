package com.giljam.daniel.statisticsandprobability;

import java.util.List;
import java.util.Collections;

import static java.lang.Double.NaN;

class Statistics {
    
    private List<Double> numbersList;
    private int listLength;
    private int halfOfListLength;
    private double minimum;
    private double maximum;
    private double average;
    private double standardDeviation;
    private double lowerQuartile;
    private double median;
    private double upperQuartile;
    private double interQuartileRange;
    
    Statistics(List<Double> numbersListUnsorted) {
        numbersList = numbersListUnsorted;
        Collections.sort(numbersList);
        listLength = numbersList.size();
        halfOfListLength = listLength / 2;
    }
    
    static double[] helaRubbet(List<Double> numbersListUnsorted) {
        Statistics statistics = new Statistics(numbersListUnsorted);
        return statistics.helaRubbet();
    }
    
    private double[] helaRubbet() {
        if (listLength == 0) return null;
        return new double[] {   getMinimum(), 
                                getMaximum(), 
                                getAverage(), 
                                getStandardDeviation(), 
                                getLowerQuartile(), 
                                getMedian(), 
                                getUpperQuartile(),
                                getInterQuartileRange()};
    }

    static double getMinimum(List<Double> numbersList) {
        if (numbersList.isEmpty()) return NaN;
        List<Double> listSorted = numbersList;
        Collections.sort(listSorted);
        return listSorted.get(0);
    }

    private double getMinimum() {
        minimum = numbersList.get(0);
        return minimum;
    }

    static double getMaximum(List<Double> numbersList) {
        if (numbersList.isEmpty()) return NaN;
        List<Double> listSorted = numbersList;
        Collections.sort(listSorted);
        return listSorted.get(listSorted.size() - 1);
    }

    private double getMaximum() {
        maximum = numbersList.get(listLength - 1);
        return maximum;
    }
    
    static double getAverage(List<Double> numbersList) {
        if (numbersList.isEmpty()) return NaN;
        int listLength = numbersList.size();
        double sum = 0;
        for (double number : numbersList) {
            sum += number;
        }
        return sum / listLength;
    }

    private double getAverage() {
        double sum = 0;
        for (double number : numbersList) {
            sum += number;
        }
        average = sum / listLength;
        return average;
    }

    static double getStandardDeviation(List<Double> numbersList) {
        if (numbersList.isEmpty()) return NaN;
        double diffSum = 0;
        double average = getAverage(numbersList);
        int listLength = numbersList.size();
        for (double number : numbersList) {
            diffSum += Math.pow(number - average, 2);
        }
        return Math.sqrt(diffSum/listLength);
    }

    private double getStandardDeviation() {
        double diffSum = 0;
        for (double number : numbersList) {
            diffSum += Math.pow(number - average, 2);
        }
        standardDeviation = Math.sqrt(diffSum / listLength);
        return standardDeviation;
    }

    static double getLowerQuartile(List<Double> numbersListUnsorted) {
        if (numbersListUnsorted.isEmpty()) return NaN;
        List<Double> numbersListSorted = numbersListUnsorted;
        Collections.sort(numbersListSorted);
        int listLength = numbersListSorted.size();
        int halfOfListLength = listLength / 2;
        switch (listLength % 2) {
            case 0:
                switch (halfOfListLength % 2) {
                    case 0:
                        return (numbersListSorted.get(halfOfListLength / 2 - 1) + numbersListSorted.get(halfOfListLength / 2)) / 2;
                    default:
                        return numbersListSorted.get(halfOfListLength / 2);
                }
            default:
                switch ((halfOfListLength + 1) % 2) {
                    case 0:
                        return (numbersListSorted.get((halfOfListLength + 1) / 2 - 1) + numbersListSorted.get((halfOfListLength + 1) / 2)) / 2;
                    default:
                        return numbersListSorted.get((halfOfListLength + 1) / 2);
                }
        }
    }

    private double getLowerQuartile() {
        if (listLength == 2) {
            lowerQuartile = numbersList.get(0);
            return lowerQuartile;
        }
        switch (listLength % 2) {
            case 0:
                switch (halfOfListLength % 2) {
                    case 0:
                        lowerQuartile = (numbersList.get(halfOfListLength / 2 - 1) + numbersList.get(halfOfListLength / 2)) / 2;
                        break;
                    default:
                        lowerQuartile = numbersList.get(halfOfListLength / 2);
                        break;
                }
                break;
            default:
                switch ((halfOfListLength + 1) % 2) {
                    case 0:
                        lowerQuartile = (numbersList.get((halfOfListLength + 1) / 2 - 1) + numbersList.get((halfOfListLength + 1) / 2)) / 2;
                        break;
                    default:
                        lowerQuartile = numbersList.get((halfOfListLength + 1) / 2);
                        break;
                }
                break;
        }
        return lowerQuartile;
    }
    
    static double getMedian(List<Double> numbersListUnsorted) {
        if (numbersListUnsorted.isEmpty()) return NaN;
        List<Double> numbersListSorted = numbersListUnsorted;
        Collections.sort(numbersListSorted);
        int listLength = numbersListSorted.size();
        switch (listLength % 2) {
            case 0:
                return (numbersListSorted.get(listLength / 2 - 1) + numbersListSorted.get(listLength / 2)) / 2;
            default:
                return numbersListSorted.get(listLength / 2);
        }
    }

    private double getMedian() {
        switch (listLength % 2) {
            case 0:
                median = (numbersList.get(listLength / 2 - 1) + numbersList.get(listLength / 2)) / 2;
                break;
            default:
                median = numbersList.get(listLength / 2);
                break;
        }
        return median;
    }

    static double getUpperQuartile(List<Double> numbersListUnsorted) {
        if (numbersListUnsorted.isEmpty()) return NaN;
        List<Double> numbersListSorted = numbersListUnsorted;
        Collections.sort(numbersListSorted);
        int listLength = numbersListSorted.size();
        int halfOfListLength = listLength / 2;
        switch (listLength % 2) {
            case 0:
                switch (halfOfListLength % 2) {
                    case 0:
                        return (numbersListSorted.get(halfOfListLength + halfOfListLength / 2 - 1) + numbersListSorted.get(halfOfListLength + halfOfListLength / 2)) / 2;
                    default:
                        return numbersListSorted.get(halfOfListLength / 2);
                }
            default:
                switch ((halfOfListLength + 1) % 2) {
                    case 0:
                        return (numbersListSorted.get(halfOfListLength + (halfOfListLength + 1) / 2 - 1) + numbersListSorted.get(halfOfListLength + (halfOfListLength + 1) / 2)) / 2;
                    default:
                        return numbersListSorted.get(halfOfListLength + (halfOfListLength + 1) / 2);
                }
        }
    }

    private double getUpperQuartile() {
        if (listLength == 2) {
            upperQuartile = numbersList.get(1);
            return upperQuartile;
        }
        switch (listLength % 2) {
            case 0:
                switch (halfOfListLength % 2) {
                    case 0:
                        upperQuartile = (numbersList.get(halfOfListLength + halfOfListLength / 2 - 1) + numbersList.get(halfOfListLength + halfOfListLength / 2)) / 2;
                        break;
                    default:
                        upperQuartile = numbersList.get(halfOfListLength + halfOfListLength / 2);
                        break;
                }
                break;
            default:
                switch ((halfOfListLength + 1) % 2) {
                    case 0:
                        upperQuartile = (numbersList.get(halfOfListLength + (halfOfListLength + 1) / 2 - 1) + numbersList.get(halfOfListLength + (halfOfListLength + 1) / 2)) / 2;
                        break;
                    default:
                        upperQuartile = numbersList.get(halfOfListLength + (halfOfListLength + 1) / 2);
                        break;
                }
                break;
        }
        return upperQuartile;
    }

    static double getInterQuartileRange(List<Double> numbersList) {
        if (numbersList.isEmpty()) return NaN;
        return getUpperQuartile(numbersList) - getLowerQuartile(numbersList);
    }

    private double getInterQuartileRange() {
        interQuartileRange = upperQuartile - lowerQuartile;
        return interQuartileRange;
    }
}