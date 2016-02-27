package autocrossdb.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Events.class)
public abstract class Events_ {

	public static volatile CollectionAttribute<Events, Runs> runsCollection;
	public static volatile SingularAttribute<Events, Date> eventDate;
	public static volatile SingularAttribute<Events, String> eventLocation;
	public static volatile SingularAttribute<Events, String> eventPaxWinner;
	public static volatile SingularAttribute<Events, String> eventClubName;
	public static volatile SingularAttribute<Events, String> eventUrl;
	public static volatile SingularAttribute<Events, String> eventRawWinner;
	public static volatile SingularAttribute<Events, String> eventType;

}

