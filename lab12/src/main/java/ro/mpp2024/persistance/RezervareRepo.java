package ro.mpp2024.persistance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.model.Rezervare;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class RezervareRepo implements IRepository<Integer, Rezervare> {
    private JdbcUtils dbUtils;

    private ExcursieRepo exRepo;

    private static final Logger logger = LogManager.getLogger();

    public RezervareRepo(Properties props, ExcursieRepo exRepo) {
        logger.info("Initializing RezervareRepo with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
        this.exRepo = exRepo;
    }

    @Override
    public int size() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select count(*) as [SIZE] from Rezervare")) {
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    logger.traceExit(result.getInt("SIZE"));
                    return result.getInt("SIZE");
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        return 0;
    }

    @Override
    public void save(Rezervare entity) {
        logger.traceEntry("saving rezervare {} ", entity);
        try (Connection con = dbUtils.getConnection();
             PreparedStatement preStmt = con.prepareStatement("INSERT INTO Rezervare(excursie, nume_client, numar_telefon, numar_locuri) VALUES (?,?,?,?)")) {
            preStmt.setInt(1, (int) entity.getExcursie());
            preStmt.setString(2, entity.getNumeClient());
            preStmt.setString(3, entity.getNrTelefon());
            preStmt.setInt(4, entity.getNrLocuri());
            int result = preStmt.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public void delete(Integer integer) {
        logger.traceEntry("deleting rezervare with {}", integer);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("delete from Rezervare where id=?")) {
            preStmt.setInt(1, integer);
            int result = preStmt.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public void update(Integer integer, Rezervare entity) {
        logger.traceEntry("update excursei {} ", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = con.prepareStatement("update Rezervare set  excursie = ?, " +
                "nume_client = ?, numar_telefon = ?, numar_locuri = ? where id = ?")) {
            preparedStatement.setInt(1, (int) entity.getExcursie());
            preparedStatement.setString(2, entity.getNumeClient());
            preparedStatement.setString(3, entity.getNrTelefon());
            preparedStatement.setInt(4, entity.getNrLocuri());
            preparedStatement.setInt(5, integer);

            int result = preparedStatement.executeUpdate();
            logger.trace("Updated {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public Rezervare findOne(Integer integer) {
        logger.traceEntry("finding rezervare with id {} ", integer);
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("select * from Rezervare where id=?")) {
            preStmt.setInt(1, integer);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("id");
                    int id_excursie = result.getInt("excursie");
                    String nume_client = result.getString("nume_client");
                    String numar_telefon = result.getString("numar_telefon");
                    int numar_locuri = result.getInt("numar_locuri");
                    Rezervare rez = new Rezervare(id_excursie, nume_client, numar_telefon, numar_locuri);
                    logger.traceExit();
                    return rez;
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit("No rezervare found with id {}", integer);

        return null;
    }

    @Override
    public Iterable<Rezervare> findAll() {
        //logger.();
        Connection con = dbUtils.getConnection();
        List<Rezervare> rezervari = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Rezervare")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    int id_excursie = result.getInt("excursie");
                    String nume_client = result.getString("nume_client");
                    String numar_telefon = result.getString("numar_telefon");
                    int numar_locuri = result.getInt("numar_locuri");
                    Rezervare rez = new Rezervare(id_excursie, nume_client, numar_telefon, numar_locuri);
                    rez.setId((long) id);
                    rezervari.add(rez);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit();
        return rezervari;
    }
}
