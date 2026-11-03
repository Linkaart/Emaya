package fr.emaya;

import fr.emaya.exception.ElementIntrouvableException;
import fr.emaya.exception.RegleMetierException;
import fr.emaya.model.Client;
import fr.emaya.model.Intervention;
import fr.emaya.model.Moto;
import fr.emaya.model.StatutIntervention;
import fr.emaya.model.TypeIntervention;
import fr.emaya.model.Utilitaire;
import fr.emaya.model.Vehicule;
import fr.emaya.model.Voiture;
import fr.emaya.service.Atelier;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Interface console : lit les saisies, appelle le service Atelier et affiche les résultats. */
public class App {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Atelier atelier;
    private final Scanner scanner;

    public App(Atelier atelier, Scanner scanner) {
        this.atelier = atelier;
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        Atelier atelier = new Atelier();
        DonneesDemo.charger(atelier);
        new App(atelier, new Scanner(System.in, StandardCharsets.UTF_8)).lancer();
    }

    public void lancer() {
        System.out.println("=== Emaya - Gestion d'atelier mécanique ===");
        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            try {
                switch (lireLigne("Votre choix : ")) {
                    case "1" -> listerClients();
                    case "2" -> ajouterClient();
                    case "3" -> ajouterVehicule();
                    case "4" -> planifierIntervention();
                    case "5" -> changerStatut();
                    case "6" -> afficherHistorique();
                    case "7" -> afficherPlanning();
                    case "8" -> afficherBilan();
                    case "0" -> continuer = false;
                    default -> System.out.println("Choix inconnu.");
                }
            } catch (RegleMetierException | ElementIntrouvableException e) {
                System.out.println("Erreur : " + e.getMessage());
            } catch (FinDeSaisieException e) {
                continuer = false;
            }
        }
        System.out.println("Au revoir !");
    }

    private void afficherMenu() {
        System.out.println();
        System.out.println("1. Lister les clients et leurs véhicules");
        System.out.println("2. Ajouter un client");
        System.out.println("3. Ajouter un véhicule à un client");
        System.out.println("4. Planifier une intervention");
        System.out.println("5. Changer le statut d'une intervention");
        System.out.println("6. Historique d'un véhicule");
        System.out.println("7. Planning d'une journée");
        System.out.println("8. Bilan (chiffre d'affaires et statistiques)");
        System.out.println("0. Quitter");
    }

    private void listerClients() {
        for (Client client : atelier.getClients()) {
            System.out.println(client);
            client.getVehicules().forEach(v -> System.out.println("    " + v));
        }
    }

    private void ajouterClient() {
        String nom = lireLigne("Nom : ");
        String telephone = lireLigne("Téléphone : ");
        Client client = atelier.ajouterClient(nom, telephone);
        System.out.println("Client créé : " + client);
    }

    private void ajouterVehicule() {
        int idClient = lireEntier("Numéro du client : ");
        atelier.trouverClient(idClient);
        int type = lireEntier("Type (1 = Voiture, 2 = Moto, 3 = Utilitaire) : ");
        String immat = Vehicule.validerImmatriculation(lireLigne("Immatriculation (AB-123-CD) : "));
        String marque = lireLigne("Marque : ");
        String modele = lireLigne("Modèle : ");
        int annee = lireEntier("Année : ");
        int km = lireEntier("Kilométrage : ");

        Vehicule vehicule = switch (type) {
            case 1 -> new Voiture(immat, marque, modele, annee, km, lireEntier("Nombre de portes : "));
            case 2 -> new Moto(immat, marque, modele, annee, km, lireEntier("Cylindrée (cm3) : "));
            case 3 -> new Utilitaire(immat, marque, modele, annee, km, lireEntier("Charge utile (kg) : "));
            default -> throw new RegleMetierException("Type de véhicule inconnu : " + type);
        };
        atelier.ajouterVehicule(idClient, vehicule);
        System.out.println("Véhicule ajouté : " + vehicule);
    }

    private void planifierIntervention() {
        String immat = lireLigne("Immatriculation : ");
        atelier.trouverVehicule(immat);
        TypeIntervention type = choisir("Type d'intervention", TypeIntervention.values());
        LocalDate date = lireDate("Date (jj/mm/aaaa, vide = aujourd'hui) : ");
        BigDecimal pieces = lireMontant("Coût des pièces HT (vide = 0) : ");
        Intervention intervention = atelier.planifierIntervention(immat, type, date, pieces);
        System.out.println("Intervention planifiée : " + intervention);
    }

    private void changerStatut() {
        atelier.getInterventions().forEach(System.out::println);
        int id = lireEntier("Numéro de l'intervention : ");
        StatutIntervention statut = choisir("Nouveau statut", StatutIntervention.values());
        atelier.changerStatut(id, statut);
        System.out.println("Statut mis à jour : " + atelier.trouverIntervention(id));
    }

    private void afficherHistorique() {
        String immat = lireLigne("Immatriculation : ");
        System.out.println("Propriétaire : " + atelier.proprietaireDe(immat).getNom());
        afficherListe(atelier.historique(immat), "Aucune intervention pour ce véhicule.");
    }

    private void afficherPlanning() {
        LocalDate jour = lireDate("Jour (jj/mm/aaaa, vide = aujourd'hui) : ");
        afficherListe(atelier.planning(jour), "Rien de prévu ce jour-là.");
    }

    private void afficherBilan() {
        System.out.println("Chiffre d'affaires TTC (interventions terminées) : " + atelier.chiffreAffairesTTC() + " €");
        Map<TypeIntervention, Long> stats = atelier.statistiquesParType();
        if (stats.isEmpty()) {
            System.out.println("Aucune intervention terminée.");
        }
        stats.forEach((type, nombre) -> System.out.println("  " + type.getLibelle() + " : " + nombre));
    }

    private void afficherListe(List<Intervention> interventions, String messageSiVide) {
        if (interventions.isEmpty()) {
            System.out.println(messageSiVide);
        }
        interventions.forEach(System.out::println);
    }

    // ---- Lecture des saisies ----

    private String lireLigne(String invite) {
        System.out.print(invite);
        if (!scanner.hasNextLine()) {
            throw new FinDeSaisieException();
        }
        return scanner.nextLine().trim();
    }

    private int lireEntier(String invite) {
        while (true) {
            try {
                return Integer.parseInt(lireLigne(invite).replace(" ", ""));
            } catch (NumberFormatException e) {
                System.out.println("Merci de saisir un nombre entier.");
            }
        }
    }

    private LocalDate lireDate(String invite) {
        while (true) {
            String saisie = lireLigne(invite);
            if (saisie.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(saisie, FORMAT_DATE);
            } catch (DateTimeParseException e) {
                System.out.println("Format attendu : jj/mm/aaaa.");
            }
        }
    }

    private BigDecimal lireMontant(String invite) {
        while (true) {
            String saisie = lireLigne(invite).replace(",", ".");
            if (saisie.isEmpty()) {
                return BigDecimal.ZERO;
            }
            try {
                return new BigDecimal(saisie);
            } catch (NumberFormatException e) {
                System.out.println("Montant invalide (exemple : 49.90).");
            }
        }
    }

    /** Affiche une liste numérotée de valeurs d'enum et renvoie celle choisie. */
    private <E extends Enum<E>> E choisir(String titre, E[] valeurs) {
        System.out.println(titre + " :");
        for (int i = 0; i < valeurs.length; i++) {
            String libelle = valeurs[i] instanceof TypeIntervention t ? t.getLibelle() : valeurs[i].name();
            System.out.println("  " + (i + 1) + ". " + libelle);
        }
        while (true) {
            int choix = lireEntier("Choix : ");
            if (choix >= 1 && choix <= valeurs.length) {
                return valeurs[choix - 1];
            }
            System.out.println("Choix hors liste.");
        }
    }

    /** Levée quand l'entrée standard est fermée (Ctrl+D ou fin d'un fichier redirigé). */
    private static class FinDeSaisieException extends RuntimeException {
    }
}
