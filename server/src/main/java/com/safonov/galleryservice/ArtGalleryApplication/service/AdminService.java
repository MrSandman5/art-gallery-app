package com.safonov.galleryservice.ArtGalleryApplication.service;

import com.safonov.galleryservice.ArtGalleryApplication.data.actor.*;
import com.safonov.galleryservice.ArtGalleryApplication.entity.actor.Artist;
import com.safonov.galleryservice.ArtGalleryApplication.entity.actor.Client;
import com.safonov.galleryservice.ArtGalleryApplication.entity.actor.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class AdminService {

    private final ClientRepository clientRepository;
    private final OwnerRepository ownerRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public AdminService(@NotNull final ClientRepository clientRepository,
                        @NotNull final OwnerRepository ownerRepository,
                        @NotNull final ArtistRepository artistRepository) {
        this.clientRepository = clientRepository;
        this.ownerRepository = ownerRepository;
        this.artistRepository = artistRepository;
    }

    public ResponseEntity<String> deletePerson(@NotNull final Long userId,
                                               @NotNull final String userType) {
        switch (userType) {
            case "ROLE_CLIENT":
                final Client client = clientRepository.findById(userId).orElse(null);
                if (client == null) {
                    return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND);
                }
                clientRepository.delete(client);
                return new ResponseEntity<>("Client was deleted", HttpStatus.OK);

            case "ROLE_OWNER":
                final Owner owner = ownerRepository.findById(userId).orElse(null);
                if (owner == null) {
                    return new ResponseEntity<>("Owner not found", HttpStatus.NOT_FOUND);
                }
                ownerRepository.save(owner);
                return new ResponseEntity<>("Owner was deleted", HttpStatus.OK);
            case "ROLE_ARTIST":
                final Artist artist = artistRepository.findById(userId).orElse(null);
                if (artist == null) {
                    return new ResponseEntity<>("Artist not found", HttpStatus.NOT_FOUND);
                }
                artistRepository.save(artist);
                return new ResponseEntity<>("Artist was deleted", HttpStatus.OK);
            default:
                return new ResponseEntity<>("Wrong parameter", HttpStatus.BAD_REQUEST);
        }
    }
}
