package play.modules.mongo;

import com.mongodb.BasicDBObject;

/**
 * Object storing all info read in entity class definition (throw @MongoIndex annotation) 
 * when entity is enhanced by play at compileTime, and used at start.
 * 
 * Warning : doesn't work if you are precompling code (production) without starting it in the same run.
 *    
 * @author Olivier Mourez
 */
public class MongoDBIndex {
	
	/** Mongo unit that embed collection */
	String unitName;

	/** Collection that shall be indexed */
	String collectionName;

	/** fields list with syntax "onField1", "on-Field2", "onField1AndField2", or "field1,-field2" */
	String fields;
	
	/** indicate if index shall control key unicity */
	boolean unique = false;
	
	/** indicate if index shall only be applied where key is present  */
	boolean sparse = false;

	public MongoDBIndex(String pUnit, String pCollection, String pFields, boolean pUnique, boolean pSparse) {
		unitName = pUnit;
		collectionName = pCollection;
		fields = pFields;
		unique = pUnique;
		sparse = pSparse;
	}

	public void createIndex() {
		if (sparse || unique) {
			BasicDBObject options = new BasicDBObject();
			if (unique) {
				options.append("unique", true);
			}
			if (sparse) {
				options.append("sparse", true);
			}
    		MongoDB.index(unitName, collectionName, fields, options);
		}
		else {
    		MongoDB.index(unitName, collectionName, fields);
		}
	}
}
