package org.dphibernate.persistence.state.testObjects
{
	import org.dphibernate.core.HibernateBean;
	
	

	[Managed]
	[RemoteClass(alias="net.digitalprimates.persistence.hibernate.testObjects.Publisher")]
	public class Publisher extends HibernateBean
	{
		public function Publisher()
		{
		}
		public var name : String;
		public var address : String;
		
		public static function withName(name:String):Publisher
		{
			var publisher:Publisher = new Publisher();
			publisher.name = name;
			return publisher;
		}
	}
}