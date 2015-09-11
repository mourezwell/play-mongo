package play.modules.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;

import play.Logger;
import play.Play;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;


public class MongoDB {
	
	/** Default unit name */
	protected static final String DEFAULT = "mongo";

	/** Default unit name */
	protected static final String KEY_PREFIX = "mongo_";

	/** Property pattern */
	private static final Pattern DATABASE_PATTERN = Pattern.compile("(mongo_?(.*))\\.database");

	/** Map of mongo persistence units */
	private static Map<String, MongoDBClient> sClients = new HashMap<String, MongoDBClient>();

    /**
     * Obtain a reference to the default mongo database.
     * 
     * @return - a reference to the default Mongo database
     */
	public static DB db() {
		return db(DEFAULT);
	}

    /**
     * Obtain a reference to the mongo database.
     * 
     * @return - a reference to the Mongo database
     */
	public static DB db(String pKey) {
		if (sClients.get(pKey) != null) {
			return sClients.get(pKey).db();
		}
		return null;
	}

	/**
	 * Static initialiser.
	 * 
	 * @throws UnknownHostException in case of bad configuration
	 * @throws MongoException in case of mongo communication error
	 */
	public static void init() throws MongoException, UnknownHostException {

		for (Object lPropName : Play.configuration.keySet()) {
			String lDBProp = (String)lPropName;
			Matcher lMatcher = DATABASE_PATTERN.matcher(lDBProp);
			if (lMatcher.matches()) {
				String lKey = null;
				if (lMatcher.group(2).isEmpty()) {
					lKey = DEFAULT;
				}
				else {
					lKey = lMatcher.group(2);
				}
				String lPropKey = lMatcher.group(1);
				
				String lHost = Play.configuration.getProperty(lPropKey + ".host", "localhost");
				Integer lPort = Integer.parseInt(Play.configuration.getProperty(lPropKey + ".port", "27017"));
				String lDbName = Play.configuration.getProperty(lPropKey + ".database");
				String lUserName = Play.configuration.getProperty(lPropKey + ".username");
				String lPassword = Play.configuration.getProperty(lPropKey + ".password");
				
				Logger.info("Connecting to mongo [" + lKey + "] DB ["+lHost+"]["+lPort+"]["+lDbName+"]");
				MongoDBClient lClient = new MongoDBClient(lHost, lPort, lDbName, lUserName, lPassword);
				sClients.put(lKey, lClient);
			}
		}
	}
	
	
	/**
	 * Creates an index.
	 * 
	 * @param collectionName
	 * @param indexString
	 */
	public static void index(String unitName, String collectionName, String indexString) {
		DBCollection c = db(unitName).getCollection(collectionName);
		DBObject indexKeys = createOrderDbObject(indexString);
		c.createIndex(indexKeys);
	}
	
	/**
	 * Removes an index. 
	 * 
	 * @param collectionName
	 * @param indexString
	 */
	public static void dropIndex(String unitName, String collectionName, String indexString) {
		DBCollection c = db(unitName).getCollection(collectionName);
		DBObject indexKeys = createOrderDbObject(indexString);
		c.dropIndex(indexKeys);
	}
	
	/** 
	 * Removes all indexes.
	 * 
	 * @param collectionName
	 */
	public static void dropIndexes(String unitName, String collectionName) {
		DBCollection c = db(unitName).getCollection(collectionName);
		c.dropIndexes();
	}
	
	/**
	 * Return a list of index names.
	 * 
	 * @param collectionName
	 * @return
	 */
	public static String[] getIndexes(String unitName, String collectionName) {
		List<String> indexNames = new ArrayList<String>();
		DBCollection c = db(unitName).getCollection(collectionName);
		List<DBObject> indexes = c.getIndexInfo();
		for (DBObject o : indexes) {
			indexNames.add((String)o.get("name"));
		}
		
		return indexNames.toArray(new String[indexNames.size()]);
	}
	
	/**
	 * Counts the records in the collection.
	 * 
	 * @param collectionName
	 * @return - number of records in the collection
	 */
	public static long count(String unitName, String collectionName) {
		return db(unitName).getCollection(collectionName).getCount();
	}
	
	/**
	 * Counts the records in the collection matching the query string.
	 * 
	 * @param collectionName - the queried collection
	 * @param query - the query string
	 * @param params - parameters for the query string
	 * @return
	 */
	public static long count(String unitName, String collectionName, String query, Object[] params) {
		return db(unitName).getCollection(collectionName).getCount(createQueryDbObject(query, params));
	}

	/**
	 * Counts the records in the collection matching the query object
	 * 
	 * @param collectionName - the queried collection
	 * @param queryObject - the query object
	 * @return
	 */
	public static long count(String unitName, String collectionName, DBObject queryObject) {
		return db(unitName).getCollection(collectionName).getCount(queryObject);
	}

	/**
	 * Provides a cursor to the objects in a collection, matching the query string.
	 * 
	 * @param collectionName - the target collection
	 * @param query - the query string
	 * @param params - parameters for the query
	 * @param clazz - the type of MongoModel
	 * @return - a mongo cursor
	 */
	@SuppressWarnings("rawtypes")
	public static MongoCursor find(String unitName, String collectionName, String query, Object[] params, Class clazz) {
		return new MongoCursor(db(unitName).getCollection(collectionName).find(createQueryDbObject(query, params)),clazz);
	}
	
	/**
	 * Provides a cursor to the objects in a collection.
	 *
	 * @param collectionName - the target collection
	 * @param clazz - the type of MongoModel
	 * @return - a mongo cursor
	 */
	@SuppressWarnings("rawtypes") 
	public static MongoCursor find(String unitName, String collectionName, Class clazz) {
		return new MongoCursor(db(unitName).getCollection(collectionName).find(), clazz);
	}

	/**
	 * Provides a cursor to the objects in a collection, matching the query object
	 * 
	 * @param collectionName - the target collection
	 * @param queryObject - the query object 
	 * @param clazz - the type of MongoModel
	 * @return - a mongo cursor
	 */
	public static MongoCursor find(String unitName, String collectionName, DBObject queryObject, Class clazz) {
		return new MongoCursor(db(unitName).getCollection(collectionName).find(queryObject), clazz);
	}

    public static <T extends MongoModel> T findById(String unitName, String collectionName, Class clazz, ObjectId id) {
        DBObject query = new BasicDBObject("_id", id);
        DBObject dbObject = db(unitName).getCollection(collectionName).findOne(query);
        if (dbObject != null) {
            Map map = dbObject.toMap();
            return (T) MongoMapper.convertValue(map, clazz);
        } else {
            return null;
        }
    }


	/**
	 * Saves a model to its collection.
	 * @param <T> - the type of MongoModel to save
	 * @param collectionName - the collection to save it to
	 * @param model - the model to save
	 * @return - an instance of the model saved
	 */
	public static <T extends MongoModel> T save(String unitName, String collectionName, T model) {
		/* 
		 * Perhaps it would be better to immediately save the object to the database and assign its id. 
		 * 
		 */
		DBObject dbObject = new BasicDBObject(MongoMapper.convertValue(model, Map.class));
		
		if (model.get_id() == null){
			db(unitName).getCollection(collectionName).insert(dbObject);
			model.set_id((ObjectId)(dbObject.get("_id")));
		}
		else{
			dbObject.removeField("_id");
			db(unitName).getCollection(collectionName).update(new BasicDBObject("_id",model.get_id()), dbObject);
		}
		
		return model;
	}
	
	/**
	 * Deletes a model from a collection.
	 * 
	 * @param <T> - the type of model
	 * @param collectionName - the collection
	 * @param model - the model
	 */
	public static <T extends MongoModel> void delete (String unitName, String collectionName, T model) {
		DBObject dbObject = new BasicDBObject("_id", model.get_id());
		db(unitName).getCollection(collectionName).remove(dbObject);
	}
	
	/**
	 * Deletes models from a collection that match a specific query string
	 * 
	 * @param collectionName - the collection 
	 * @param query - the query string
	 * @param params - parameters for the query string
	 * @return - the number of models deleted
	 */
	public static long delete(String unitName, String collectionName, String query, Object[] params) {
		DBObject dbObject = createQueryDbObject(query, params);
		long deleteCount = db(unitName).getCollection(collectionName).getCount(dbObject);
		db(unitName).getCollection(collectionName).remove(dbObject);
		
		return deleteCount;
	}

	/**
	 * Deletes models from a collection that match a specific query object
	 * 
	 * @param collectionName - the collection 
	 * @param queryObject - the query object
	 * @return - the number of models deleted
	 */
	public static long delete(String unitName, String collectionName, DBObject queryObject) {
		long deleteCount = db(unitName).getCollection(collectionName).getCount(queryObject);
		db(unitName).getCollection(collectionName).remove(queryObject);
		return deleteCount;
	}

	/**
	 * Deletes all models from the collection.
	 * 
	 * @param collectionName - the collection
	 * @return - the number of models deleted
	 */
	public static long deleteAll(String unitName, String collectionName) {
		long deleteCount = count(unitName, collectionName);
		db(unitName).getCollection(collectionName).drop();
		return deleteCount;
	}
	
	/**
	 * Creates a query object for use with other methods
	 * 
	 * @param query - the query string
	 * @param values - values for the query
	 * @return - a DBObject representing the query
	 */
	public static DBObject createQueryDbObject(String query, Object[] values) {
		
		DBObject object = null;

		if (query.startsWith("by")) {
	    	String keys = extractKeys(query);
	    	
			object = new BasicDBObject(); 	
	    	String [] keyList = keys.split(",");
	    	if (keyList.length > values.length){
	    		throw new IllegalArgumentException("Not enough values for the keys provided");
	    	}
			for (int i = 0; i < keyList.length; i++){
				object.put(keyList[i].trim(), values[i]);
			}
		}
		else {
			object = parseQuery(query, values);
		}
		
    	return object;
    }
	
	/**
	 * Creates an ordering object for use with other methods
	 * 
	 * @param query - the query string
	 * @return - a DBObject representing the ordering
	 */
	public static DBObject createOrderDbObject(String query) {
		
		String keys = extractKeys(query);
		
    	DBObject object = new BasicDBObject(); 	
    	String [] keyList = keys.split(",");
    	
		for (int i = 0; i < keyList.length; i++){
			
			int value = 1;
			if (keyList[i].charAt(0) == '-'){
				value = -1;
				keyList[i] = keyList[i].substring(1);
			}
			
			object.put(keyList[i].trim(), value);
		}  
    	
    	return object;
    }
	
	/**
	 * Extracts parameter names from a query string
	 * 
	 * @param queryString - the query string
	 * @return - a comma seperated string of parameter names
	 */
	private static String extractKeys(String queryString) {
		queryString = queryString.substring(2);
		List<String> keys = new ArrayList<String>();
        String[] parts = queryString.split("And");
        for (String part : parts){
        	if (part.charAt(0) == '-'){
        		keys.add((part.charAt(0) + "") + (part.charAt(1) + "").toLowerCase() + part.substring(2));
        	}
        	else{
        		keys.add((part.charAt(0) + "").toLowerCase() + part.substring(1));
        	}
        }
        return StringUtils.join(keys.toArray(), ",");
	}

	/**
	 * A class to link a list of conditions and the logic applied 
	 * (required if more than one condition in the list)   
	 */
	private static class LogicalBlock {
		private BasicDBList conds = new BasicDBList();
		private String logic;
	}
	
	/**
	 * Extracts parameter names from a query string
	 * 
	 * @param queryString - the query string
	 * @return The BasicDBObject representing the query in Java Mongo DB format
	 */
	private static DBObject parseQuery(String queryString, Object[] values) {
		
		LinkedList<LogicalBlock> lExprQueue = new LinkedList<LogicalBlock>();
		lExprQueue.add(new LogicalBlock());
		
		String lLastKey = null;
		String lLastOperator = null;
		Object lLastValue = null;
		boolean lLastNegative = false;
		
		String lParsedQuery = queryString;
		
        while (lParsedQuery.length() > 0) {
    		if (lLastKey == null) {
        		// space
    			while (lParsedQuery.startsWith(" ")) {
    				lParsedQuery = lParsedQuery.substring(1);
    			}
            	if (lParsedQuery.toUpperCase().startsWith("OR")) {
    				if (lExprQueue.getLast().logic == null) {
    					lExprQueue.getLast().logic = "$or";
    				}
    				else if (!lExprQueue.getLast().logic.equals("$or")) {
    		        	throw new IllegalArgumentException("Brackets needded to mix AND and OR operators at " + lParsedQuery + ", in " + queryString);
    				}
    				lParsedQuery = lParsedQuery.substring(2);
    			}
            	else if (lParsedQuery.toUpperCase().startsWith("AND")) {
    				if (lExprQueue.getLast().logic == null) {
    					lExprQueue.getLast().logic = "$and";
    				}
    				else if (!lExprQueue.getLast().logic.equals("$and")) {
    		        	throw new IllegalArgumentException("Brackets needded to mix AND and OR operators at " + lParsedQuery + ", in " + queryString);
    				}
    				lParsedQuery = lParsedQuery.substring(3);
            	}
            	else if (lParsedQuery.startsWith("(")) { // logical parenthesis
        			lExprQueue.add(new LogicalBlock());
        			lParsedQuery = lParsedQuery.substring(1);
        		}
            	else {
                	int lFirstSpace = lParsedQuery.indexOf(' ');
            		if (lFirstSpace > 0) {
            			lLastKey = lParsedQuery.substring(0, lFirstSpace);
            			lParsedQuery = lParsedQuery.substring(lFirstSpace);
            		}
            		else {
            			throw new IllegalArgumentException("Missing space after key it at " + lParsedQuery + " , in " + queryString);
            		}
            	}
    		}
    		else if (lLastOperator == null) {
    			String part = null;
        		int lFirstSpace = lParsedQuery.indexOf(' ');
        		if (lFirstSpace > 0) {
        			part = lParsedQuery.substring(0, lFirstSpace);
        		}
        		else {
        			throw new IllegalArgumentException("Missing space after operator at " + lParsedQuery + ", in " + queryString);
        		}
    			
    			if (part.equals("=") || part.equals("==")) {
    				lLastOperator = "";  // default operator
    			}
    			else if (part.equals("!=") || part.equals("<>")) {
    				lLastOperator = "$ne";
    			}
    			else if (part.equals(">")) {
    				lLastOperator = "$gt";
    			}
    			else if (part.equals(">=")) {
    				lLastOperator = "$gte";
    			}
    			else if (part.equals("<")) {
    				lLastOperator = "$lt";
    			}
    			else if (part.equals("<=")) {
    				lLastOperator = "$lte";
    			}
    			else if (part.equalsIgnoreCase("LIKE")) {
    				lLastOperator = "$regex";
    			}
    			else if (part.equalsIgnoreCase("IN")) {
    				lLastOperator = "$in";
    			}
    			else if (part.equalsIgnoreCase("IS")) {
    				lLastOperator = "IS";
    			}
    			else if (part.equalsIgnoreCase("NOT")) {
    				lLastOperator = "NOT";
    			}
    			else {
    				throw new IllegalArgumentException("Unsupported operator at " + lParsedQuery + ", in " + queryString);
    			}
    			lParsedQuery = lParsedQuery.substring(lFirstSpace);
    		}
    		else if (lLastOperator.equals("IS")) {
    			if (lParsedQuery.toUpperCase().startsWith("NULL")) { 
    				lLastOperator = ""; // default operator 
    				lLastValue = ""; // default value => change into null when building object
    				lParsedQuery = lParsedQuery.substring(4);
    			}
    			else if (lParsedQuery.toUpperCase().startsWith("NOT")) {
    				lLastOperator = "ISNOT";
    				lParsedQuery = lParsedQuery.substring(3);
    			}
    			else {
    				throw new IllegalArgumentException("Unsupported operator after IS at " + lParsedQuery + ", in " + queryString);
    			}
    		}
    		else if (lLastOperator.equals("NOT")) {
    			if (lParsedQuery.toUpperCase().startsWith("LIKE")) { // native mongo operator
    				lLastOperator = "$regex";
    				lLastNegative = true;
    				lParsedQuery = lParsedQuery.substring(4);
    			}
    			else if (lParsedQuery.toUpperCase().startsWith("IN")) {
    				lLastOperator = "$nin";
    				lParsedQuery = lParsedQuery.substring(2);
    			}
    			else {
    				throw new IllegalArgumentException("Unsupported operator after NOT at " + lParsedQuery + ", in " + queryString);
    			}
    		}
    		else if (lLastOperator.equals("ISNOT")) {
    			if (lParsedQuery.toUpperCase().startsWith("NULL")) { 
    				lLastOperator = "$ne";
    				lLastValue = ""; // default value => change into null when building object
        			lParsedQuery = lParsedQuery.substring(4);
    			}
    			else {
    				throw new IllegalArgumentException("Unsupported operator after IS NOT at " + lParsedQuery + ", in " + queryString);
    			}
    		}
    		else if (lLastValue == null) {
    			if (lParsedQuery.startsWith("?")) { //JPA like param numbering
        			int nb = 0;
        			lParsedQuery = lParsedQuery.substring(1);
        			String nbStr = lParsedQuery;
        			
					int lSp = lParsedQuery.indexOf(' ');
					int lEnd = lParsedQuery.indexOf(')');
					int lMin = -1;
					if (lSp >= 0) lMin = lSp;
					if (lEnd >= 0 && (lEnd < lMin || lMin < 0)) lMin = lEnd;
					try {
						if (lMin >= 0) {
							nbStr = nbStr.substring(0, lMin);
						}
						nb = Integer.parseInt(nbStr);
					}
					catch (NumberFormatException nfe) {
						throw new IllegalArgumentException("Unsupported parameter number at  " + lParsedQuery + ", in " + queryString);
					}
					if (nb > values.length) {
        				throw new IllegalArgumentException("Missing parameter number at " + lParsedQuery + ", in " + queryString);
					}
					lLastValue = values[nb - 1];
					lParsedQuery = lParsedQuery.substring(String.valueOf(nb).length());
    			}
    			else if (lParsedQuery.startsWith("(")) { // list
    				lLastValue = new ArrayList();
    				lParsedQuery = lParsedQuery.substring(1);
    				while (!lParsedQuery.isEmpty()) {
    					if (lParsedQuery.startsWith(")")) { // end
							lParsedQuery = lParsedQuery.substring(1);
							break;
    					}
    					else if (lParsedQuery.startsWith(",")) { // next
							lParsedQuery = lParsedQuery.substring(1);
    					}
    					else if (lParsedQuery.startsWith(" ")) { // space
							lParsedQuery = lParsedQuery.substring(1);
    					}
    					else if (lParsedQuery.startsWith("'")) { //chain 
    	    				lParsedQuery = lParsedQuery.substring(1);
    						char lLastChar = 0;
    						StringBuilder lSb = new StringBuilder();
    						while (!lParsedQuery.isEmpty()) {
    							char lCurrChar = lParsedQuery.charAt(0);
    							lParsedQuery = lParsedQuery.substring(1);
    							if (lCurrChar == '\'' && lLastChar != '\\') {
        							lLastChar = lCurrChar;
    								break;
    							}
    							else {
        							lLastChar = lCurrChar;
    								lSb.append(lCurrChar);
    							}
    						}
    						if (lLastChar == '\'') {
        						((List)lLastValue).add(lSb.toString());
    						}
    						else {
    							throw new IllegalArgumentException("Unsupported list string value at  " + lParsedQuery + ", in " + queryString);
    						}
    					}
    					else { // number
    						int lSp = lParsedQuery.indexOf(' ');
    						int lNext = lParsedQuery.indexOf(',');
    						int lEnd = lParsedQuery.indexOf(')');
    						int lMin = -1;
    						if (lSp >= 0) lMin = lSp;
    						if (lNext >= 0 && (lNext < lMin  || lMin < 0)) lMin = lNext;
    						if (lEnd >= 0 && (lEnd < lMin || lMin < 0)) lMin = lEnd;
    						try {
    							String lNumberStr = lParsedQuery;
    							if (lMin >= 0) {
    								lNumberStr = lNumberStr.substring(0, lMin);
    							}
    							((List)lLastValue).add(Double.valueOf(lNumberStr));
    						}
    						catch (NumberFormatException nfe) {
    							throw new IllegalArgumentException("Unsupported list number value at  " + lParsedQuery + ", in " + queryString);
    						}
    						if (lMin >= 0) {
    							lParsedQuery = lParsedQuery.substring(lMin);
    						}
    						else {
    							lParsedQuery = "";
    						}
    					}
    				}
    			}
    			else { // single/simple type
    				if (lParsedQuery.startsWith("'")) { //chain 
	    				lParsedQuery = lParsedQuery.substring(1);
						char lLastChar = 0;
						StringBuilder lSb = new StringBuilder();
						while (!lParsedQuery.isEmpty()) {
							char lCurrChar = lParsedQuery.charAt(0);
							lParsedQuery = lParsedQuery.substring(1);
							if (lCurrChar == '\'' && lLastChar != '\\') {
    							lLastChar = lCurrChar;
								break;
							}
							else {
    							lLastChar = lCurrChar;
								lSb.append(lCurrChar);
							}
						}
						if (lLastChar == '\'') {
							lLastValue = lSb.toString();
						}
						else {
							throw new IllegalArgumentException("Unsupported string value at  " + lParsedQuery + ", in " + queryString);
						}

    				}
    				else { // number
						int lSp = lParsedQuery.indexOf(' ');
						int lEnd = lParsedQuery.indexOf(')');
						int lMin = -1;
						if (lSp >= 0) lMin = lSp;
						if (lEnd >= 0 && (lEnd < lMin || lMin < 0)) lMin = lEnd;
						try {
							String lNumberStr = lParsedQuery;
							if (lMin >= 0) {
								lNumberStr = lNumberStr.substring(0, lMin);
							}
							lLastValue = Double.valueOf(lNumberStr);
						}
						catch (NumberFormatException nfe) {
							throw new IllegalArgumentException("Unsupported number value at  " + lParsedQuery + ", in " + queryString);
						}
						if (lMin >= 0) {
							lParsedQuery = lParsedQuery.substring(lMin);
						}
						else {
							lParsedQuery = "";
						}
    				}
    			}
    		}
    		
    		if (lLastValue != null) {
    			// on a tout pour construire une condition
    			BasicDBObject lCond = new BasicDBObject();
    			if (lLastOperator.equals("") || lLastOperator.equals("$eq"))  { // default operator
    				if (lLastValue.equals("")) {
    					lCond.put(lLastKey, null); // x is null
    				}
    				else {
    					lCond.put(lLastKey, lLastValue); // x = y
    				}
    			}
    			else if (lLastOperator.equals("$ne"))  {
    				if (lLastValue.equals("")) {
    					lCond.put(lLastKey, new BasicDBObject("$ne", null)); // x is not null
    				}
    				else {
    					lCond.put(lLastKey, new BasicDBObject("$ne", lLastValue)); // x != y
    				}
    			}
    			else if (lLastOperator.equals("$regex"))  { 
    				// transform sql syntax to Pattern
    				String lExpr = (String)lLastValue;
    				if (lExpr.startsWith("%")) {
    					lExpr = ".*" + lExpr.substring(1);
    				}
    				else {
    					lExpr = "^" + lExpr;
    				}
    				if (lExpr.endsWith("%")) {
    					lExpr = lExpr.substring(0, lExpr.length()-1) + ".*";
    				}
    				else {
    					lExpr = lExpr + "$";
    				}
    				// inside chars
    				lExpr = lExpr.replace('?', '.');
					lExpr = lExpr.replaceAll("%", ".*");
    				if (lLastNegative) {
    					lCond.put(lLastKey, new BasicDBObject("$not", Pattern.compile(lExpr))); // x not like y
    				}
    				else {
    					lCond.put(lLastKey,  Pattern.compile(lExpr)); // x like y
    				}
    			}
    			else if (lLastOperator.equals("$in") || lLastOperator.equals("$nin"))  {
    				BasicDBList inList = new BasicDBList();
    				inList.addAll((List)lLastValue);
					lCond.put(lLastKey, new BasicDBObject(lLastOperator, inList)); // x not in (y, z)
    			}
    			else { // default case
					lCond.put(lLastKey, new BasicDBObject(lLastOperator, lLastValue)); // x ? z
    			}
    			// add condition to the list of current logical block 
    			lExprQueue.getLast().conds.add(lCond);
    			
        		lLastKey = null;
				lLastOperator = null;
				lLastValue = null;
				lLastNegative = false;
    		}
    		// space
			while (lParsedQuery.startsWith(" ")) {
				lParsedQuery = lParsedQuery.substring(1);
			}
			// closing logical block
        	if (lParsedQuery.startsWith(")")) {
        		// requires at least two blocks : current and parent
        		if (lExprQueue.size() <= 1) {
		        	throw new IllegalArgumentException("Missing brackets at " + lParsedQuery + ", in " + queryString);
        		}
        		LogicalBlock lClosedBlock = lExprQueue.pollLast();
        		LogicalBlock lParentBlock = lExprQueue.getLast();
        		if (lClosedBlock.logic == null) {
        			if (lClosedBlock.conds.size() == 1) {
            			lParentBlock.conds.add(lClosedBlock.conds.iterator().next());
        			}
        			else {
    		        	throw new IllegalArgumentException("Missing logic at " + lParsedQuery + ", in " + queryString);
        			}
        		}
        		else {
        			// encapsulate condidtion and logic in parent
        			lParentBlock.conds.add(new BasicDBObject(lClosedBlock.logic, lClosedBlock.conds));
        		}
				lParsedQuery = lParsedQuery.substring(1);
        	}
        	// space
			while (lParsedQuery.startsWith(" ")) {
				lParsedQuery = lParsedQuery.substring(1);
			}
        }
        
        if (lExprQueue.size() != 1) {
        	throw new IllegalArgumentException("Not enough closing brackets in global logic in " + queryString);
        }
        LogicalBlock lGlobalExpr = lExprQueue.pollLast();
        
        if (lGlobalExpr.logic == null) {
        	// requires only one condition in the block
        	if (lGlobalExpr.conds.size() == 1) {
            	return (BasicDBObject)lGlobalExpr.conds.iterator().next();
        	}
        	else {
            	throw new IllegalArgumentException("Missing logical operators in global logic in" + queryString);
        	}
        }
        else {
        	return new BasicDBObject(lGlobalExpr.logic, lGlobalExpr.conds);
        }
	}

	
} 
