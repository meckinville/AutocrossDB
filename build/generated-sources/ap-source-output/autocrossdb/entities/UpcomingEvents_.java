package autocrossdb.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(UpcomingEvents.class)
public abstract class UpcomingEvents_ {

	public static volatile SingularAttribute<UpcomingEvents, String> upcomingType;
	public static volatile SingularAttribute<UpcomingEvents, String> upcomingRegistration;
	public static volatile SingularAttribute<UpcomingEvents, Integer> upcomingId;
	public static volatile SingularAttribute<UpcomingEvents, Date> upcomingDate;
	public static volatile SingularAttribute<UpcomingEvents, String> upcomingClub;
	public static volatile SingularAttribute<UpcomingEvents, String> upcomingLocation;

}

