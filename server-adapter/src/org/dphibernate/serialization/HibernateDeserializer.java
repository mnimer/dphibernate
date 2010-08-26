/**
	Copyright (c) 2008. Digital Primates IT Consulting Group
	http://www.digitalprimates.net
	All rights reserved.
	
	This library is free software; you can redistribute it and/or modify it under the 
	terms of the GNU Lesser General Public License as published by the Free Software 
	Foundation; either version 2.1 of the License.

	This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
	without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
	See the GNU Lesser General Public License for more details.

	
	@author: Mike Nimer
	@ignore
**/

package org.dphibernate.serialization;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import org.dphibernate.adapters.RemotingAdapter;
import org.dphibernate.core.IHibernateProxy;
import org.hibernate.collection.PersistentCollection;
import org.w3c.dom.Document;

import flex.messaging.io.amf.ASObject;
import flex.messaging.messages.RemotingMessage;


/**
 * convert incoming flash request back to java/java hibernate objects
 * 
 * @author mike nimer
 */
@SuppressWarnings("unchecked")
public class HibernateDeserializer implements IDeserializer
{

	private RemotingAdapter adapter;
	private RemotingMessage remotingMessage;

	private HashMap cache;

	@Override
	public Object translate(RemotingAdapter adapter, RemotingMessage message, String sessionFactoryClassName, String getSessionMethod, Object obj)
	{
		this.cache = new HashMap();
		this.adapter = adapter;
		this.remotingMessage = message;

		Object result = translate(obj);
		return result;
	}


	private Object translate(Object obj)
	{
		return translate(obj, null);
	}
	
	
	private Object translate(Object obj, Class type)
	{
	    
		if (cache.containsKey(obj))
		{
			return cache.get(obj);
		}
		cache.put(obj, obj);

	
		if (obj == null || "java.lang.Class".equals(obj.getClass().getName()))
		{
			return obj;
		} 
		else if (obj instanceof PersistentCollection && !((PersistentCollection) obj).wasInitialized())
		{
			Object pcResult = readPersistanceCollection(obj); 
			
			//replace cache with initialized item
			cache.put(obj, pcResult);
			
			return pcResult;
		} 
		else if (obj != null && obj instanceof IHibernateProxy && !((IHibernateProxy) obj).getProxyInitialized())
		{
			Object hibResult = readHibernateProxy(obj);
			
			//replace cache with initialized item
			cache.put(obj, hibResult);
			
			return hibResult;
		} 
		else if (obj instanceof Collection)
		{
			Object coll = readCollection(obj, type);
			
			//replace cache with initialized item
			cache.put(obj, coll);
			
			return coll;
		} 
		else if (obj instanceof Object && (!isSimple(obj)) && !(obj instanceof ASObject))
		{
			Object bean = readBean(obj); 
			//replace cache with initialized item
			cache.put(obj, bean);
			return bean;
		}

		return obj;
	}


	private boolean isSimple(Object obj)
	{
		return ((obj == null) 
				|| (obj instanceof String) 
				|| (obj instanceof Character) 
				|| (obj instanceof Boolean) 
				|| (obj instanceof Number) 
				|| (obj instanceof Date) 
				|| (obj instanceof Calendar)
				|| (obj instanceof Document));
		
	}
	

	private Object invokeLoad(Object obj)
	{
		try
		{
			List args = new ArrayList();
			if (obj instanceof PersistentCollection)
			{
				List paramArray = remotingMessage.getParameters();

				args.add(Class.forName(obj.getClass().getName()));
				args.add(((PersistentCollection) obj).getKey());
			} else
			{
				List paramArray = remotingMessage.getParameters();

				args.add(Class.forName(obj.getClass().getName()));
				args.add(((IHibernateProxy) obj).getProxyKey());
			}

			remotingMessage.setParameters(args);
		} 
		catch (ClassNotFoundException ex)
		{
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}

		Object result = this.adapter.superInvoke(this.remotingMessage);
		return result;
	}


	private Object readBean(Object obj)
	{
		try
		{
			BeanInfo info = Introspector.getBeanInfo(obj.getClass());
			for (PropertyDescriptor pd : info.getPropertyDescriptors())
			{
				String propName = pd.getName();
				if (!"class".equals(propName) && !"annotations".equals(propName) && !"hibernateLazyInitializer".equals(propName))
				{
					Object val = pd.getReadMethod().invoke(obj, null);
					if (val != null)
					{
						Object newVal = translate(val, pd.getPropertyType());
						try
						{
							Method writeMethod = pd.getWriteMethod();
							if ( writeMethod != null )
							{
								writeMethod.invoke(obj, newVal);	
							}
						}
						catch (IllegalArgumentException e)
						{
							e.printStackTrace();
							throw new RuntimeException(e);
						}
						catch (NullPointerException npe)
						{
							throw npe;
						}
						
					}
				}
			}
		} 
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		return obj;
	}


	private Object readCollection(Object obj, Class type)
	{
		Collection items;
		if (obj instanceof Set)
		{
			items = new HashSet();
		}
		else
		{
			items = new ArrayList();
		}
		Iterator itr = ((Collection) obj).iterator();
		while (itr.hasNext())
		{
			Object o = itr.next();
			Object newVal = translate(o, type);
			items.add(newVal);
		}
		return items;
	}


	private Object readHibernateProxy(Object obj)
	{
		Object newObj = invokeLoad(obj);
		return newObj;
	}


	private Object readPersistanceCollection(Object obj)
	{
		if( !((PersistentCollection) obj).wasInitialized() )
		{
			((PersistentCollection) obj).forceInitialization();
		}
		return translate(obj);
	}

}
