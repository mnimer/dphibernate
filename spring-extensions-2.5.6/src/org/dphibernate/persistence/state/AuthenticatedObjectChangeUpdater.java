package org.dphibernate.persistence.state;

import java.util.List;
import java.util.Set;

import org.dphibernate.serialization.DPHibernateCache;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Extends ObjectChangeUpater to provide integration with Spring Security for providing
 *  Principals 
 */
@Transactional
public class AuthenticatedObjectChangeUpdater extends ObjectChangeUpdater
{

	public AuthenticatedObjectChangeUpdater() {
		super();
		setPrincipleProvider(new SpringPrincipalProvider());
	}

	public AuthenticatedObjectChangeUpdater(SessionFactory sessionFactory,
			IProxyResolver proxyResolver, DPHibernateCache cache) {
		super(sessionFactory, proxyResolver, cache);
		setPrincipleProvider(new SpringPrincipalProvider());
	}

	public AuthenticatedObjectChangeUpdater(SessionFactory sessionFactory,
			IProxyResolver proxyResolver) {
		super(sessionFactory, proxyResolver);
		setPrincipleProvider(new SpringPrincipalProvider());
	}
	
	@Override
	@Transactional(readOnly=false)
	public Set<ObjectChangeResult> update(List<ObjectChangeMessage> changeMessages)
	{
		return super.update(changeMessages);
	}
}
