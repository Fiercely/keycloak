/**
 * 
 */
package org.keycloak.testsuite.console.roles;

import static org.junit.Assert.assertTrue;
import static org.keycloak.testsuite.admin.ApiUtil.createUserWithAdminClient;

import java.util.List;

import org.jboss.arquillian.graphene.page.Page;
import org.junit.Before;
import org.junit.Test;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.console.page.roles.DefaultRoles;
import org.keycloak.testsuite.console.page.roles.RealmRoles;
import org.keycloak.testsuite.console.page.roles.Role;
import org.keycloak.testsuite.console.page.roles.Roles;
import org.keycloak.testsuite.console.page.users.UserRoleMappings;
import org.keycloak.testsuite.console.page.users.Users;

/**
 * @author <a href="mailto:antonio.ferreira@fiercely.pt">Antonio Ferreira</a>
 *
 */
public class UsersInRoleTest extends AbstractRolesTest {
    

    @Page
    private DefaultRoles defaultRolesPage;

    @Page
    private UserRoleMappings userRolesPage;
    
    @Page
    private Users usersPage;
    
    @Page
    private Roles rolesPage;
    
    @Page
    private Role rolePage;

    @Page
    private RealmRoles realmRolesPage;
    
    private RoleRepresentation testRoleRep;



    @Before
    public void beforeDefaultRolesTest() {
        // create a role via admin client
        testRoleRep = new RoleRepresentation("test-role", "", false);
        rolesResource().create(testRoleRep);

        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername("test_user");
        createUserWithAdminClient(testRealmResource(), newUser);
        rolesResource().create(testRoleRep);
        rolesPage.navigateTo();
    }


    public RolesResource rolesResource() {
        return testRealmResource().roles();
    }
    
    //Added for KEYCLOAK-2035
    @Test
    public void usersInRoleTabIsPresent() {

        rolesPage.navigateTo();
        rolesPage.tabs().realmRoles();
        realmRolesPage.table().search(testRoleRep.getName());
        realmRolesPage.table().clickRole(testRoleRep.getName());        
       
        rolePage.tabs();
        
        //users.table().search(newUser.getUsername());
       // users.table().clickUser(newUser.getUsername());

        userPage.tabs().roleMappings();
        //assertTrue(userRolesPage.form().isAssignedRole(testRoleName));
    }

    //Added for KEYCLOAK-2035
    @Test
    public void roleHasUsersinTab() {

        String defaultRoleName = testRoleRep.getName();

        defaultRolesPage.form().addAvailableRole(defaultRoleName);
        assertAlertSuccess();

        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername("new_user");

        createUserWithAdminClient(testRealmResource(), newUser);
        usersPage.navigateTo();
        usersPage.table().search(newUser.getUsername());
        usersPage.table().clickUser(newUser.getUsername());

        userPage.tabs().roleMappings();
        assertTrue(userRolesPage.form().isAssignedRole(defaultRoleName));
    }
    
    @Test
    public void removeRoleAndConfirmNoUsers() {

        String defaultRoleName = testRoleRep.getName();

        defaultRolesPage.form().addAvailableRole(defaultRoleName);
        assertAlertSuccess();

        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername("new_user");

        createUserWithAdminClient(testRealmResource(), newUser);
        usersPage.navigateTo();
        usersPage.table().search(newUser.getUsername());
        usersPage.table().clickUser(newUser.getUsername());

        userPage.tabs().roleMappings();
        assertTrue(userRolesPage.form().isAssignedRole(defaultRoleName));
    }


}
