package serviceimpl;

import com.mongodb.DB;
import com.mongodb.Mongo;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * An example of a Mongo Integration Test that does proper setup and teardown.
 * Should not have the issue with port collisions.
 * Created by henriktreadup on 9/30/15.
 */
public class EmbedMongoIntegrationTest {
    private static MongodForTestsFactory testsFactory;

    @BeforeClass
    public static void setMongoDB() throws IOException {
        testsFactory = MongodForTestsFactory.with(Version.Main.V2_6);
    }

    @AfterClass
    public static void tearDownMongoDB() throws Exception {
        testsFactory.shutdown();
    }

    private DB db;

    @Before
    public void setUpMongoDB() throws Exception {
        final Mongo mongo = testsFactory.newMongo();
        db = testsFactory.newDB(mongo);
    }

    @Test
    public void testDatabaseCreated() {
        assertNotNull(db);
    }
}