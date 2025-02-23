package com.makethediference.mtdapi.web.controller;

import com.makethediference.mtdapi.domain.dto.playlist.ListPlaylist;
import com.makethediference.mtdapi.domain.dto.playlist.RegisterPlaylist;
import com.makethediference.mtdapi.domain.dto.playlist.UpdatePlaylist;
import com.makethediference.mtdapi.service.PlaylistService;
import com.makethediference.mtdapi.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;
    private final AuthService authService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<ListPlaylist> registerPlaylist(@RequestBody @Valid RegisterPlaylist registerPlaylistDto) {
        authService.authorizeRegisterPlaylist();

        ListPlaylist savedDto = playlistService.registerPlaylist(registerPlaylistDto);
        return ResponseEntity.ok(savedDto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ListPlaylist>> getAllPlaylists() {
        List<ListPlaylist> playlists = playlistService.getPlaylists();
        return ResponseEntity.ok(playlists);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    @Transactional
    public ResponseEntity<ListPlaylist> updatePlaylist(
            @PathVariable Long id,
            @RequestBody @Valid UpdatePlaylist updatePlaylistDto
    ) {
        ListPlaylist updatedDto = playlistService.updatePlaylist(id, updatePlaylistDto);
        return ResponseEntity.ok(updatedDto);
    }
}
