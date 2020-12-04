package bg.sofia.uni.fmi.mjt.tagger;

import java.io.*;
import java.util.*;

public class Main {
    static final String OPENING_COUNTRY_TAG_FRAG_1 = "<city country=\"";
    static final String OPENING_COUNTRY_TAG_FRAG_2 = "\">";
    static final String CLOSING_COUNTRY_TAG = "</city>";

    public static void main(String[] args) throws IOException {
        BufferedReader citiesReader = new BufferedReader(new FileReader("C:\\Users\\Kaloyan\\Desktop\\world-cities.csv"));
        HashMap<String, HashMap<String, Integer>> data = new HashMap<>();
        StringBuilder city = new StringBuilder();
        StringBuilder country = new StringBuilder();

        int symbolAsNumber;
        char symbolAsChar;
        boolean makingCity = true;
        while ((symbolAsNumber = citiesReader.read()) != -1) {
            symbolAsChar = (char) symbolAsNumber;
            if (makingCity) {
                if (symbolAsChar != ',') {
                    city.append(symbolAsChar);
                } else {
                    makingCity = false;
                }
            } else {
                if (symbolAsChar != '\n') {
                    country.append(symbolAsChar);
                } else {
                    makingCity = true;
                    data.putIfAbsent(country.toString(), new HashMap<>());
                    data.get(country.toString()).put(city.toString(), 0);
                    country = new StringBuilder();
                    city = new StringBuilder();
                }
            }
        }

        BufferedReader text = new BufferedReader(new FileReader("C:\\Users\\Kaloyan\\Desktop\\test.txt"));

        StringBuilder line = new StringBuilder();
        while ((symbolAsNumber = text.read()) != -1) {
            symbolAsChar = (char) symbolAsNumber;
            if (symbolAsChar != '\n') {
                line.append(symbolAsChar);
            } else {
                String str = line.toString();
                for (String countryKey : data.keySet()) {
                    HashMap<String, Integer> countryData = data.get(countryKey);
                    for (String cityKey : countryData.keySet()) {
                        int lastOpeningTagStartIndex = 0;
                        int lastTempEndIndex = 0;
                        while (lastOpeningTagStartIndex != -1) {
                            int openingTagStartIndex = str.indexOf(cityKey, lastTempEndIndex);
                            if (openingTagStartIndex != -1) {
                                int closingTagStartIndex = openingTagStartIndex + cityKey.length();
                                String openingTag = OPENING_COUNTRY_TAG_FRAG_1 + countryKey + OPENING_COUNTRY_TAG_FRAG_2;

                                String temp = str.substring(0, openingTagStartIndex) +
                                        openingTag +
                                        str.substring(openingTagStartIndex, closingTagStartIndex) +
                                        CLOSING_COUNTRY_TAG;

                                str = temp +
                                        str.substring(closingTagStartIndex);

                                int count = countryData.get(cityKey);
                                countryData.put(cityKey, ++count);
                                lastTempEndIndex = temp.length();
                            }
                            lastOpeningTagStartIndex = openingTagStartIndex;
                        }
                    }
                }
                System.out.println(str);
                line = new StringBuilder();
            }
        }

        String str = line.toString();
        for (String countryKey : data.keySet()) {
            HashMap<String, Integer> countryData = data.get(countryKey);
            for (String cityKey : countryData.keySet()) {
                int lastOpeningTagStartIndex = 0;
                int lastTempEndIndex = 0;
                while (lastOpeningTagStartIndex != -1) {
                    int openingTagStartIndex = str.indexOf(cityKey, lastTempEndIndex);
                    if (openingTagStartIndex != -1) {
                        int closingTagStartIndex = openingTagStartIndex + cityKey.length();
                        String openingTag = OPENING_COUNTRY_TAG_FRAG_1 + countryKey + OPENING_COUNTRY_TAG_FRAG_2;

                        String temp = str.substring(0, openingTagStartIndex) +
                                openingTag +
                                str.substring(openingTagStartIndex, closingTagStartIndex) +
                                CLOSING_COUNTRY_TAG;

                        str = temp +
                                str.substring(closingTagStartIndex);

                        int count = countryData.get(cityKey);
                        countryData.put(cityKey, ++count);
                        lastTempEndIndex = temp.length();
                    }
                    lastOpeningTagStartIndex = openingTagStartIndex;
                }
            }
        }
        System.out.println(str);

        Map<String, Integer> taggedCities = new HashMap<>();
        for (String countryKey : data.keySet()) {
            HashMap<String, Integer> countryData = data.get(countryKey);
            for (String cityKey : countryData.keySet()) {
                int tagCount = countryData.get(cityKey);
                if (tagCount > 0) {
                    taggedCities.put(cityKey, tagCount);
                }
            }
        }

        List<String> result = new ArrayList<>(taggedCities.keySet());
        result.sort(Comparator.comparing(taggedCities::get).reversed());

        result = 10 <= result.size() ? result.subList(0, 10) : result.subList(0, result.size());
        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i));
        }
    }
}
