package com.example.desporto24.controllers;

import com.example.desporto24.service.SportsTeamRosterImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Team squad imported from Excel/CSV. Owner only. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}/roster")
public class SportsTeamRosterController {
    private final SportsTeamRosterImportService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<SportsTeamRosterImportService.Entry> list(@PathVariable Long teamId, Authentication auth) {
        return service.list(teamId, auth.getName());
    }

    @PostMapping(value = "/import/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public SportsTeamRosterImportService.Preview preview(@PathVariable Long teamId,
            @RequestPart("file") MultipartFile file, Authentication auth) {
        return service.preview(teamId, auth.getName(), file);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public SportsTeamRosterImportService.Result confirm(@PathVariable Long teamId,
            @RequestPart("file") MultipartFile file, Authentication auth) {
        return service.confirm(teamId, auth.getName(), file);
    }

    @DeleteMapping("/{entryId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long teamId, @PathVariable Long entryId, Authentication auth) {
        service.delete(teamId, entryId, auth.getName());
    }
}
