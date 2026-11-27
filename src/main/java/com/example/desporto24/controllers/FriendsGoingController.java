package com.example.desporto24.controllers;

import com.example.desporto24.service.FriendsGoingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FriendsGoingController {
    private final FriendsGoingService service;

    /** ?ids=1,2,3 → which friends are in each of these games. */
    @GetMapping("/api/sessions/friends-going")
    public List<FriendsGoingService.SessionFriends> friendsGoing(@RequestParam(name = "ids", required = false) List<Long> ids,
                                                                 Authentication auth) {
        return service.friendsGoing(auth == null ? null : auth.getName(), ids == null ? List.of() : ids);
    }
}
