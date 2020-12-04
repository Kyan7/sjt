package bg.sofia.uni.fmi.mjt.tagger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

public class Tagger {

    HashMap<String, HashMap<String, Integer>> data;
    final String openingCountryTagFrag1 = "<city country=\"";
    final String openingCountryTagFrag2 = "\">";
    final String closingCountryTag = "</city>";

    public Tagger(Reader citiesReader) throws IOException {
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
    }

    public void tagCities(Reader text, Writer output) throws IOException {
        for (String countryKey : data.keySet()) {
            HashMap<String, Integer> countryData = data.get(countryKey);
            countryData.replaceAll((k, v) -> 0);
        }
        StringBuilder line = new StringBuilder();
        int symbolAsNumber;
        char symbolAsChar;
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
                                String openingTag = openingCountryTagFrag1 + countryKey + openingCountryTagFrag1;

                                String temp = str.substring(0, openingTagStartIndex) +
                                        openingTag +
                                        str.substring(openingTagStartIndex, closingTagStartIndex) +
                                        closingCountryTag;

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
                output.append(str);
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
                        String openingTag = openingCountryTagFrag1 + countryKey + openingCountryTagFrag2;

                        String temp = str.substring(0, openingTagStartIndex) +
                                openingTag +
                                str.substring(openingTagStartIndex, closingTagStartIndex) +
                                closingCountryTag;

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
        output.append(str);
    }

    public Collection<String> getNMostTaggedCities(int n) {
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

        return n <= result.size() ? result.subList(0, n) : result.subList(0, result.size());

    }

    public Collection<String> getAllTaggedCities() {
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

        return new ArrayList<>(taggedCities.keySet());
    }

    public long getAllTagsCount() {
        long allTagsCount = 0;
        for (String countryKey : data.keySet()) {
            HashMap<String, Integer> countryData = data.get(countryKey);
            for (String cityKey : countryData.keySet()) {
                int tagCount = countryData.get(cityKey);
                allTagsCount += tagCount;
            }
        }
        return allTagsCount;
    }

}