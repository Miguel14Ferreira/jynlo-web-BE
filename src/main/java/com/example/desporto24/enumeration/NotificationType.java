package com.example.desporto24.enumeration;

public enum NotificationType {
    WAITLIST_PROMOTED,
    SESSION_REMOVED,
    SESSION_CANCELLED,
    SESSION_MEETING_POINT_UPDATED,
    SESSION_DETAILS_UPDATED,
    SESSION_POST_GAME_REVIEW,
    ATTENDANCE_DISPUTE_ACCEPTED,
    ATTENDANCE_DISPUTE_REJECTED,
    COMMUNITY_INVITE,
    COMMUNITY_JOIN_REQUEST,
    COMMUNITY_JOIN_ACCEPTED,
    COMMUNITY_JOIN_REJECTED,
    SESSION_INVITE,
    CHAT_MESSAGE,
    ADMIN_MESSAGE,
    SOCIAL_NEW_FOLLOWER,
    SOCIAL_POST_LIKED,
    SOCIAL_POST_COMMENTED,
    SOCIAL_STORY_LIKED,
    SOCIAL_STORY_REPLIED,
    VENUE_EDIT_DECISION,
    TEAM_MEMBER_INVITE,
    TEAM_ADMIN_TRANSFER,
    TEAM_ADMIN_ASSIGNED,
    /** Pre-game reminder, about 24h before the session. */
    SESSION_REMINDER,
    /** A tournament result involving the recipient was recorded or corrected. */
    TOURNAMENT_RESULT,
    /** The leader of a league the recipient plays in changed. */
    TOURNAMENT_LEADER,
    /** The organiser asked players who have not paid yet to pay. */
    PAYMENT_REMINDER,
    /** A new public game near the recipient's chosen place (opt-in). */
    NEARBY_SESSION,
    /** A member of a group or community asked who can play at some proposed times. */
    AVAILABILITY_REQUEST,
    /** Reminder, 24h later, to members who have not answered an availability request. */
    AVAILABILITY_REMINDER,
    /** A member of one of the recipient's communities created a game for that community. */
    COMMUNITY_SESSION_CREATED,
    /** Someone is calling the recipient (app may be closed). */
    CALL_INCOMING,
    /** A call to the recipient ended before being answered. */
    CALL_MISSED,
    /** Venue claims and Jynlo court booking requests: new request, confirmed, declined, cancelled. */
    VENUE_BOOKING
}
