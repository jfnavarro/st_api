package serviceimpl;

import com.mongodb.Mongo;
import cz.jirutka.spring.embedmongo.EmbeddedMongoBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * This is a sample integration test that spins up a real MongoDB instance that you
 * can then test against.
 *
 * The purpose of this class is to show a running example of how such an integration
 * test can be written.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EmbeddedMongoIntegrationTest.TestConfiguration.class})
public class EmbeddedMongoIntegrationTest {

    @Test
    public void testGetEmbeddedMongo() {
        assertNotNull(mongo);
    }

    @Test
    public void testGetMongoDbFactory() {
        assertNotNull(mongoDbFactory);
    }

    @Test
    public void testGetMongoConverter() {
        assertNotNull(mongoConverter);
    }

    @Test
    public void testGetGridFsTemplate() {
        assertNotNull(gridFsTemplate);
    }

    @Test
    public void testMonogTemplate() {
        assertNotNull(mongoTemplate);
    }

    @Autowired
    private Mongo mongo;

    @Autowired
    private MongoDbFactory mongoDbFactory;

    @Autowired
    private MongoConverter mongoConverter;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Configuration
    public static class TestConfiguration {

        private final static String DATABASE_NAME = "EmbeddedMongoIntegrationTestDatabase";

        @Bean(destroyMethod="close")
        public Mongo mongo() throws IOException {
            return new EmbeddedMongoBuilder()
                    .version("2.4.5")
                    .bindIp("127.0.0.1")
                    .port(12345)
                    .build();
        }

        @Bean
        public MongoDbFactory getMongoDbFactory() throws IOException {
            return new SimpleMongoDbFactory(mongo(), DATABASE_NAME);
        }

        @Bean
        public MongoConverter getMongoConverter() throws IOException {

            DbRefResolver dbRefResolver = new DefaultDbRefResolver(getMongoDbFactory());
            MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, new MongoMappingContext());
            converter.afterPropertiesSet();
            return converter;
        }

        @Bean
        public GridFsTemplate getGridFsTemplate() throws IOException {
            return new GridFsTemplate(getMongoDbFactory(), getMongoConverter());
        }

        @Bean
        public MongoTemplate getMongoTemplate() throws IOException {
            return new MongoTemplate(mongo(), DATABASE_NAME);
        }
    }
}