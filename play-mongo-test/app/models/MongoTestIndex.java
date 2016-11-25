package models;

import play.modules.mongo.MongoEntity;
import play.modules.mongo.MongoIndex;
import play.modules.mongo.MongoModel;
import play.modules.mongo.MongoUnit;

/**
 * Test class with index defined in class annotation
 *  
 * @author Olivier Mourez
 */
@MongoEntity(value="mongotestindex", indexes= {
		@MongoIndex(fields="-number", unique=true),
		@MongoIndex(fields="text", sparse=true),
		@MongoIndex(fields="number,-text")
})
public class MongoTestIndex extends MongoModel {
	
	public int number;
	public String text;
	
	public MongoTestIndex(int pNumber, String pText) {
		number = pNumber;
		text = pText;
	}
	
}