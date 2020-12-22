package com.safonov.galleryservice.ArtGalleryApplication.service.actor;

import com.safonov.galleryservice.ArtGalleryApplication.data.actor.*;
import com.safonov.galleryservice.ArtGalleryApplication.entity.actor.*;
import com.safonov.galleryservice.ArtGalleryApplication.model.response.ResponseOrMessage;
import com.safonov.galleryservice.ArtGalleryApplication.model.user.IdAndUserTypeModel;
import com.safonov.galleryservice.ArtGalleryApplication.model.user.RegistrationModel;
import com.safonov.galleryservice.ArtGalleryApplication.model.user.SignInModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

import static com.safonov.galleryservice.ArtGalleryApplication.configuration.Constants.Roles.*;

@Service
public class UserService {

    private final ClientRepository clientRepository;
    private final OwnerRepository ownerRepository;
    private final ArtistRepository artistRepository;
    private final CredentialsRepository credentialsRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(@NotNull final ClientRepository clientRepository,
                       @NotNull final OwnerRepository ownerRepository,
                       @NotNull final ArtistRepository artistRepository,
                       @NotNull final CredentialsRepository credentialsRepository,
                       @NotNull final RoleRepository roleRepository,
                       @NotNull final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.clientRepository = clientRepository;
        this.ownerRepository = ownerRepository;
        this.artistRepository = artistRepository;
        this.credentialsRepository = credentialsRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        if (credentialsRepository.findByLogin("admin").isEmpty()) {
            final Credentials admin = new Credentials();
            admin.setEmail("admin@mail.ru");
            admin.setLogin("admin");
            admin.setPassword(bCryptPasswordEncoder.encode("nimda"));
            final Role role = roleRepository.findRoleByName("ROLE_ADMIN").orElse(null);
            admin.setRole(role);
            credentialsRepository.save(admin);
        }
    }

    public ResponseOrMessage<Boolean> signUp(@NotNull final RegistrationModel model) {
        final Role role = roleRepository.findRoleByName(model.getRole().getCode()).orElse(null);
        if (role == null) {
            return new ResponseOrMessage<>("Role doesnt exist");
        }
        final Credentials credentials = new Credentials(model.getEmail(), bCryptPasswordEncoder.encode(model.getPassword()), role);
        try {
            switch (model.getRole()) {
                case ROLE_CLIENT:
                    final Client client = new Client(model.getFirstName(), model.getLastName());
                    client.setCredentials(credentials);
                    clientRepository.save(client);
                    return new ResponseOrMessage<>(true);
                case ROLE_OWNER:
                    final Owner owner = new Owner(model.getFirstName(), model.getLastName());
                    owner.setCredentials(credentials);
                    ownerRepository.save(owner);
                    return new ResponseOrMessage<>(true);
                case ROLE_ARTIST:
                    final Artist artist = new Artist(model.getFirstName(), model.getLastName());
                    artist.setCredentials(credentials);
                    artistRepository.save(artist);
                    return new ResponseOrMessage<>(true);
                default:
                    return new ResponseOrMessage<>("Wrong parameter");
            }
        } catch (Exception e) {
            return new ResponseOrMessage<>("Login already exist");
        }
    }

    public ResponseOrMessage<SignInModel> signIn(@NotNull final Map<String, String> emailOrUserName) {
        if (emailOrUserName.containsKey("emailOrUserName")) {
            final Credentials credentials = credentialsRepository.findByEmail(emailOrUserName.get("emailOrUserName"))
                    .orElseGet(() -> credentialsRepository.findByLogin(emailOrUserName.get("emailOrUserName")).orElse(null));
            if (credentials != null) {
                final SignInModel response = new SignInModel();
                User user = null;
                final Client client = clientRepository.findByCredentials(credentials).orElse(null);
                final Owner owner = ownerRepository.findByCredentials(credentials).orElse(null);
                final Artist artist = artistRepository.findByCredentials(credentials).orElse(null);
                for (final User elem : List.of(client, owner, artist)){
                    if (elem != null) {
                        switch (elem.getCredentials().getRole().getName()) {
                            case "ROLE_CLIENT":
                                final Client newClient = new Client(elem.getFirstName(), elem.getLastName());
                                user = clientRepository.save(newClient);
                                response.setRole(ROLE_CLIENT);
                                break;
                            case "ROLE_OWNER":
                                final Owner newOwner = new Owner(elem.getFirstName(), elem.getLastName());
                                user = ownerRepository.save(newOwner);
                                response.setRole(ROLE_OWNER);
                                break;
                            case "ROLE_ARTIST":
                                final Artist newArtist = new Artist(elem.getFirstName(), elem.getLastName());
                                user = artistRepository.save(newArtist);
                                response.setRole(ROLE_ARTIST);
                                break;
                            default:
                                return new ResponseOrMessage<>("Invalid user role");
                        }
                        break;
                    }
                }
                if (user == null) {
                    return new ResponseOrMessage<>("User not found");
                }
                response.setUserId(user.id);
                response.setPassword(user.getCredentials().getPassword());
                return new ResponseOrMessage<>(response);
            } else {
                return new ResponseOrMessage<>("Incorrect login");
            }
        } else {
            return new ResponseOrMessage<>("Wrong parameter");
        }
    }

    public ResponseOrMessage<User> getUserById(@NotNull final IdAndUserTypeModel model) {
        final Long personId = model.getUserId();
        User user;
        switch (model.getRole()) {
            case ROLE_CLIENT:
                user = clientRepository.findById(personId).orElse(null);
                break;
            case ROLE_OWNER:
                user = ownerRepository.findById(personId).orElse(null);
                break;
            case ROLE_ARTIST:
                user = artistRepository.findById(personId).orElse(null);
                break;
            default:
                return new ResponseOrMessage<>("Wrong with role parameter");
        }

        if (user == null) {
            return new ResponseOrMessage<>("User not found");
        }
        return new ResponseOrMessage<>(user);
    }
}
