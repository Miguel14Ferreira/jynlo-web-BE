package com.example.desporto24.service;

/**
 * Proposed championship entitlement model. This policy is intentionally not wired
 * into existing tournament endpoints until billing and migration are ready.
 */
public final class CommunityChampionshipPlanPolicy {
    public static final int FREE_CHAMPIONSHIPS_PER_COMMUNITY = 1;

    public enum Plan { FREE, PRO, CLUB }

    private CommunityChampionshipPlanPolicy() {}

    public static boolean canCreateChampionship(Plan plan, long existingChampionships) {
        if (plan == null || existingChampionships < 0) {
            throw new IllegalArgumentException("Plano ou contagem de campeonatos inválida");
        }
        return plan != Plan.FREE || existingChampionships < FREE_CHAMPIONSHIPS_PER_COMMUNITY;
    }

    public static boolean canManageAdvancedChampionshipRules(Plan plan) {
        if (plan == null) {
            throw new IllegalArgumentException("Plano inválido");
        }
        return plan == Plan.PRO || plan == Plan.CLUB;
    }

    /** Reading and participation remain available regardless of subscription. */
    public static boolean canReadAndParticipate(Plan plan) {
        return true;
    }
}
