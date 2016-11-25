package play.modules.mongo;

/** 
 * Enable to create Index at start after instrumenting class.
 *
 * Warning : doesn't work if you are precompling code (production) without starting it in the same run.
 *
 * @author Olivier Mourez
 */
public @interface MongoIndex {

	/** fields list with syntax "FielName", "-FieldName", "Field1,-Field2" */
	String fields();
	
	/** indicate if index shall control key unicity */
	boolean unique() default false;
	
	/** indicate if index shall only be applied where key is present  */
	boolean sparse() default false;
	
}
