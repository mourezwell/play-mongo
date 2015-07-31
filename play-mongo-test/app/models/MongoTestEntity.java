package models;

import java.util.Date;

import play.modules.mongo.MongoEntity;
import play.modules.mongo.MongoModel;
import play.modules.mongo.MongoUnit;

@MongoEntity
@MongoUnit("bis")
public class MongoTestEntity extends MongoModel {
	
	public Boolean testBool;
	public Integer testInt;
	public String testStr;
	public Date testDate;
	
}