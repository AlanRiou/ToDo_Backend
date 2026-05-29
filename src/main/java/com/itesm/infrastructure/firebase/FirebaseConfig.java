package com.itesm.infrastructure.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.quarkus.arc.profile.UnlessBuildProfile;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

@Startup
@ApplicationScoped
@UnlessBuildProfile("test")
public class FirebaseConfig {
    private static final Logger LOG = Logger.getLogger(FirebaseConfig.class.getName());

    @ConfigProperty(name="firebase.service-account-location")
    String path;

    @PostConstruct
    void init(){
        try{
            if(FirebaseApp.getApps().isEmpty()){
                Path serviceAccountPath = Path.of(path);
                if (!Files.isRegularFile(serviceAccountPath)) {
                    throw new IllegalStateException("Firebase service account file not found: " + path);
                }

                InputStream serviceAccount = new FileInputStream(serviceAccountPath.toFile());
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
                LOG.info("Firebase initialized");
            }
        }catch (Exception e){
            throw new IllegalStateException("Firebase could not be initialized", e);
        }
    }

}
