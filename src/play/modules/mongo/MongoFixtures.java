package play.modules.mongo;

import play.Logger;
import play.Play;
import play.classloading.ApplicationClasses;
import play.db.Model;
import play.test.Fixtures;
import play.utils.Java;

import java.util.ArrayList;
import java.util.List;

public class MongoFixtures extends Fixtures {
    
    public static void deleteDatabase() {
        for (Class clz: Play.classloader.getAssignableClasses(MongoModel.class)) {
            dropCollection(clz);
        }
    }
    
    public static void delete(Class<? extends Model> ... types) {
        for (Class<? extends Model> type: types) {
            dropCollection(type);
        }
    }

    private static void dropCollection(Class type) {
        try {
            String collectionName = (String) Java.invokeStatic(type, "getCollectionName");
            String unitName = (String) Java.invokeStatic(type, "getUnitName");
            MongoDB.deleteAll(unitName, collectionName);
        } catch (Exception e) {
            Logger.error("Unable to delete collection for class: %s", type.getSimpleName());
        }
    }
    
    public static void delete(List<Class<? extends Model>> classes) {
        for (Class<? extends Model> type : classes) {
            dropCollection(type);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static void deleteAllModels() {
        List<Class<? extends Model>> mongoClasses = new ArrayList<Class<? extends Model>>();
        for (ApplicationClasses.ApplicationClass c : Play.classes.getAssignableClasses(play.db.Model.class)) {
        	Class<?> jc = c.javaClass;
        	mongoClasses.add((Class<? extends Model>)jc);
        }
        delete(mongoClasses);
    }

}
