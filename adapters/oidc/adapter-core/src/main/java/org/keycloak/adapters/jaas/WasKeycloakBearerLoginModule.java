package org.keycloak.adapters.jaas;

import org.keycloak.adapters.jaas.BearerTokenLoginModule;
import org.keycloak.common.VerificationException;
import org.keycloak.common.util.Time;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import java.security.Principal;
import java.util.*;

/**
 * Created by npassaro on 6/1/17.
 */
public class WasKeycloakBearerLoginModule extends BearerTokenLoginModule {
    private String username;
    private Map<String, ?> sharedState;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        super.initialize(subject, callbackHandler, sharedState, options);
        this.sharedState = sharedState;
    }

    @Override
    public boolean commit() throws LoginException {
        if (auth == null) {
            return false;
        }

        this.subject.getPrincipals().add(auth.getPrincipal());
        this.subject.getPrivateCredentials().add(auth.getTokenString());
        if (auth.getRoles() != null) {
            for (String roleName : auth.getRoles()) {
                Principal rolePrinc = createRolePrincipal(roleName);
                this.subject.getPrincipals().add(rolePrinc);

                Map shared = new HashMap();
                shared.put("com.ibm.wsspi.security.cred.propertiesObject", mapKeycloakSubject(subject.getPrincipals(), username));
                shared.putAll(sharedState);
                this.sharedState = shared;
            }
        }

        return true;
    }
    @Override
    protected Auth doAuth(String username, String password) throws VerificationException {
        this.username = username;
        // Should do some checking of authenticated username if it's equivalent to passed value?
        return bearerAuth(password);
    }

    private Map<String, Object> mapKeycloakSubject(Set<Principal> principals, String username) {
        List<Principal> principalList = new ArrayList<Principal>(principals);
        String uuid = principalList.remove(0).getName(); //first principal is always the keycloak user uuid

        Map<String, Object> hashtable = new Hashtable<String, Object>();
        hashtable.put("com.ibm.wsspi.security.cred.userId", uuid);
        hashtable.put("com.ibm.wsspi.security.cred.securityName", username);
        hashtable.put("com.ibm.wsspi.security.cred.groups", principalList);
        hashtable.put("com.ibm.wsspi.security.cred.cacheKey", uuid + Time.currentTimeMillis());
        return hashtable;
    }

}