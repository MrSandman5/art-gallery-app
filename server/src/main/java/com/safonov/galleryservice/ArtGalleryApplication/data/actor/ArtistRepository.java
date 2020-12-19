package com.safonov.galleryservice.ArtGalleryApplication.data.actor;

import com.safonov.galleryservice.ArtGalleryApplication.data.BaseRepository;
import com.safonov.galleryservice.ArtGalleryApplication.entity.actor.Artist;
import com.safonov.galleryservice.ArtGalleryApplication.entity.actor.Credentials;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface ArtistRepository extends BaseRepository<Artist> {
    Optional<Artist> findByCredentials(@NotNull final Credentials credentials);
}
