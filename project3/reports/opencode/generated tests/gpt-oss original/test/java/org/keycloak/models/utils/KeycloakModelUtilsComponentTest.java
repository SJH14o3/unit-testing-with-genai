import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.*;

import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;

public class KeycloakModelUtilsComponentTest {

    @Test
    public void testComponentModelGetter_returnsFunction() {
        java.util.function.Function<KeycloakSessionFactory, ComponentModel> fn = KeycloakModelUtils.componentModelGetter("realm1", "comp1");
        Assertions.assertNotNull(fn);
    }

    @Test
    public void testGetComponentModel_null() {
        KeycloakSessionFactory factory = mock(KeycloakSessionFactory.class);
        when(factory.create()).thenThrow(new RuntimeException("should not be called"));
        ComponentModel result = KeycloakModelUtils.getComponentModel(factory, "realm1", "comp1");
        Assertions.assertNull(result);
    }
}
