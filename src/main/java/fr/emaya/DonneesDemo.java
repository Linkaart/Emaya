package fr.emaya;

import fr.emaya.model.Client;
import fr.emaya.model.Intervention;
import fr.emaya.model.Moto;
import fr.emaya.model.StatutIntervention;
import fr.emaya.model.TypeIntervention;
import fr.emaya.model.Utilitaire;
import fr.emaya.model.Voiture;
import fr.emaya.service.Atelier;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Jeu de données fictif chargé au démarrage pour pouvoir tester l'application tout de suite. */
final class DonneesDemo {

    private DonneesDemo() {
    }

    static void charger(Atelier atelier) {
        LocalDate aujourdhui = LocalDate.now();

        Client dupont = atelier.ajouterClient("Jean Dupont", "06 12 34 56 78");
        Client martin = atelier.ajouterClient("Sophie Martin", "07 98 76 54 32");
        Client livraisons = atelier.ajouterClient("Livraisons Express", "03 87 00 00 00");

        atelier.ajouterVehicule(dupont.getId(), new Voiture("AB-123-CD", "Peugeot", "208", 2019, 54_000, 5));
        atelier.ajouterVehicule(martin.getId(), new Moto("EF-456-GH", "Yamaha", "MT-07", 2021, 12_000, 689));
        atelier.ajouterVehicule(martin.getId(), new Voiture("MN-012-OP", "Renault", "Clio", 2016, 98_000, 5));
        atelier.ajouterVehicule(livraisons.getId(), new Utilitaire("IJ-789-KL", "Renault", "Master", 2018, 143_000, 1_400));

        terminer(atelier, atelier.planifierIntervention("AB-123-CD", TypeIntervention.VIDANGE,
                aujourdhui.minusDays(30), new BigDecimal("45.00")));
        terminer(atelier, atelier.planifierIntervention("EF-456-GH", TypeIntervention.FREINS,
                aujourdhui.minusDays(10), new BigDecimal("120.00")));

        Intervention diagnostic = atelier.planifierIntervention("MN-012-OP", TypeIntervention.DIAGNOSTIC,
                aujourdhui, BigDecimal.ZERO);
        atelier.changerStatut(diagnostic.getId(), StatutIntervention.EN_COURS);

        atelier.planifierIntervention("IJ-789-KL", TypeIntervention.DISTRIBUTION, aujourdhui, new BigDecimal("250.00"));
        atelier.planifierIntervention("AB-123-CD", TypeIntervention.PNEUS, aujourdhui.plusDays(3), new BigDecimal("320.00"));
    }

    private static void terminer(Atelier atelier, Intervention intervention) {
        atelier.changerStatut(intervention.getId(), StatutIntervention.EN_COURS);
        atelier.changerStatut(intervention.getId(), StatutIntervention.TERMINEE);
    }
}
