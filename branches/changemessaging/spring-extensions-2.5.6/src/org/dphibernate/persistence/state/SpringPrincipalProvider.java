package org.dphibernate.persistence.state;

import java.security.Principal;

import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

public class SpringPrincipalProvider implements IPrincipalProvider {

	@Override
	public Principal getPrincipal() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (Principal) authentication.getPrincipal();
	}

	@Override
	public boolean isAnonymous() {
		return getPrincipal().equals("roleAnonymous");
	}

}