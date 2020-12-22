package com.safonov.galleryservice.ArtGalleryApplication.service.actor;

import com.safonov.galleryservice.ArtGalleryApplication.data.actor.ArtistRepository;
import com.safonov.galleryservice.ArtGalleryApplication.data.gallery.*;
import com.safonov.galleryservice.ArtGalleryApplication.entity.actor.Artist;
import com.safonov.galleryservice.ArtGalleryApplication.entity.gallery.Artwork;
import com.safonov.galleryservice.ArtGalleryApplication.entity.gallery.Expo;
import com.safonov.galleryservice.ArtGalleryApplication.entity.gallery.OwnerArtistPayment;
import com.safonov.galleryservice.ArtGalleryApplication.model.gallery.AcceptRoyaltiesModel;
import com.safonov.galleryservice.ArtGalleryApplication.model.gallery.AddArtworkModel;
import com.safonov.galleryservice.ArtGalleryApplication.model.response.ApiResponse;
import com.safonov.galleryservice.ArtGalleryApplication.model.response.ResponseOrMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtworkRepository artworkRepository;
    private final ExpoRepository expoRepository;
    private final OwnerArtistPaymentRepository ownerArtistPaymentRepository;

    @Autowired
    public ArtistService(@NotNull final ArtistRepository artistRepository,
                         @NotNull final ArtworkRepository artworkRepository,
                         @NotNull final ExpoRepository expoRepository,
                         @NotNull final OwnerArtistPaymentRepository ownerArtistPaymentRepository) {
        this.artistRepository = artistRepository;
        this.artworkRepository = artworkRepository;
        this.expoRepository = expoRepository;
        this.ownerArtistPaymentRepository = ownerArtistPaymentRepository;
    }

    @Transactional
    public ApiResponse addArtwork(@NotNull final AddArtworkModel model){
        final Artist artist = artistRepository.findById(model.getArtistId()).orElse(null);
        if (artist == null) {
            return new ApiResponse("Artist doesnt exist");
        }
        final Artwork artwork = artworkRepository.findById(model.getArtwork().getArtworkId()).orElse(null);
        final List<Artwork> artworks = artist.getArtworks();
        if (artwork == null) {
            final Artwork newArtwork = new Artwork(model.getArtwork().getName(), model.getArtwork().getInfo(), artist, null);
            artworks.add(artworkRepository.save(newArtwork));
            artistRepository.save(artist);
            return new ApiResponse("New artwork from artist " + artist.getCredentials().getLogin() + "added for expo");
        } else if (artist.equals(artwork.getArtist())) {
            return new ApiResponse("Artwork " + artwork.getName() + "belongs to another artist");
        } else if (artworks.contains(artwork)) {
            return new ApiResponse("Artwork already existed for this artist");
        } else {
            artworks.add(artworkRepository.save(artwork));
            artistRepository.save(artist);
            return new ApiResponse("Existed artwork from artist " + artist.getCredentials().getLogin() + "added for expo");
        }
    }

    public ApiResponse acceptRoyalties(@NotNull final AcceptRoyaltiesModel model){
        final Expo closedExpo = expoRepository.findById(model.getExpo().getExpoId()).orElse(null);
        if (closedExpo == null){
            return new ApiResponse("Expo doesnt exist");
        } if (!closedExpo.isClosed()){
            return new ApiResponse("Expo with name " + closedExpo.getName() + " hasn't closed");
        }
        final Artist artist = artistRepository.findById(model.getArtistId()).orElse(null);
        if (artist == null) {
            return new ApiResponse("Artist doesnt exist");
        }
        final OwnerArtistPayment payment = ownerArtistPaymentRepository.findPaymentByExpo(closedExpo).orElse(null);
        if (payment == null){
            return new ApiResponse("Payment for expo " + closedExpo.getName() + " doesnt exist");
        }
        artist.getArtworks().clear();
        artistRepository.save(artist);
        return new ApiResponse("Artist " + artist.getCredentials().getLogin() + " accepted royalties for expo " + closedExpo.getName());
    }

    public ResponseOrMessage<List<Artwork>> getAllArtworks(@NotNull final Map<String, Long> artistId) {
        if (artistId.containsKey("artistId")) {
            final Artist artist = artistRepository.findById(artistId.get("artistId")).orElse(null);
            if (artist == null) {
                return new ResponseOrMessage<>("Artist doesnt exist");
            }
            final List<Artwork> artworks = artworkRepository.findArtworksByArtist(artist);
            if (artworks == null) {
                return new ResponseOrMessage<>("Artist has no artworks");
            } else {
                return new ResponseOrMessage<>(artworks);
            }
        } else {
            return new ResponseOrMessage<>("Wrong parameter");
        }
    }

    public ResponseOrMessage<List<Artwork>> getExpoArtworks(@NotNull final Map<String, Long> artistId) {
        if (artistId.containsKey("artistId")) {
            final Artist artist = artistRepository.findById(artistId.get("artistId")).orElse(null);
            if (artist == null) {
                return new ResponseOrMessage<>("Artist doesnt exist");
            }
            final List<Artwork> artworks = artist.getArtworks();
            if (artworks == null) {
                return new ResponseOrMessage<>("Artist has no artworks for expo");
            } else {
                return new ResponseOrMessage<>(artworks);
            }
        } else {
            return new ResponseOrMessage<>("Wrong parameter");
        }
    }
}
