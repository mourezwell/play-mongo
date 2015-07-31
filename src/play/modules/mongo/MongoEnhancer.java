package play.modules.mongo;



import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import play.Logger;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.classloading.enhancers.Enhancer;

/**
 * This class uses the Play framework enhancement process to enhance 
 * classes marked with the mongo module annotations.
 * 
 * @author Andrew Louth
 */
public class MongoEnhancer extends Enhancer {

	public static final String PACKAGE_NAME = "play.modules.mongo";
	
	public static final String ENTITY_ANNOTATION_NAME = "play.modules.mongo.MongoEntity";
	public static final String ENTITY_ANNOTATION_VALUE = "value";

	public static final String UNIT_ANNOTATION_NAME = "play.modules.mongo.MongoUnit";
	public static final String UNIT_ANNOTATION_VALUE = "value";

	@Override
	public void enhanceThisClass(ApplicationClass applicationClass) throws Exception {
		
		final CtClass ctClass = makeClass(applicationClass);

		// Enhance MongoEntity annotated classes
        if (hasAnnotation(ctClass, ENTITY_ANNOTATION_NAME)) {
            enhanceMongoEntity(ctClass, applicationClass);
        }
        else {
        	return;
        }
	}
	
	/**
	 * Enhance classes marked with the MongoEntity annotation.
	 * 
	 * @param ctClass
	 * @throws Exception
	 */
	private void enhanceMongoEntity(CtClass ctClass, ApplicationClass applicationClass) throws Exception {
    	// Don't need to fully qualify types when compiling methods below
        classPool.importPackage(PACKAGE_NAME);
		
		String className = ctClass.getName();
        
        // Set the default collection name
        String collectionName = "\"" + ctClass.getSimpleName().toLowerCase() + "\"";
        String unitName = "\"" + MongoDB.DEFAULT + "\"";
        
        AnnotationsAttribute attr = getAnnotations(ctClass);
        Annotation annotation = attr.getAnnotation(ENTITY_ANNOTATION_NAME);
        if (annotation.getMemberValue(ENTITY_ANNOTATION_VALUE) != null){
        	collectionName = annotation.getMemberValue(ENTITY_ANNOTATION_VALUE).toString();
        }
        Annotation annotationUnit = attr.getAnnotation(UNIT_ANNOTATION_NAME);
        if (annotationUnit != null && annotationUnit.getMemberValue(UNIT_ANNOTATION_VALUE) != null){
        	unitName = annotationUnit.getMemberValue(UNIT_ANNOTATION_VALUE).toString();
        }

        Logger.debug( this.getClass().getName() + "-->enhancing MongoEntity-->" + ctClass.getName()+ "-->unit-->" + unitName + "-->collection-->" + collectionName);
        
        // getCollectionName
        CtMethod getCollectionName = CtMethod.make("public static java.lang.String getCollectionName() { return " + collectionName + ";}", ctClass);
        ctClass.addMethod(getCollectionName);

        // getUnitName
        CtMethod getUnitName = CtMethod.make("public static java.lang.String getUnitName() { return " + unitName + ";}", ctClass);
        ctClass.addMethod(getUnitName);

        // create an _id field
        CtField idField = new CtField(classPool.get("org.bson.types.ObjectId"), "_id", ctClass);
        idField.setModifiers(Modifier.PRIVATE);
        ctClass.addField(idField);
        
        //get_id
        CtMethod get_id = CtMethod.make("public org.bson.types.ObjectId get_id() { return _id;}", ctClass);
        ctClass.addMethod(get_id);
        
        // set_id
        CtMethod set_id = CtMethod.make("public void set_id(org.bson.types.ObjectId _id) { this._id = _id;}", ctClass);
        ctClass.addMethod(set_id);
        
        // count
        CtMethod count = CtMethod.make("public static long count() { return MongoDB.count(getUnitName(), getCollectionName());}", ctClass);
        ctClass.addMethod(count);

        // count2
        CtMethod count2 = CtMethod.make("public static long count(java.lang.String query, java.lang.Object[] params) { return MongoDB.count(getUnitName(), getCollectionName(), query, params); }", ctClass);
        ctClass.addMethod(count2);

        // count3
        CtMethod count3 = CtMethod.make("public static long count(com.mongodb.DBObject queryObject) { return MongoDB.count(getUnitName(), getCollectionName(), queryObject); }", ctClass);
        ctClass.addMethod(count3);

        // find        
        CtMethod find = CtMethod.make("public static MongoCursor find(String query, Object[] params){ return MongoDB.find(getUnitName(), getCollectionName(),query,params,"+className+".class); }", ctClass);
        ctClass.addMethod(find);
        
        // find2        
        CtMethod find2 = CtMethod.make("public static MongoCursor find(){ return MongoDB.find(getUnitName(), getCollectionName(),"+className+".class); }", ctClass);
        ctClass.addMethod(find2);
      
        // find3        
        CtMethod find3 = CtMethod.make("public static MongoCursor find(com.mongodb.DBObject queryObject){ return MongoDB.find(getUnitName(), getCollectionName(),queryObject,"+className+".class); }", ctClass);
        ctClass.addMethod(find3);

        // delete        
        CtMethod delete = CtMethod.make("public void delete() { MongoDB.delete(getUnitName(), getCollectionName(), this); }", ctClass);
        ctClass.addMethod(delete);
        
        // delete2        
        CtMethod delete2 = CtMethod.make("public static long delete(String query, Object[] params) { return MongoDB.delete(getUnitName(), getCollectionName(), query, params); }", ctClass);
        ctClass.addMethod(delete2);

        // delete3        
        CtMethod delete3 = CtMethod.make("public static long delete(com.mongodb.DBObject queryObject) { return MongoDB.delete(getUnitName(), getCollectionName(), queryObject); }", ctClass);
        ctClass.addMethod(delete3);

        // deleteAll        
        CtMethod deleteAll = CtMethod.make("public static long deleteAll() { return MongoDB.deleteAll(getUnitName(), getCollectionName()); }", ctClass);
        ctClass.addMethod(deleteAll);
    
        // save     
        CtMethod save = CtMethod.make("public MongoModel save() { return (MongoModel)MongoDB.save(getUnitName(), getCollectionName(), this); }", ctClass);
        ctClass.addMethod(save);
        
        // index
        CtMethod index = CtMethod.make("public static void index(String indexString) { MongoDB.index(getUnitName(), getCollectionName(), indexString); }", ctClass);
        ctClass.addMethod(index);
        
        // dropIndex
        CtMethod dropIndex = CtMethod.make("public static void dropIndex(String indexString) { MongoDB.dropIndex(getUnitName(), getCollectionName(), indexString); }", ctClass);
        ctClass.addMethod(dropIndex);
        
        // dropIndexes
        CtMethod dropIndexes = CtMethod.make("public static void dropIndexes() { MongoDB.dropIndexes(getUnitName(), getCollectionName()); }", ctClass);
        ctClass.addMethod(dropIndexes);
        
        // getIndexes
        CtMethod getIndexes = CtMethod.make("public static String[] getIndexes() { return MongoDB.getIndexes(getUnitName(), getCollectionName()); }", ctClass);
        ctClass.addMethod(getIndexes);
        
        // Done.
        applicationClass.enhancedByteCode = ctClass.toBytecode(); 
        ctClass.detach();
	}
}
