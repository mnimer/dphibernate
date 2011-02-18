package net.digitalprimates.persistence.hibernate.utils;

import java.security.Principal;

import net.digitalprimates.persistence.hibernate.proxy.IHibernateProxy;
import net.digitalprimates.persistence.state.IChangeMessageInterceptor;
import net.digitalprimates.persistence.state.IProxyResolver;
import net.digitalprimates.persistence.state.ObjectChangeMessage;
import net.digitalprimates.persistence.state.PropertyChangeMessage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

/**
 * ChangeMessageInterceptor which encrypts passwords using a provided spring PasswordEncoder.
 * 
 *  It's reccommended that this is used only as a post processor.
 * 
 * Although this could be used as either a pre or post processor, the entities primary key is used
 * as the salt for the password encoder.
 * 
 * Therefore, if this is used as a preprocessor on an object creation, it's likely to either fail,
 * or create a password which is not decoded correctly.
 * 

 * @author Marty Pitt
 *
 */
public class PasswordEncryptionInterceptor implements IPasswordEncryptionInterceptor
{
	private final String passwordPropertyName;
	private final Class<? extends IHibernateProxy> entityClass;
	private final PasswordEncoder passwordEncoder;
	private final SessionFactory sessionFactory;


	public PasswordEncryptionInterceptor( Class<? extends IHibernateProxy> entityClass, String passwordPropertyName, PasswordEncoder passwordEncoder,SessionFactory sessionFactory)
	{
		this.passwordPropertyName = passwordPropertyName;
		this.entityClass = entityClass;
		this.passwordEncoder = passwordEncoder;
		this.sessionFactory = sessionFactory;
	}


	@Override
	public boolean appliesToMessage(ObjectChangeMessage message)
	{
		if (!message.getOwner().getRemoteClassName().equals(entityClass.getName()))
		{
			return false;
		}
		if (message.containsChangeToProperty(passwordPropertyName))
		{
			return true;
		}
		return false;
	}


	@Override
	@Transactional
	public void processMessage(ObjectChangeMessage message, IProxyResolver proxyResolver)
	{
		IHibernateProxy entity = (IHibernateProxy) proxyResolver.resolve(message.getOwner());
		String password = getOriginalPassword(message);
		String encodedPassword = getEncodedPassword(entity, password);
		updateEntityWithEncodedPassword(entity, encodedPassword);
	}


	private void updateEntityWithEncodedPassword(IHibernateProxy entity, String encodedPassword)
	{
		String setterName = "set" + StringUtils.capitalize(passwordPropertyName);
		try
		{
			ReflectionUtils.findMethod(entityClass, setterName,new Class[]{String.class})
				.invoke(entity, encodedPassword);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		sessionFactory.getCurrentSession().update(entity);
	}


	private String getEncodedPassword(IHibernateProxy entity, String password)
	{
		Object proxyKey = entity.getProxyKey();
		String encodedPassword = passwordEncoder.encodePassword(password, proxyKey);
		return encodedPassword;
	}


	private String getOriginalPassword(ObjectChangeMessage message)
	{
		PropertyChangeMessage propertyChange = message.getPropertyChange(passwordPropertyName);
		String password = (String) propertyChange.getNewValue();
		return password;
	}


	@Override
	public void processMessage(ObjectChangeMessage message, IProxyResolver proxyResolver, Principal user)
	{
		processMessage(message, proxyResolver);
	}

}