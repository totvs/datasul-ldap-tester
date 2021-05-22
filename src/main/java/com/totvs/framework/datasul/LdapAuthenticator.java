package com.totvs.framework.datasul;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LdapAuthenticator {

	private static final Logger logger = Logger.getLogger(LdapAuthenticator.class.getCanonicalName());

	private static final String INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private static final String SECURITY_AUTHENTICATION = "simple";

	@Value("${ldap.url}")
	private String ldapUrl;

	@Value("${ldap.user.base:not informed}")
	private String ldapUserBase;

	@Value("${ldap.manager.dn:not informed}")
	private String ldapManagerDn;

	@Value("${ldap.manager.password:not informed}")
	private String ldapManagerPassword;

	@Value("${ldap.user.search.base:not informed}")
	private String ldapUserSearchBase;

	@Value("${ldap.user.search.filter:sAMAccountName={0}}")
	private String ldapUserSearchFilter;

	public LdapAuthenticator() {
	}

	public boolean baseAuthentication(String username, String password) {
		logger.log(Level.INFO, "LDAP Base Authentication for user: " + username);

		String principal = "CN=" + username + "," + ldapUserBase;
		
		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.SECURITY_PRINCIPAL, principal);
		env.put(Context.SECURITY_CREDENTIALS, password);

		logger.log(Level.INFO, "LDAP URL: " + ldapUrl);
		logger.log(Level.INFO, "LDAP SECURITY PRINCIPAL: " + principal);
		logger.log(Level.FINEST, "LDAP SECURITY CREDENTIALS: " + password);

		DirContext dirContext = null;

		try {
			dirContext = getLdapContext(env);
			return true;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "LDAP Authentication Fail!", e);
		} finally {
			closeContext(dirContext);
		}

		return false;
	}

	private boolean searchAuthentication(String username, String password) throws Exception {
		logger.log(Level.INFO, "LDAP Search Authentication for username: " + username);

		logger.log(Level.INFO, "LDAP URL: " + ldapUrl);
		logger.log(Level.INFO, "LDAP SECURITY PRINCIPAL: " + ldapManagerDn);
		logger.log(Level.FINEST, "LDAP SECURITY CREDENTIALS: " + ldapManagerPassword);

		String distinguishedNameInNamespace = getDistinguishedNameInNamespace(username);
		
		Hashtable<String, String> ldapEnv = new Hashtable<>();
		
		ldapEnv.put(Context.SECURITY_PRINCIPAL, distinguishedNameInNamespace);
		ldapEnv.put(Context.SECURITY_CREDENTIALS, password);

		DirContext dirContext = null;
		
		try {
			dirContext = getLdapContext(ldapEnv);
			return true;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "LDAP Authentication Fail!", e);
		} finally {
			closeContext(dirContext);
		}

		return false;
	}

	private String getDistinguishedNameInNamespace(String username) throws Exception {
		String decodedPwd = new String(Base64.getDecoder().decode(ldapManagerPassword), StandardCharsets.ISO_8859_1);
		logger.log(Level.FINEST, "LDAP Decoded password: " + decodedPwd);
		
		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.SECURITY_PRINCIPAL, ldapManagerDn);
		env.put(Context.SECURITY_CREDENTIALS, decodedPwd);
		
		DirContext ctx = (DirContext) getLdapContext(env).lookup(ldapUserSearchBase);
		
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		String filter = ldapUserSearchFilter.replace("{0}", username);
		NamingEnumeration<SearchResult> results = ctx.search("", filter, searchControls);

		String distinguishedName = null;

		if (results.hasMoreElements()) {
			SearchResult result = (SearchResult) results.next();
			logger.log(Level.INFO, "LDAP distinguished name returned: "
					+ result.getAttributes().get("distinguishedName").get().toString());
			distinguishedName = result.getNameInNamespace();
		}

		results.close();

		return distinguishedName;
	}

	private DirContext getLdapContext(Hashtable<String, String> env) throws Exception {
		env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
		env.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION);
		env.put(Context.PROVIDER_URL, ldapUrl);
		return new InitialDirContext(env);
	}

	private void closeContext(DirContext context) {
		if (context != null) {
			try {
				context.close();
			} catch (Exception e) {
			}
		}
	}

	public boolean authenticate(String username, String password) throws Exception {
		if (ldapUserBase == null || "not informed".equals(ldapUserBase)) {
			return searchAuthentication(username, password);
		} else {
			return baseAuthentication(username, password);
		}
	}
}
