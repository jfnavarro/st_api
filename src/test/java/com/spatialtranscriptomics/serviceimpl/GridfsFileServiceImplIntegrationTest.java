package com.spatialtranscriptomics.serviceimpl;

import com.mongodb.Mongo;
import com.spatialtranscriptomics.file.File;
import com.spatialtranscriptomics.service.FileService;
import com.spatialtranscriptomics.serviceImpl.GridfsFileServiceImpl;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by henriktreadup on 9/30/15.
 */
public class GridfsFileServiceImplIntegrationTest {

    private static final String TEST_DATA = "ABCDEFGHIJKLMNOPQRSTUVXYZ";
    private static final String FILENAME = "GridfsFileServicecImplIntegrationTestFile.data";

    @Test
    public void testDatabaseCreated() {
        assertNotNull(gridFSService);
    }

    private FileService gridFSService;

    private byte[] getTestFileBytes() {
        return TEST_DATA.getBytes();
    }

    private InputStream getTestFileInputStream() {
        return new ByteArrayInputStream(getTestFileBytes());
    }

    @Test
    public void testCreateGridFsService() {
        assertNotNull(gridFSService);
    }

    @Test
    public void testStoringInputStreamGettingAndDeleting() throws IOException {
        InputStream testInputStream = getTestFileInputStream();

        // Store
        gridFSService.storeFile(testInputStream, FILENAME);

        File file = gridFSService.getFile(FILENAME);

        byte[] actualTestData = IOUtils.toByteArray(file.getInputStream());
        assertArrayEquals(getTestFileBytes(), actualTestData);

        // Delete
        gridFSService.deleteFile(FILENAME);

        // Get non existing file.
        File missingFile = gridFSService.getFile(FILENAME);

        assertNull(missingFile);
    }

    //
    // In this section we create the Mongo instance used for unit testing.
    // We use the MongodForTestsFactory to create the instance.
    //

    private static MongodForTestsFactory testsFactory;

    @BeforeClass
    public static void setMongoDB() throws IOException {
        testsFactory = MongodForTestsFactory.with(Version.Main.V2_6);
    }

    @AfterClass
    public static void tearDownMongoDB() throws Exception {
        testsFactory.shutdown();
    }

    @Before
    public void setUpGridFSService() throws Exception {
       this.gridFSService = getGridfsFileService();
    }

    public Mongo getMongo() throws Exception {
        return testsFactory.newMongo();
    }

    public MongoDbFactory getMongoDbFactory() throws Exception {
        final String databaseName = "integration-test-db-" + UUID.randomUUID().toString();
        return new SimpleMongoDbFactory(getMongo(), databaseName);
    }

    public MongoConverter getMongoConverter() throws Exception {

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(getMongoDbFactory());
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, new MongoMappingContext());
        converter.afterPropertiesSet();
        return converter;
    }

    //
    // Create a GridFsTemplate and the GridFSService that we are going to use.
    //

    public GridFsTemplate getGridFsTemplate() throws Exception {
        return new GridFsTemplate(getMongoDbFactory(), getMongoConverter());
    }

    public FileService getGridfsFileService() throws Exception {
        GridfsFileServiceImpl gridfsFileService = new GridfsFileServiceImpl();
        gridfsFileService.setMongoGridFsTemplate(getGridFsTemplate());

        return gridfsFileService;
    }
}
