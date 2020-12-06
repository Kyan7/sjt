package bg.sofia.uni.fmi.mjt.tagger;

import bg.sofia.uni.fmi.mjt.tagger.containers.CaseInsensitiveString;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    static final String OPENING_COUNTRY_TAG_FRAG_1 = "<city country=\"";
    static final String OPENING_COUNTRY_TAG_FRAG_2 = "\">";
    static final String CLOSING_COUNTRY_TAG = "</city>";

    public static void main(String[] args) throws IOException {
        BufferedReader citiesReader = new BufferedReader(new FileReader("C:\\Users\\Kaloyan\\Desktop\\world-cities.csv"));
        HashMap<String, HashMap<CaseInsensitiveString, Integer>> data = new HashMap<>();
        try (Scanner sc = new Scanner(citiesReader)) {
            while (sc.hasNext()) {
                String[] tokens = sc.nextLine().split(",");
                String city = tokens[0];
                String country = tokens[1];
                data.putIfAbsent(country.toString(), new HashMap<>());
                data.get(country.toString()).put(new CaseInsensitiveString(city.toString()), 0);
            }
        }

        BufferedReader text = new BufferedReader(new FileReader("C:\\Users\\Kaloyan\\Desktop\\test.txt"));

        StringBuilder output = new StringBuilder();
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
                                    String openingTag = OPENING_COUNTRY_TAG_FRAG_1 + countryKey + OPENING_COUNTRY_TAG_FRAG_2;

                                    String temp = line.substring(0, openingTagStartIndex) +
                                            openingTag +
                                            line.substring(openingTagStartIndex, closingTagStartIndex) +
                                            CLOSING_COUNTRY_TAG;

                                    String uppercaseTemp = uppercaseLine.substring(0, openingTagStartIndex) +
                                            openingTag +
                                            uppercaseLine.substring(openingTagStartIndex, closingTagStartIndex) +
                                            CLOSING_COUNTRY_TAG;

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
}
