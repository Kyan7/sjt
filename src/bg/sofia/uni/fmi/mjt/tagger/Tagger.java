package bg.sofia.uni.fmi.mjt.tagger;

import bg.sofia.uni.fmi.mjt.tagger.containers.CaseInsensitiveString;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Comparator;

public class Tagger {

    HashMap<String, HashMap<CaseInsensitiveString, Integer>> data;
    final String openingCountryTagFrag1 = "<city country=\"";
    final String openingCountryTagFrag2 = "\">";
    final String closingCountryTag = "</city>";

    public Tagger(Reader citiesReader) throws IOException {
        try (Scanner sc = new Scanner(citiesReader)) {
            while (sc.hasNext()) {
                String[] tokens = sc.nextLine().split(",");
                String city = tokens[0];
                String country = tokens[1];
                data.putIfAbsent(country.toString(), new HashMap<>());
                data.get(country.toString()).put(new CaseInsensitiveString(city.toString()), 0);
            }
        }
    }

    public void tagCities(Reader text, Writer output) throws IOException {
        for (String countryKey : data.keySet()) {
            HashMap<CaseInsensitiveString, Integer> countryData = data.get(countryKey);
            countryData.replaceAll((k, v) -> 0);
        }
        try (Scanner sc = new Scanner(text)) {
            while (sc.hasNext()) {
                String line = sc.nextLine();
                String uppercaseLine = line.toUpperCase();
                for (String countryKey : data.keySet()) {
                    HashMap<CaseInsensitiveString, Integer> countryData = data.get(countryKey);
                    for (CaseInsensitiveString cityKey : countryData.keySet()) {
                        int lastOpeningTagStartIndex = 0;
                        int lastTempEndIndex = 0;
                        int extraIndex = -1;
                        while (lastOpeningTagStartIndex != -1) {
                            int openingTagStartIndex = uppercaseLine.indexOf(cityKey.getString().toUpperCase(), lastTempEndIndex);
                            if (openingTagStartIndex != -1) {
                                int closingTagStartIndex = openingTagStartIndex + cityKey.getString().toUpperCase().length();
                                if (!Character.isAlphabetic(uppercaseLine.charAt(closingTagStartIndex))) {
                                    String openingTag = openingCountryTagFrag1 + countryKey + openingCountryTagFrag2;

                                    String temp = line.substring(0, openingTagStartIndex) +
                                            openingTag +
                                            line.substring(openingTagStartIndex, closingTagStartIndex) +
                                            closingCountryTag;

                                    String uppercaseTemp = uppercaseLine.substring(0, openingTagStartIndex) +
                                            openingTag +
                                            uppercaseLine.substring(openingTagStartIndex, closingTagStartIndex) +
                                            closingCountryTag;

                                    line = temp +
                                            line.substring(closingTagStartIndex);

                                    uppercaseLine = uppercaseTemp +
                                            uppercaseLine.substring(closingTagStartIndex);

                                    int count = countryData.get(cityKey);
                                    countryData.put(cityKey, ++count);
                                    extraIndex = temp.length();
                                } else {
                                    extraIndex = -1;
                                }
                                if (extraIndex != -1) {
                                    lastTempEndIndex = extraIndex;
                                } else {
                                    lastTempEndIndex = line.length();
                                }
                                lastOpeningTagStartIndex = openingTagStartIndex;
                            } else {
                                lastOpeningTagStartIndex = -1;
                            }
                        }
                    }
                }
                output.append(line);
            }
        }
    }

    public Collection<String> getNMostTaggedCities(int n) {
        Map<String, Integer> taggedCities = new HashMap<>();
        for (String countryKey : data.keySet()) {
            HashMap<CaseInsensitiveString, Integer> countryData = data.get(countryKey);
            for (CaseInsensitiveString cityKey : countryData.keySet()) {
                int tagCount = countryData.get(cityKey);
                if (tagCount > 0) {
                    taggedCities.put(cityKey.getString(), tagCount);
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
            HashMap<CaseInsensitiveString, Integer> countryData = data.get(countryKey);
            for (CaseInsensitiveString cityKey : countryData.keySet()) {
                int tagCount = countryData.get(cityKey);
                if (tagCount > 0) {
                    taggedCities.put(cityKey.getString(), tagCount);
                }
            }
        }

        return new ArrayList<>(taggedCities.keySet());
    }

    public long getAllTagsCount() {
        long allTagsCount = 0;
        for (String countryKey : data.keySet()) {
            HashMap<CaseInsensitiveString, Integer> countryData = data.get(countryKey);
            for (CaseInsensitiveString cityKey : countryData.keySet()) {
                int tagCount = countryData.get(cityKey);
                allTagsCount += tagCount;
            }
        }
        return allTagsCount;
    }
}
