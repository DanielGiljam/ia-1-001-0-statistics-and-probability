package com.giljam.daniel.statisticsandprobability;

import java.util.ArrayList;
import java.util.Arrays;
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

    private List<Double> xList;
    private List<Double> yList;
    private int n;
    private double xAvg;
    private double yAvg;
    private double xStdSampDev;
    private double yStdSampDev;
    private double correlationCoefficient;
    private LinearFunction linearRegressionLine;
    
    Statistics(List<Double> numbersListUnsorted, List<Double> xList, List<Double> yList) {

        if (numbersListUnsorted == null) numbersList = new ArrayList<>();
        else numbersList = numbersListUnsorted;

        if (xList == null) this.xList = new ArrayList<>();
        else this.xList = xList;

        if (yList == null) this.yList = new ArrayList<>();
        else this.yList = yList;

        Collections.sort(numbersList);

        listLength = numbersList.size();

        halfOfListLength = listLength / 2;
    }
    
    static List<Object> helaRubbet(List<Double> numbersListUnsorted, List<Double> xList, List<Double> yList) {
        Statistics statistics = new Statistics(numbersListUnsorted, xList, yList);
        return statistics.helaRubbet();
    }

    static double[] forstaHalvanAvRubbet(List<Double> numbersListUnsorted) {
        Statistics statistics = new Statistics(numbersListUnsorted, null, null);
        return statistics.forstHalvanAvRubbet();
    }

    static List<Object> andraHalvanAvRubbet(List<Double> xList, List<Double> yList) {
        Statistics statistics = new Statistics(null, xList, yList);
        return statistics.andraHalvanAvRubbet();
    }
    
    private List<Object> helaRubbet() {
        double[] partZero;
        if (listLength == 0) partZero = null;
        else partZero = new double[]{getMinimum(),
                                     getMaximum(),
                                     getAverage(),
                                     getStandardDeviation(),
                                     getLowerQuartile(),
                                     getMedian(),
                                     getUpperQuartile(),
                                     getInterQuartileRange()};
        LinearFunction partOne;
        double partTwo;
        if (xList.size() < 2 || yList.size() < 2) {
            partOne = null;
            partTwo = NaN;
        } else {
            AnalyzeXYLists();
            partOne = getLinearRegressionLine();
            partTwo = getCorrelationCoefficient();
        }
        return new ArrayList<>(Arrays.asList(partZero, partOne, partTwo));
    }

    private double[] forstHalvanAvRubbet() {
        double[] forstaHalvan;
        if (listLength == 0) forstaHalvan = null;
        else forstaHalvan = new double[]{   getMinimum(),
                                            getMaximum(),
                                            getAverage(),
                                            getStandardDeviation(),
                                            getLowerQuartile(),
                                            getMedian(),
                                            getUpperQuartile(),
                                            getInterQuartileRange()};
        return forstaHalvan;
    }

    private List<Object> andraHalvanAvRubbet() {
        LinearFunction forstaHalvanAvAndraHalvan;
        double andraHalvanAvAndraHalvan;
        if (xList.size() < 2 || yList.size() < 2) {
            forstaHalvanAvAndraHalvan = null;
            andraHalvanAvAndraHalvan = NaN;
        } else {
            AnalyzeXYLists();
            forstaHalvanAvAndraHalvan = getLinearRegressionLine();
            andraHalvanAvAndraHalvan = getCorrelationCoefficient();
        }
        return new ArrayList<>(Arrays.asList(forstaHalvanAvAndraHalvan, andraHalvanAvAndraHalvan));
    }

    private void AnalyzeXYLists() {
        if (xList.size() > yList.size()) n = yList.size();
        else n = xList.size();
        xAvg = getAverage(xList);
        yAvg = getAverage(yList);
        getStandardSampleDeviation();
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

    static double getStandardSampleDeviation(List<Double> numbersList) {
        if (numbersList.isEmpty()) return NaN;
        double diffSum = 0;
        double average = getAverage(numbersList);
        int listLength = numbersList.size();
        for (double number : numbersList) {
            diffSum += Math.pow(number - average, 2);
        }
        return Math.sqrt(diffSum / (listLength - 1));
    }

    private void getStandardSampleDeviation() {
        double xDiffSum = 0;
        double yDiffSum = 0;
        for (int i = 0; i < n; i++) {
            xDiffSum += Math.pow(xList.get(i) - xAvg, 2);
            yDiffSum += Math.pow(yList.get(i) - yAvg, 2);
        }
        xStdSampDev = Math.sqrt(xDiffSum / (n - 1));
        yStdSampDev = Math.sqrt(yDiffSum / (n - 1));
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

    static double getCorrelationCoefficient(List<Double> xList, List<Double> yList) {
        double xAvg = getAverage(xList);
        double yAvg = getAverage(yList);
        double n;
        if (xList.size() > yList.size()) n = yList.size();
        else n = xList.size();
        double xDiffSum = 0;
        double yDiffSum = 0;
        for (int i = 0; i < n; i++) {
            xDiffSum += Math.pow(xList.get(i) - xAvg, 2);
            yDiffSum += Math.pow(yList.get(i) - yAvg, 2);
        }
        double xStdSampDev = Math.sqrt(xDiffSum / (n - 1));
        double yStdSampDev = Math.sqrt(yDiffSum / (n - 1));
        double firstFactor = 1 / (n - 1);
        double secondFactor = 0;
        for (int i = 0; i < n; i++)
            secondFactor += ((xList.get(i) - xAvg) / xStdSampDev) *
                            ((yList.get(i) - yAvg) / yStdSampDev);
        return firstFactor * secondFactor;
    }

    private double getCorrelationCoefficient() {
        double firstFactor = 1 / (double) (n - 1);
        double secondFactor = 0;
        for (int i = 0; i < n; i++)
            secondFactor += ((xList.get(i) - xAvg) / xStdSampDev) *
                            ((yList.get(i) - yAvg) / yStdSampDev);
        correlationCoefficient = firstFactor * secondFactor;
        return correlationCoefficient;
    }

    static LinearFunction getLinearRegressionLine(List<Double> xList, List<Double> yList) {
        double xAvg = Statistics.getAverage(xList);
        double yAvg = Statistics.getAverage(yList);
        double n;
        if (xList.size() > yList.size()) n = yList.size();
        else n = xList.size();
        double k1 = 0;
        double k2 = 0;
        for (int i = 0; i < n; i++) {
            k1 += (xList.get(i) - xAvg) * (yList.get(i) - yAvg);
            k2 += Math.pow(xList.get(i) - xAvg, 2);
        }
        double k = k1 / k2;
        double b = yAvg - k * xAvg;
        return new LinearFunction(k, b);
    }

    private LinearFunction getLinearRegressionLine() {
        double k1 = 0;
        double k2 = 0;
        for (int i = 0; i < n; i++) {
            k1 += (xList.get(i) - xAvg) * (yList.get(i) - yAvg);
            k2 += Math.pow(xList.get(i) - xAvg, 2);
        }
        double k = k1 / k2;
        double b = yAvg - k * xAvg;
        linearRegressionLine = new LinearFunction(k, b);
        return linearRegressionLine;
    }
}

class LinearFunction {

    private final double k;
    private final double b;

    LinearFunction(double k, double b) {
        this.k = k;
        this.b = b;
    }

    double getK() {
        return k;
    }

    double getB() {
        return b;
    }

    double getX(double y) {
        return (y - b) / k;
    }

    double getY(double x) {
        return k * x + b;
    }
}

