import java.util.List;

import models.Car;
import models.MongoTestEntity;
import models.MongoTestIndex;
import models.MongoTestUnit;

import org.junit.Before;
import org.junit.Test;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import play.modules.mongo.MongoDB;
import play.test.UnitTest;


public class TestMongoIndex extends UnitTest {

	@Before
	public void setUp() throws Exception {
		// do not drop collection else index will be lost between app start and test
		MongoTestIndex.delete("number != 0");
	}

	@Test
	public void testCreate() {
		// just to be sure collection is created
		MongoTestIndex one = new MongoTestIndex(1, null);
		one.save();
		MongoTestIndex two = new MongoTestIndex(2, "two");
		two.save();

		// 3 new indexes _id_is by default, name is computed by Mongo
		String[] indexes = MongoTestIndex.getIndexes();
		assertEquals(4, indexes.length);
		assertEquals("_id_", indexes[0]);
		assertEquals("number_-1", indexes[1]);
		assertEquals("text_1", indexes[2]);
		assertEquals("number_1_text_-1", indexes[3]);

		// check options
		DBCollection c = MongoDB.db(one.getUnitName()).getCollection(one.getCollectionName());
		List<DBObject> indexeObjects = c.getIndexInfo();
		for (DBObject iObj : indexeObjects) {
			String iName = (String)iObj.get("name");
			if ("number_-1".equals(iName)) {
				assertEquals(true, iObj.get("unique"));
				assertNull(iObj.get("sparse"));
			}
			else if ("text_1".equals(iName)) {
				assertNull(iObj.get("unique"));
				assertEquals(true, iObj.get("sparse"));
			}
			else if ("number_1_text_-1".equals(iName)) {
				assertNull(iObj.get("unique"));
				assertNull(iObj.get("sparse"));
			}
		}
		
	}
	
}
