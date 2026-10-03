import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.keycloak.models.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.models.credential.OTPCredentialModel;

public class DeprecatedCredFormatTest {
    @Test
    public void passwordCredConversion() {
        UserRepresentation user = new UserRepresentation();
        user.setUsername("alice");
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType("password");
        cred.setHashIterations(10);
        cred.setAlgorithm("SHA256");
        cred.setSalt("salt");
        cred.setHashedSaltedValue("hash");
        cred.setValue(null);
        cred.setCredentialData(null);
        cred.setSecretData(null);
        user.setCredentials(Arrays.asList(cred));

        RepresentationToModel.convertDeprecatedCredentialsFormat(user);
        assertNotNull(cred.getCredentialData());
        assertNotNull(cred.getSecretData());
    }

    @Test
    public void totpCredConversion() {
        UserRepresentation user = new UserRepresentation();
        user.setUsername("bob");
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType("totp");
        cred.setDigits(6);
        cred.setCount(5);
        cred.setPeriod(30);
        cred.setAlgorithm("HmacSHA1");
        cred.setHashedSaltedValue("hash2");
        cred.setSalt("salt2");
        cred.setValue(null);
        cred.setCredentialData(null);
        cred.setSecretData(null);
        user.setCredentials(Arrays.asList(cred));

        RepresentationToModel.convertDeprecatedCredentialsFormat(user);
        assertTrue(cred.getType().equals("totp"));
        assertNotNull(cred.getCredentialData());
        assertNotNull(cred.getSecretData());
    }

    @Test
    public void skipConvertedCredential_return() {
        UserRepresentation user = new UserRepresentation();
        user.setUsername("carol");
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setValue("someValue");
        user.setCredentials(Arrays.asList(cred));

        RepresentationToModel.convertDeprecatedCredentialsFormat(user);
        // No changes expected
        assertEquals("someValue", cred.getValue());
    }
}
