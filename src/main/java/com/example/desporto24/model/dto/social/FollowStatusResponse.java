package com.example.desporto24.model.dto.social;

public record FollowStatusResponse(
        boolean followedByMe,
        boolean followsMe,
        long followers,
        long following,
        boolean requestPending,
        boolean privateProfile
) {
    public FollowStatusResponse(boolean followedByMe, boolean followsMe,
            long followers, long following) {
        this(followedByMe,followsMe,followers,following,false,false);
    }
}
