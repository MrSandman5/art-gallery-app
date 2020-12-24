package com.safonov.galleryservice.ArtGalleryApplication.model.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CredentialsModel {
    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;
}
