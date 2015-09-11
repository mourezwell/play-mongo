package play.modules.mongo;

import org.bson.types.ObjectId;

import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.db.Model;
import play.exceptions.DatabaseException;
import play.utils.Java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The plugin for the Mongo module.
 * 
 * @author Andrew Louth
 * @author Niko Schmuck
 */
public class MongoPlugin extends PlayPlugin {
	
	private MongoEnhancer enhancer = new MongoEnhancer();

	@Override
	public void enhance(ApplicationClass applicationClass) throws Exception {
		enhancer.enhanceThisClass(applicationClass);
	}

    @Override
    public void onApplicationStart() {
    	try {
    		MongoDB.init();
    	}
    	catch (Exception e) {
    		throw new DatabaseException("Unable to connect Mongo DB", e);
    	}
    }

    /*
    @Override
    @SuppressWarnings("unchecked")
    public Object bind(String name, @SuppressWarnings("rawtypes") Class clazz,
                       java.lang.reflect.Type type, Annotation[] annotations,
                       Map<String, String[]> params) {
        if (MongoModel.class.isAssignableFrom(clazz)) {
            String keyName = modelFactory(clazz).keyName();
            String idKey = name + "." + keyName;
            if (params.containsKey(idKey) && params.get(idKey).length > 0
                    && params.get(idKey)[0] != null
                    && params.get(idKey)[0].trim().length() > 0) {
                String id = params.get(idKey)[0];
                try {
                    Object o = ds().createQuery(clazz).filter(keyName, new ObjectId(id)).get();
                    return MongoModel.edit(o, name, params, annotations);
                } catch (Exception e) {
                    return null;
                }
            }
            return MongoModel.create(clazz, name, params, annotations);
        }
        return super.bind(name, clazz, type, annotations, params);
    }
    */

    @SuppressWarnings("unchecked")
    @Override
    public MongoModel.Factory modelFactory(Class<? extends play.db.Model> modelClass) {
        if (MongoModel.class.isAssignableFrom(modelClass)
                && modelClass.isAnnotationPresent(MongoEntity.class)) {
            return MongoLoader.getFactory((Class<? extends MongoModel>) modelClass);
        }
        return null;
    }

    public static class MongoLoader implements MongoModel.Factory {

        private static Map<Class<? extends Model>, Model.Factory> m_ = new HashMap<Class<? extends Model>, Model.Factory>();

        private Class<? extends Model> clazz;
        private String collectionName;
        private String unitName;

        private MongoLoader(Class<? extends Model> clazz) {
            this.clazz = clazz;
            this.collectionName = getCollectionName();
            this.unitName = getUnitName();
            m_.put(clazz, this);
        }

        private String getCollectionName() {
            try {
                return (String) Java.invokeStatic(clazz, "getCollectionName");
            } catch (Exception e) {
                throw new RuntimeException("Unable to get collection name for " + clazz, e);
            }
        }

        private String getUnitName() {
            try {
                return (String) Java.invokeStatic(clazz, "getUnitName");
            } catch (Exception e) {
                throw new RuntimeException("Unable to get unit name for " + clazz, e);
            }
        }

        public static Model.Factory getFactory(Class<? extends MongoModel> modelClass) {
            synchronized (m_) {
                Model.Factory factory = m_.get(modelClass);
                if (factory == null) {
                    factory = new MongoLoader(modelClass);
                }
                return factory;
            }
        }

        @Override
        public String keyName() {
            return "_id";
        }

        @Override
        public Class<?> keyType() {
            return ObjectId.class;
        }

        @Override
        public Object keyValue(Model m) {
            return m._key();
        }

        @Override
        public Model findById(Object id) {
            return MongoDB.findById(unitName, collectionName, clazz, (ObjectId) id);
        }

        @Override
        public List<Model> fetch(int offset, int length, String orderBy, String orderDirection, List<String> properties, String keywords, String where) {
            throw new UnsupportedOperationException("fetch not yet suppored");
        }

        @Override
        public Long count(List<String> properties, String keywords, String where) {
            // return MongoDB.count(getCollectionName(), where, properties.toArray());
            throw new UnsupportedOperationException("count not yet suppored");
        }

        @Override
        public void deleteAll() {
            MongoDB.deleteAll(unitName, collectionName);
        }

        @Override
        public List<Model.Property> listProperties() {
            throw new UnsupportedOperationException("listProperties not yet suppored");
        }

    }

}
