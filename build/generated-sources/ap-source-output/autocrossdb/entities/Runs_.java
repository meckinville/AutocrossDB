package autocrossdb.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Runs.class)
public abstract class Runs_ {

	public static volatile SingularAttribute<Runs, Classes> runClassName;
	public static volatile SingularAttribute<Runs, String> runDriverName;
	public static volatile SingularAttribute<Runs, Integer> runNumber;
	public static volatile SingularAttribute<Runs, Integer> runId;
	public static volatile SingularAttribute<Runs, String> runCarName;
	public static volatile SingularAttribute<Runs, Double> runTime;
	public static volatile SingularAttribute<Runs, String> runOffcourse;
	public static volatile SingularAttribute<Runs, Integer> runCones;
	public static volatile SingularAttribute<Runs, Double> runPaxTime;
	public static volatile SingularAttribute<Runs, Events> runEventUrl;

}

