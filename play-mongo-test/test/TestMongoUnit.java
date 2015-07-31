import java.util.List;

import models.Car;
import models.MongoTestEntity;
import models.MongoTestUnit;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;


public class TestMongoUnit extends UnitTest {

	@Before
	public void setUp() throws Exception {
		MongoTestEntity.deleteAll();
		MongoTestUnit.deleteAll();
		Car.deleteAll();
	}

	@Test
	public void testGetUnitName() {
		assertEquals("mongo", Car.getUnitName());
		assertEquals("bis", MongoTestEntity.getUnitName());
		assertEquals("ter", MongoTestUnit.getUnitName());
	}
	
	@Test
	public void testDifferentsDbNamesSameMongoInstance() {
		Car lC = new Car("Citroen", "red", 200);
		lC.save();
		Car lC1 = Car.find("colour = ?1", "red").first();
		assertNotNull(lC1);

		MongoTestEntity lE = new MongoTestEntity();
		lE.save();
		MongoTestEntity lE1 = MongoTestEntity.find().first();
		assertNotNull(lE1);
	}

	@Test
	public void testDifferentsMongoInstancesSameDbName() {
		MongoTestEntity lE = new MongoTestEntity();
		lE.save();
		MongoTestEntity lE1 = MongoTestEntity.find().first();
		assertNotNull(lE1);
		
		MongoTestUnit lU = new MongoTestUnit("toto");
		lU.save();
		List<MongoTestUnit> lUL = MongoTestUnit.find().fetch();
		assertEquals(1, lUL.size());
		assertEquals("toto", lUL.get(0).text);
	}

}
