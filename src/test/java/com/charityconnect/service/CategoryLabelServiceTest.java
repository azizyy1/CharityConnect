package com.charityconnect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;

class CategoryLabelServiceTest {

    private CategoryLabelService categoryLabelService;

    @BeforeEach
    void setUp() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("category.health", Locale.ENGLISH, "Health");
        messageSource.addMessage("category.youth", Locale.ENGLISH, "Youth");
        categoryLabelService = new CategoryLabelService(messageSource);
    }

    @Test
    void resolveShouldReturnLocalizedLabelForKnownCategory() {
        assertEquals("Health", categoryLabelService.resolve("Health", Locale.ENGLISH));
    }

    @Test
    void resolveShouldHandleLegacyCategoryTokens() {
        assertEquals("Youth", categoryLabelService.resolve("CATEGORY.YOUTH_EN", Locale.ENGLISH));
    }

    @Test
    void resolveShouldFallbackToHumanizedValueWhenTranslationMissing() {
        assertEquals("Animal Welfare", categoryLabelService.resolve("animal_welfare", Locale.ENGLISH));
    }
}