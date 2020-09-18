package com.example.springbootreactstream.service;

import com.example.springbootreactstream.model.TestFile;
import com.example.springbootreactstream.repository.TestFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private final TestFileRepository testFileRepository;

//    #2 https://www.baeldung.com/hibernate-entitymanager
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public FileServiceImpl(final TestFileRepository testFileRepository) {
        this.testFileRepository = testFileRepository;
    }

    @Override
    @Transactional
    public void uploadFileStream(final MultipartFile file) throws Exception {
//        #4 https://www.netsurfingzone.com/jpa/how-to-get-jpa-entitymanager-in-spring-boot/
//        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
//        Session session = sessionFactory.openSession();
//        EntityManager em = session.getEntityManagerFactory().createEntityManager();

        //------------------------------
        // Загрузка файла в папку
//        file.transferTo(new File("src/main/resources/" + file.getOriginalFilename()));
        //------------------------------

        //        #3 https://stackoverflow.com/questions/4148231/how-can-i-get-the-session-object-if-i-have-the-entity-manager
        Session session = entityManager.unwrap(Session.class);
        TestFile testFile = new TestFile();
        // #1 https://coderoad.ru/31469136/Spring-data-jpa-%D1%85%D1%80%D0%B0%D0%BD%D0%B5%D0%BD%D0%B8%D0%B5-blob
        Blob data = session.getLobHelper().createBlob(file.getInputStream(), file.getSize());
        testFile.setBytes(data);
        testFileRepository.save(testFile);

        //------------------------------
        // #5 https://stackoverflow.com/questions/53873162/is-it-possible-to-make-drivermanager-setlogintimeoutint-value-accept-timeout-t
//        Properties props = new Properties();
//        props.setProperty("user", user);
//        props.setProperty("password", password);
////        props.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT, "-1");
////        props.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_READ_TIMEOUT, "-1");
//        Connection connection = DriverManager.getConnection(url, props);
//        connection.setAutoCommit(false);
////        Connection connection = DriverManager.getConnection(url, user, password);
//        PreparedStatement ps = connection.prepareStatement(
//                "INSERT INTO TEST_FILE(id, bytes) VALUES (SEQ_TEST_FILE.nextval, ?)");
//
//        // set the value of the input parameter to the input stream
//        ps.setBinaryStream(1, file.getInputStream(), file.getSize());
//        ps.execute();
//        connection.commit();
    }

    /**
     * Потоковое сохранение в Oracle (работает!)
     */
    @Override
    public void uploadFileStream(final InputStream inputStream, final String fileName, final long length) {
        //        #3 https://stackoverflow.com/questions/4148231/how-can-i-get-the-session-object-if-i-have-the-entity-manager
        Session session = entityManager.unwrap(Session.class);
        TestFile testFile = new TestFile();
        // #1 https://coderoad.ru/31469136/Spring-data-jpa-%D1%85%D1%80%D0%B0%D0%BD%D0%B5%D0%BD%D0%B8%D0%B5-blob
        Blob data = session.getLobHelper().createBlob(inputStream, length);
        testFile.setBytes(data);
        testFile.setName(fileName);
        testFileRepository.save(testFile);
    }

    /**
     * Потоковое скачивание из Oracle (работает!)
     * https://stackoverflow.com/questions/57268541/how-to-stream-large-blob-from-database-to-application-using-jpa
     * https://cmsdk.com/java/how-to-stream-large-blob-from-database-to-application-using-jpa.html
     */
    @Override
    public void downloadFileOutputStream(final Long testFileId, final OutputStream outputStream) {
        Session session = entityManager.unwrap(Session.class);
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                PreparedStatement stmt = connection.prepareStatement("SELECT bytes FROM TEST_FILE where id=?");
                stmt.setLong(1, testFileId);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                if (rs != null) {
                    Blob blob = rs.getBlob(1);
                    InputStream input = blob.getBinaryStream();
                    byte[] buffer = new byte[1024];
                    try {
                        while (input.read(buffer) > 0) {
                            String str = new String(buffer, StandardCharsets.UTF_8);
                            outputStream.write(buffer);
                        }
                        input.close();
                    } catch (IOException e) {
                        log.error("Failure in streaming report", e);
                    }
                    rs.close();
                }
            }
        });
    }

    @Override
    public TestFile getTestFile(final Long testFileId) {
        return testFileRepository.findById(testFileId).orElseThrow(RuntimeException::new);
    }
}
