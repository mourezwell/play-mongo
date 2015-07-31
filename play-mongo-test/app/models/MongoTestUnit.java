package models;

import play.modules.mongo.MongoEntity;
import play.modules.mongo.MongoModel;
import play.modules.mongo.MongoUnit;

@MongoEntity("mongotestentity")
@MongoUnit("ter")
public class MongoTestUnit extends MongoModel {
	
	public String text;
	public MongoTestUnit(String pText) {
		text = pText;
	}
	
}