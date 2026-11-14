package com.example.desporto24.constant;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class SportsCatalog {

    private SportsCatalog() {
    }

    public static final Set<String> ALLOWED = Set.of(
            "Aeromodelismo",
            "Aikido",
            "Airsoft",
            "Andebol",
            "Apneia",
            "Artes Marciais Chinesas",
            "Atividades Subaquáticas",
            "Atletismo",
            "Automobilismo",
            "Badminton",
            "Basebol",
            "Basquetebol",
            "Basquetebol 3x3",
            "Bilhar",
            "BMX",
            "Bodyboard",
            "Bowling",
            "Boxe",
            "Breaking",
            "Bridge",
            "Calistenia",
            "Caminhada",
            "Campismo",
            "Canoagem",
            "Canoagem Slalom",
            "Canoagem Velocidade",
            "Ciclismo",
            "Ciclismo de Estrada",
            "Ciclismo de Pista",
            "Ciclocrosse",
            "Columbofilia",
            "Corfebol",
            "Corrida",
            "Corrida de obstáculos",
            "Cricket",
            "Curling",
            "Damas",
            "Dança Desportiva",
            "Escalada",
            "Esgrima",
            "Esqui",
            "Esqui Alpino",
            "Esqui de Fundo",
            "Esqui Freestyle",
            "Esqui de Montanha",
            "Flag Football",
            "Futebol",
            "Futebol Americano",
            "Futevólei",
            "Futsal",
            "Ginásio",
            "Ginástica",
            "Ginástica Artística",
            "Ginástica Rítmica",
            "Golfe",
            "Halterofilismo",
            "Hipismo",
            "Hóquei",
            "Hóquei em Campo",
            "Hóquei em Patins",
            "Hóquei no Gelo",
            "Jiu-Jitsu",
            "Jiu-Jitsu Brasileiro",
            "Judo",
            "Karaté",
            "Karting",
            "Kempo",
            "Kickboxing",
            "Kung Fu",
            "Lacrosse",
            "Luta Greco-Romana",
            "Luta Livre Olímpica",
            "Mergulho",
            "Montanhismo",
            "Motociclismo",
            "Motocross",
            "Motonáutica",
            "Mountain Bike",
            "Muay Thai",
            "Musculação",
            "Natação",
            "Natação Artística",
            "Natação em Águas Abertas",
            "Orientação",
            "Padel",
            "Paraquedismo",
            "Parkour",
            "Patinagem",
            "Patinagem Artística",
            "Patinagem de Velocidade",
            "Pentatlo Moderno",
            "Pesca Desportiva",
            "Petanca",
            "Pickleball",
            "Pilates",
            "Polo Aquático",
            "Powerlifting",
            "Remo",
            "Rugby",
            "Rugby Sevens",
            "Skate",
            "Snowboard",
            "Softbol",
            "Squash",
            "Stand Up Paddle",
            "Surf",
            "Teqball",
            "Taekwondo",
            "Ténis",
            "Ténis de praia",
            "Ténis de mesa",
            "Tiro",
            "Tiro com Arco",
            "Trail Running",
            "Trampolim",
            "Triatlo",
            "Vela",
            "Voo Livre",
            "Voleibol",
            "Voleibol de Praia",
            "Xadrez",
            "Yoga"
    );

    /**
     * Community and profile sports remain restricted to the official catalog.
     * Independent sessions may use a proposed sport without claiming that
     * tournament rankings or official venue compatibility are supported.
     */
    public static boolean isCustomSessionSport(String name) {
        if (name == null || name.length() < 2 || name.length() > 60
                || !name.equals(name.trim()) || ALLOWED.contains(name)) {
            return false;
        }
        if (!name.matches("^[\\p{L}\\p{M}][\\p{L}\\p{M}\\p{N} .&'’()/-]*$")) {
            return false;
        }
        String normalized = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(java.util.Locale.ROOT);
        return ALLOWED.stream().noneMatch(known ->
                java.text.Normalizer.normalize(known, java.text.Normalizer.Form.NFD)
                        .replaceAll("\\p{M}", "").toLowerCase(java.util.Locale.ROOT)
                        .equals(normalized));
    }

    public static boolean isAllowedSessionSport(String name) {
        return ALLOWED.contains(name) || isCustomSessionSport(name);
    }

    public static boolean isValidSelection(String favoriteSports) {
        if (favoriteSports == null || favoriteSports.isBlank()) {
            return true;
        }

        Set<String> selected = Arrays.stream(favoriteSports.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toSet());

        return !selected.isEmpty() && selected.stream().allMatch(ALLOWED::contains);
    }
}
