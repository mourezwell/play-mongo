package play.modules.mongo;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;

import play.db.Model;

import java.io.Serializable;

/**
 * This class provides the abstract declarations for all MongoModels.
 * Implementations of these declarations are provided by the MongoEnhancer.
 * 
 * @author Andrew Louth
 */
public class MongoModel implements Model, Serializable {
	
    public ObjectId get_id() {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public void set_id(ObjectId _id) {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
	
    public static String getCollectionName() {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }

    public static String getUnitName() {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }

    public static long count() {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static long count(String query, Object... params) {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static long count(DBObject queryObject) {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static MongoCursor find(String query, Object... params) {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }

    public static MongoCursor find(DBObject queryObject) {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }

    public static MongoCursor find() {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public <T extends MongoModel> T save() {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public void delete() {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static long delete(String query, Object... params) {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static long delete(DBObject queryObject) {
        throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }

    public static long deleteAll(){
    	throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static void index(String indexString){
    	throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static void dropIndex(String indexString){
    	throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static void dropIndexes(){
    	throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }
    
    public static String[] getIndexes(){
    	throw new UnsupportedOperationException("Please annotate your model with @MongoEntity annotation.");
    }

    @Override
    public void _save() {
        save();
    }

    @Override
    public void _delete() {
        delete();
    }

    @Override
    public Object _key() {
        return get_id();
    }
}
