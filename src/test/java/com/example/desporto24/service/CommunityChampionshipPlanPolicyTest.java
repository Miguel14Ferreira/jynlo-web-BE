package com.example.desporto24.service;

import org.junit.jupiter.api.Test;

import static com.example.desporto24.service.CommunityChampionshipPlanPolicy.*;
import static org.junit.jupiter.api.Assertions.*;

class CommunityChampionshipPlanPolicyTest {
    @Test
    void freePlanAllowsOneChampionshipPerCommunity() {
        assertTrue(canCreateChampionship(Plan.FREE, 0));
        assertFalse(canCreateChampionship(Plan.FREE, 1));
        assertFalse(canCreateChampionship(Plan.FREE, 2));
    }

    @Test
    void paidOrganizersCanCreateMultipleChampionshipsAndManageAdvancedRules() {
        for (Plan plan : new Plan[]{Plan.PRO, Plan.CLUB}) {
            assertTrue(canCreateChampionship(plan, 5));
            assertTrue(canManageAdvancedChampionshipRules(plan));
        }
        assertFalse(canManageAdvancedChampionshipRules(Plan.FREE));
    }

    @Test
    void participationAndReadingNeverDependOnPlan() {
        assertTrue(canReadAndParticipate(Plan.FREE));
        assertTrue(canReadAndParticipate(Plan.PRO));
        assertTrue(canReadAndParticipate(Plan.CLUB));
        assertTrue(canReadAndParticipate(null));
    }

    @Test
    void invalidEntitlementInputsFailClosed() {
        assertThrows(IllegalArgumentException.class, () -> canCreateChampionship(null, 0));
        assertThrows(IllegalArgumentException.class, () -> canCreateChampionship(Plan.FREE, -1));
        assertThrows(IllegalArgumentException.class, () -> canManageAdvancedChampionshipRules(null));
    }
}
