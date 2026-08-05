package cs;

import java.util.List;

/**
 * Contains the static registry of all available cards in the game.
 */
public class CardData {

    public static final List<Card> CARDS = List.of(
        // === Biosphere Category ===
        new Card("Kelp Forest", "biosphere", "images/biosphere/kelp_forest.jpg", 40),
        new Card("Mangrove Restoration", "biosphere", "images/biosphere/mangrove_restoration.jpg", 50),
        new Card("Old Growth Protection", "biosphere", "images/biosphere/old_growth_protection.jpg", 65),
        new Card("Peat Bog Revival", "biosphere", "images/biosphere/peat_bog_revival.jpg", 45),
        new Card("Soil Microbiome", "biosphere", "images/biosphere/soil_microbiome.jpg", 30),

        // === Renewable Category ===
        new Card("Geothermal Tap", "renewable", "images/renewable/geothermal_tap.jpg", 55),
        new Card("Offshore Wind", "renewable", "images/renewable/offshore_wind.jpg", 70),
        new Card("Solar", "renewable", "images/renewable/solar.jpg", 35),
        new Card("SSD", "renewable", "images/renewable/ssd.jpg", 80),
        new Card("Tidal Generator", "renewable", "images/renewable/tidal_generator.jpg", 60),

        // === Green Tech Category ===
        new Card("Algae Biofuel", "green_tech", "images/green_tech/algae_biofuel.jpg", 55),
        new Card("Carbon Cured Concrete", "green_tech", "images/green_tech/carbon_cured_concrete.jpg", 60),
        new Card("Direct Air Capture", "green_tech", "images/green_tech/direct_air_capture.jpg", 85),
        new Card("Green Hydrogen", "green_tech", "images/green_tech/green_hydrogen.jpg", 70),
        new Card("Smart Grid AI", "green_tech", "images/green_tech/smart_grid_ai.jpg", 50),

        // === Policy & Society Category ===
        new Card("Carbon Dividend", "policy_and_society", "images/policy_and_society/carbon_dividend.jpg", 75),
        new Card("Climate Treaty", "policy_and_society", "images/policy_and_society/climate_treaty.jpg", 90),
        new Card("Emission Cap", "policy_and_society", "images/policy_and_society/emission_cap.jpg", 50),
        new Card("Green Urbanism", "policy_and_society", "images/policy_and_society/green_urbanism.jpg", 45),
        new Card("Public Transit", "policy_and_society", "images/policy_and_society/public_transit.jpg", 60)
    );

    /**
     * Gets a list of cards filtered by category.
     */
    public static List<Card> getByCategory(String category) {
        return CARDS.stream()
                .filter(card -> card.category().equalsIgnoreCase(category))
                .toList();
    }
}
