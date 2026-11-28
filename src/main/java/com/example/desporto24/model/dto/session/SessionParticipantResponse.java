package com.example.desporto24.model.dto.session;

import com.example.desporto24.enumeration.AttendanceStatus;

import java.time.Instant;

public record SessionParticipantResponse(
        Long id,
        String username,
        AttendanceStatus attendanceStatus,
        Instant joinedAt,
        String teamCode,
        String profilePicUrl,
        /** Players who confirmed through the WhatsApp link (no account): username is "guest:<id>". */
        String displayName,
        boolean guest
) {
    public SessionParticipantResponse(
            Long id, String username, AttendanceStatus attendanceStatus,
            Instant joinedAt, String teamCode, String profilePicUrl
    ) {
        this(id, username, attendanceStatus, joinedAt, teamCode, profilePicUrl, username, false);
    }

    /** Preserve source compatibility for existing callers and tests. */
    public SessionParticipantResponse(
            Long id, String username, AttendanceStatus attendanceStatus,
            Instant joinedAt, String teamCode
    ) {
        this(id, username, attendanceStatus, joinedAt, teamCode, null);
    }

    public SessionParticipantResponse(
            Long id,
            String username,
            AttendanceStatus attendanceStatus,
            Instant joinedAt
    ) {
        this(id, username, attendanceStatus, joinedAt, null, null);
    }
}
