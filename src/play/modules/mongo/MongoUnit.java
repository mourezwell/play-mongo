package play.modules.mongo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used by the MongoEnhancer to 
 * signal it to provide implementations for MongoModel
 * methods with specific persistence unit
 * 
 * @author Olivier Mourez
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MongoUnit {

	/**
	 * This value represents the key for database property in configuration
	 * @return
	 */
	String value() default MongoDB.DEFAULT;

}
