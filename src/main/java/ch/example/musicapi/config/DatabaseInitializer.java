package ch.example.musicapi.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

  private final DataSource dataSource;

  public DatabaseInitializer(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void run(String... args) throws Exception {
    try (Connection connection = dataSource.getConnection()) {

      boolean tablesExist = allRequiredTablesExist(connection);

      if (!tablesExist) {
        logger.info("Required tables are missing. Running schema.sql and sample-data.sql");

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("sql/schema.sql"));
        populator.addScript(new ClassPathResource("sql/sample-data.sql"));
        populator.execute(dataSource);

        logger.info("Database setup completed successfully.");

      } else if (!hasAlbumData(connection)) {
        logger.info("Tables exist but contain no album data. Running sample-data.sql");

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("sql/sample-data.sql"));
        populator.execute(dataSource);

        logger.info("Sample data inserted successfully.");

      } else {
        logger.info("Database already initialized. No setup needed.");
      }

    } catch (Exception e) {
      logger.error("Database initialization failed", e);
      throw e;
    }
  }

  private boolean allRequiredTablesExist(Connection connection) throws Exception {
    return tableExists(connection, "album")
        && tableExists(connection, "song")
        && tableExists(connection, "role")
        && tableExists(connection, "app_user");
  }

  private boolean tableExists(Connection connection, String tableName) throws Exception {
    DatabaseMetaData metaData = connection.getMetaData();

    try (ResultSet rs =
        metaData.getTables(connection.getCatalog(), null, tableName, new String[] {"TABLE"})) {
      return rs.next();
    }
  }

  private boolean hasAlbumData(Connection connection) throws Exception {
    try (var stmt = connection.createStatement();
        var rs = stmt.executeQuery("SELECT COUNT(*) FROM album")) {
      if (rs.next()) {
        return rs.getInt(1) > 0;
      }
    }
    return false;
  }
}
