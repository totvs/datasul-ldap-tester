package com.totvs.framework.datasul;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LdapAuthenticationTesterApplication implements CommandLineRunner {

	private static final Logger logger = Logger.getLogger(LdapAuthenticationTesterApplication.class.getCanonicalName());

	@Autowired
	private LdapAuthenticator ldapAuthenticator;

	@Autowired
	private Console console;

	public static void main(String[] args) {
		SpringApplication.run(LdapAuthenticationTesterApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.print("Usuário: ");
		String username = console.readLine();

		System.out.print("Senha: ");
		String password = new String(console.readPassword());

		logger.log(Level.FINEST, "Starting LDAP Authentication with username: " + username + " password: " + password);

		boolean result = ldapAuthenticator.authenticate(username, password);
		logger.log(Level.INFO, String.format("LDAP Authentication result: %s", (result) ? "Success" : "Fail"));
	}

}
