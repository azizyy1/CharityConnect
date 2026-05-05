package com.charityconnect.service;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryLabelService {

    private final MessageSource messageSource;

    public String resolve(String category, Locale locale) {
        if (category == null || category.isBlank()) {
            return "";
        }

        String normalized = normalize(category);
        if (!normalized.isBlank()) {
            String key = "category." + normalized;
            String localized = messageSource.getMessage(key, null, null, locale);
            if (localized != null && !localized.isBlank()) {
                return localized;
            }
        }

        return humanize(category);
    }

    private String normalize(String category) {
        String normalized = category.trim().toLowerCase(Locale.ROOT);

        if (normalized.startsWith("category.")) {
            normalized = normalized.substring("category.".length());
        }

        if (normalized.endsWith("_en") || normalized.endsWith("_fr")) {
            normalized = normalized.substring(0, normalized.length() - 3);
        }

        normalized = normalized.replace('-', '_').replace(' ', '_');
        normalized = normalized.replaceAll("[^a-z0-9_]", "");

        return normalized;
    }

    private String humanize(String category) {
        String readable = category.trim();

        if (readable.toLowerCase(Locale.ROOT).startsWith("category.")) {
            readable = readable.substring("category.".length());
        }

        if (readable.endsWith("_EN") || readable.endsWith("_FR")) {
            readable = readable.substring(0, readable.length() - 3);
        }

        return Arrays.stream(readable.replace('_', ' ').replace('-', ' ').replace('.', ' ').split("\\s+"))
                .filter(word -> !word.isBlank())
                .map(this::capitalizeWord)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

    private String capitalizeWord(String word) {
        if (word == null || word.isBlank()) {
            return null;
        }
        return word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1).toLowerCase(Locale.ROOT);
    }
}