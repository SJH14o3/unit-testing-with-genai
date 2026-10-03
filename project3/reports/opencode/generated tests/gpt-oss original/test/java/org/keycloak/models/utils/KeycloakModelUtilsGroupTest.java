import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.*;

import java.util.*;

import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;

public class KeycloakModelUtilsGroupTest {

    @Test
    public void testSplitPath_handlesEscape() {
        String[] parts = KeycloakModelUtils.splitPath("/a/\~b/c", true);
        Assertions.assertArrayEquals(new String[]{"a", "~b", "c"}, parts);
    }

    @Test
    public void testBuildGroupPath_normal() {
        String path = KeycloakModelUtils.buildGroupPath(false, "root", "child");
        Assertions.assertEquals("/root/child", path);
    }

    @Test
    public void testBuildGroupPath_escape() {
        String path = KeycloakModelUtils.buildGroupPath(true, "root", "child/folder");
        Assertions.assertEquals("/root/child~folder", path);
    }

    @Test
    public void testNormalizeGroupPath_variants() {
        Assertions.assertEquals("/test", KeycloakModelUtils.normalizeGroupPath("test"));
        Assertions.assertEquals("/test", KeycloakModelUtils.normalizeGroupPath("/test"));
        Assertions.assertEquals("/test", KeycloakModelUtils.normalizeGroupPath("/test/"));
    }

    @Test
    public void testFindGroupByPath_realmValid() {
        KeycloakSession session = mock(KeycloakSession.class);
        RealmModel realm = mock(RealmModel.class);
        GroupProvider groupProvider = mock(GroupProvider.class);
        GroupModel g1 = mock(GroupModel.class);
        GroupModel g2 = mock(GroupModel.class);
        when(session.groups()).thenReturn(groupProvider);
        when(groupProvider.getGroupByName(realm, null, "root")).thenReturn(g1);
        when(groupProvider.getGroupByName(realm, g1, "child")).thenReturn(g2);

        GroupModel result = KeycloakModelUtils.findGroupByPath(session, realm, "/root/child");
        Assertions.assertSame(g2, result);
    }

    @Test
    public void testFindGroupByPath_returnsNull_onNull() {
        Assertions.assertNull(KeycloakModelUtils.findGroupByPath(null, null, null));
    }
}
