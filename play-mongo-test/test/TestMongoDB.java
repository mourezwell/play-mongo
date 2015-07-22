import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

import models.MongoTestEntity;

public class TestMongoDB extends UnitTest {

	@Before
	public void setUp() throws Exception {
		
		MongoTestEntity.deleteAll();
		
		MongoTestEntity lE1 = new MongoTestEntity();
		lE1.testBool = true;
		lE1.testInt = 1;
		lE1.testStr = "un";
		lE1.save();
		
		MongoTestEntity lE2 = new MongoTestEntity();
		lE2.testBool = false;
		lE2.testInt = 2;
		lE2.testStr = "deux";
		lE2.save();

		MongoTestEntity lE3 = new MongoTestEntity();
		lE3.testBool = null;
		lE3.testInt = 3;
		lE3.testStr = "trois";
		lE3.save();

		MongoTestEntity lE4 = new MongoTestEntity();
		lE4.testBool = true;
		lE4.testInt = null;
		lE4.testStr = "quatre";
		lE4.save();
		
		MongoTestEntity lE5 = new MongoTestEntity();
		lE5.testBool = true;
		lE5.testInt = 5;
		lE5.testStr = null;
		lE5.save();
		
		MongoTestEntity lE6 = new MongoTestEntity();
		lE6.testBool = true;
		lE6.testInt = 6;
		lE6.testStr = "six";
		lE6.testDate = new Date(0);
		lE6.save();

		MongoTestEntity lE7 = new MongoTestEntity();
		lE7.testBool = true;
		lE7.testInt = 7;
		lE7.testStr = "sept";
		lE7.testDate = new Date(1);
		lE7.save();
		
	}
	
	@Test
	public void testEquals() {
		List<MongoTestEntity> lL1 = MongoTestEntity.find("testInt = 1").fetch();
		assertEquals(1, lL1.size());
		assertEquals(new Integer(1), lL1.iterator().next().testInt);
		
		List<MongoTestEntity> lL2 = MongoTestEntity.find("testStr = 'deux'").fetch();
		assertEquals(1, lL2.size());
		assertEquals("deux", lL2.iterator().next().testStr);

		List<MongoTestEntity> lL3 = MongoTestEntity.find("testBool = ?1", true).fetch();
		assertEquals(5, lL3.size());
		assertEquals(true, lL3.iterator().next().testBool);

		List<MongoTestEntity> lL4 = MongoTestEntity.find("testDate = ?1", new Date(0)).fetch();
		assertEquals(1, lL4.size());
		assertEquals(new Date(0), lL4.iterator().next().testDate);
		

	}
	
	@Test
	public void testNotEquals() {
		List<MongoTestEntity> lL1 = MongoTestEntity.find("testInt != 1").fetch();
		assertEquals(6, lL1.size());
		assertEquals(new Integer(2), lL1.iterator().next().testInt);
		
		List<MongoTestEntity> lL2 = MongoTestEntity.find("testStr != 'deux'").fetch();
		assertEquals(6, lL2.size());
		assertEquals("un", lL2.iterator().next().testStr);

		List<MongoTestEntity> lL3 = MongoTestEntity.find("testBool != ?1", true).fetch();
		assertEquals(2, lL3.size());
		assertEquals(false, lL3.iterator().next().testBool);

		List<MongoTestEntity> lL4 = MongoTestEntity.find("testDate != ?1", new Date(0)).fetch();
		assertEquals(6, lL4.size());
		assertEquals(null, lL4.iterator().next().testDate);

		List<MongoTestEntity> lL5 = MongoTestEntity.find("testStr <> 'deux'").fetch();
		assertEquals(6, lL5.size());
		assertEquals("un", lL5.iterator().next().testStr);

		List<MongoTestEntity> lL6 = MongoTestEntity.find("testInt <> 1").fetch();
		assertEquals(6, lL6.size());
		assertEquals(new Integer(2), lL6.iterator().next().testInt);

	}

	
	@Test
	public void testIn() {
		List<MongoTestEntity> lL1 = MongoTestEntity.find("testInt in (1, 2, 3)").fetch();
		assertEquals(3, lL1.size());
		assertEquals(new Integer(1), lL1.iterator().next().testInt);
		
		List<MongoTestEntity> lL2 = MongoTestEntity.find("testStr in ('six', 'sept')").fetch();
		assertEquals(2, lL2.size());
		assertEquals("six", lL2.iterator().next().testStr);

	}


	@Test
	public void testNotIn() {
		List<MongoTestEntity> lL1 = MongoTestEntity.find("testInt not in (1, 2, 3)").fetch();
		assertEquals(4, lL1.size());
		assertEquals(null, lL1.iterator().next().testInt);
		
		List<MongoTestEntity> lL2 = MongoTestEntity.find("testStr not in ('six', 'sept')").fetch();
		assertEquals(5, lL2.size());
		assertEquals("un", lL2.iterator().next().testStr);

	}

	@Test
	public void testGt() {
		List<MongoTestEntity> lL1 = MongoTestEntity.find("testInt > 6").fetch();
		assertEquals(1, lL1.size());
		assertEquals(new Integer(7), lL1.iterator().next().testInt);
		
		List<MongoTestEntity> lL2 = MongoTestEntity.find("testDate > ?1", new Date(0)).fetch();
		assertEquals(1, lL2.size());
		assertEquals(new Date(1), lL2.iterator().next().testDate);

	}

	@Test
	public void testGte() {
		List<MongoTestEntity> lL1 = MongoTestEntity.find("testInt >= 5").fetch();
		assertEquals(3, lL1.size());
		assertEquals(new Integer(5), lL1.iterator().next().testInt);
		
		List<MongoTestEntity> lL2 = MongoTestEntity.find("testDate >= ?1", new Date(0)).fetch();
		assertEquals(2, lL2.size());
		assertEquals(new Date(0), lL2.iterator().next().testDate);

	}

	@Test
	public void testLt() {
		List<MongoTestEntity> lL1 = MongoTestEntity.find("testInt < 3").fetch();
		assertEquals(2, lL1.size());
		assertEquals(new Integer(1), lL1.iterator().next().testInt);
		
		List<MongoTestEntity> lL2 = MongoTestEntity.find("testDate < ?1", new Date(1)).fetch();
		assertEquals(1, lL2.size());
		assertEquals(new Date(0), lL2.iterator().next().testDate);

	}

	@Test
	public void testLte() {
		List<MongoTestEntity> lL1 = MongoTestEntity.find("testInt <= 5").fetch();
		assertEquals(4, lL1.size());
		assertEquals(new Integer(1), lL1.iterator().next().testInt);
		
		List<MongoTestEntity> lL2 = MongoTestEntity.find("testDate <= ?1", new Date(1)).fetch();
		assertEquals(2, lL2.size());
		assertEquals(new Date(0), lL2.iterator().next().testDate);

	}

	@Test
	public void testIsNull() {
		List<MongoTestEntity> lL1 = MongoTestEntity.find("testInt IS null").fetch();
		assertEquals(1, lL1.size());
		assertEquals(null, lL1.iterator().next().testInt);

		List<MongoTestEntity> lL2 = MongoTestEntity.find("testStr is null").fetch();
		assertEquals(1, lL2.size());
		assertEquals(null, lL2.iterator().next().testStr);

		List<MongoTestEntity> lL3 = MongoTestEntity.find("testDate is null").fetch();
		assertEquals(5, lL3.size());
		assertEquals(null, lL3.iterator().next().testDate);
	}

	@Test
	public void testIsNotNull() {
		List<MongoTestEntity> lL1 = MongoTestEntity.find("testInt IS NOT null").fetch();
		assertEquals(6, lL1.size());
		assertEquals(new Integer(1), lL1.iterator().next().testInt);

		List<MongoTestEntity> lL2 = MongoTestEntity.find("testStr is NOT null").fetch();
		assertEquals(6, lL2.size());
		assertEquals("un", lL2.iterator().next().testStr);

		List<MongoTestEntity> lL3 = MongoTestEntity.find("testDate is NOT null").fetch();
		assertEquals(2, lL3.size());
		assertEquals(new Date(0), lL3.iterator().next().testDate);
	}

	@Test
	public void testLike() {
		List<MongoTestEntity> lL1 = MongoTestEntity.find("testStr like 'six'").fetch();
		assertEquals(1, lL1.size());
		assertEquals("six", lL1.iterator().next().testStr);

		List<MongoTestEntity> lL2 = MongoTestEntity.find("testStr like 'se%'").fetch();
		assertEquals(1, lL2.size());
		assertEquals("sept", lL2.iterator().next().testStr);

		List<MongoTestEntity> lL4 = MongoTestEntity.find("testStr like '%ux'").fetch();
		assertEquals(1, lL4.size());
		assertEquals("deux", lL4.iterator().next().testStr);
		
		List<MongoTestEntity> lL3 = MongoTestEntity.find("testStr like '%tr%'").fetch();
		assertEquals(2, lL3.size());
		assertEquals("trois", lL3.iterator().next().testStr);

		List<MongoTestEntity> lL5 = MongoTestEntity.find("testStr like '%u?t%'").fetch();
		assertEquals(1, lL5.size());
		assertEquals("quatre", lL5.iterator().next().testStr);

		List<MongoTestEntity> lL6 = MongoTestEntity.find("testStr like '%u%r%'").fetch();
		assertEquals(1, lL6.size());
		assertEquals("quatre", lL6.iterator().next().testStr);

	}

	@Test
	public void testNotLike() {
		List<MongoTestEntity> lL1 = MongoTestEntity.find("testStr not like 'six'").fetch();
		assertEquals(6, lL1.size());
		assertEquals("un", lL1.iterator().next().testStr);

		List<MongoTestEntity> lL2 = MongoTestEntity.find("testStr not like 'se%'").fetch();
		assertEquals(6, lL2.size());
		assertEquals("un", lL2.iterator().next().testStr);

		List<MongoTestEntity> lL4 = MongoTestEntity.find("testStr not like '%ux'").fetch();
		assertEquals(6, lL4.size());
		assertEquals("un", lL4.iterator().next().testStr);
		
		List<MongoTestEntity> lL3 = MongoTestEntity.find("testStr not like '%tr%'").fetch();
		assertEquals(5, lL3.size());
		assertEquals("un", lL3.iterator().next().testStr);

		List<MongoTestEntity> lL5 = MongoTestEntity.find("testStr not like '%u?t%'").fetch();
		assertEquals(6, lL5.size());
		assertEquals("un", lL5.iterator().next().testStr);

		List<MongoTestEntity> lL6 = MongoTestEntity.find("testStr not like '%u%r%'").fetch();
		assertEquals(6, lL6.size());
		assertEquals("un", lL6.iterator().next().testStr);

	}
	
	
	@Test
	public void testAnd() {
		List<MongoTestEntity> lL1 = MongoTestEntity.find("testStr = 'un' and testInt = 1").fetch();
		assertEquals(1, lL1.size());
		assertEquals("un", lL1.iterator().next().testStr);

		List<MongoTestEntity> lL2 = MongoTestEntity.find("(testStr = 'un' and testInt = 1)").fetch();
		assertEquals(1, lL2.size());
		assertEquals("un", lL2.iterator().next().testStr);

		List<MongoTestEntity> lL4 = MongoTestEntity.find("testStr = 'un' and (testInt = 1)").fetch();
		assertEquals(1, lL4.size());
		assertEquals("un", lL4.iterator().next().testStr);
		
		List<MongoTestEntity> lL3 = MongoTestEntity.find("(testStr = 'un') and (testInt = 1)").fetch();
		assertEquals(1, lL3.size());
		assertEquals("un", lL3.iterator().next().testStr);

		List<MongoTestEntity> lL5 = MongoTestEntity.find("(testStr = 'un') and testInt = 1").fetch();
		assertEquals(1, lL5.size());
		assertEquals("un", lL5.iterator().next().testStr);

		List<MongoTestEntity> lL6 = MongoTestEntity.find("testStr = 'un' and testInt = 1 and testBool = ?1", true).fetch();
		assertEquals(1, lL6.size());
		assertEquals("un", lL6.iterator().next().testStr);
	}

	@Test
	public void testOr() {
		List<MongoTestEntity> lL1 = MongoTestEntity.find("testStr = 'un' or testInt = 2").fetch();
		assertEquals(2, lL1.size());
		assertEquals("un", lL1.iterator().next().testStr);

		List<MongoTestEntity> lL2 = MongoTestEntity.find("(testStr = 'un' or testInt = 2)").fetch();
		assertEquals(2, lL2.size());
		assertEquals("un", lL2.iterator().next().testStr);

		List<MongoTestEntity> lL4 = MongoTestEntity.find("testStr = 'un' or (testInt = 2)").fetch();
		assertEquals(2, lL4.size());
		assertEquals("un", lL4.iterator().next().testStr);
		
		List<MongoTestEntity> lL3 = MongoTestEntity.find("(testStr = 'un') or (testInt = 2)").fetch();
		assertEquals(2, lL3.size());
		assertEquals("un", lL3.iterator().next().testStr);

		List<MongoTestEntity> lL5 = MongoTestEntity.find("(testStr = 'un') or testInt = 2").fetch();
		assertEquals(2, lL5.size());
		assertEquals("un", lL5.iterator().next().testStr);

		List<MongoTestEntity> lL6 = MongoTestEntity.find("testStr = 'un' or testInt = 2 or testBool is null").fetch();
		assertEquals(3, lL6.size());
		assertEquals("un", lL6.iterator().next().testStr);

	}

	@Test
	public void testAndOr() {
		List<MongoTestEntity> lL1 = MongoTestEntity.find("testStr = 'un' and (testInt = 3 or testInt = 1)").fetch();
		assertEquals(1, lL1.size());
		assertEquals("un", lL1.iterator().next().testStr);

		List<MongoTestEntity> lL2 = MongoTestEntity.find("testStr = 'un' or (testInt = 2 and testDate is null)").fetch();
		assertEquals(2, lL2.size());
		assertEquals("un", lL2.iterator().next().testStr);

	}
}
