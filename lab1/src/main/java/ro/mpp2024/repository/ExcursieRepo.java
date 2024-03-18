package ro.mpp2024.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.domain.Excursie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class ExcursieRepo implements IExcursieRepo{
    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    public ExcursieRepo(Properties props){
        logger.info("Initializing ExcursieRepo with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public int size() {
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("select count(*) as [SIZE] from Excursie")) {
            try(ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    logger.traceExit(result.getInt("SIZE"));
                    return result.getInt("SIZE");
                }
            }
        }catch(SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        return 0;
    }

    @Override
    public void save(Excursie entity) {
        logger.traceEntry("saving excursie {} ",entity);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("insert into Excursie values (?,?,?,?,?,?)")){
            preStmt.setInt(1,   entity.getId().intValue());
            preStmt.setString(2,entity.getObiectivTuristic());
            preStmt.setString(3,entity.getNumeTransport());
            preStmt.setTime(4,java.sql.Time.valueOf(entity.getOraPlecare()));
            preStmt.setInt(5,entity.getPret());
            preStmt.setInt(6,entity.getNrLocuri());
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit();

    }

    @Override
    public void delete(Integer integer) {
        logger.traceEntry("deleting excursie with {}",integer);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("delete from Excursie where id=?")){
            preStmt.setInt(1,integer);
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit();
    }

    @Override
    public void update(Integer integer, Excursie entity) {
        //To do
        logger.traceEntry("update excursei {} ", entity);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preparedStatement=con.prepareStatement("update Excursie set  obiectiv = ?, " +
                "firma_transport = ?, ora_plecare = ?, pret = ?, numar_locuri = ? where id = ?")){
            preparedStatement.setString(1, entity.getObiectivTuristic());
            preparedStatement.setString(2, entity.getNumeTransport());
            preparedStatement.setTime(3, java.sql.Time.valueOf(entity.getOraPlecare()));
            preparedStatement.setInt(4, entity.getPret());
            preparedStatement.setInt(5, entity.getNrLocuri());
            preparedStatement.setInt(6, integer);

            int result = preparedStatement.executeUpdate();
            logger.trace("Updated {} instances", result);
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ ex);
        }
        logger.traceExit();
    }

    @Override
    public Excursie findOne(Integer integer) {
        logger.traceEntry("finding excursie with id {} ",integer);
        Connection con=dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("select * from Excursie where id=?")){
            preStmt.setInt(1,integer);
            try(ResultSet result=preStmt.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("id");
                    String obiectiv = result.getString("obiectiv");
                    String firma_transport = result.getString("firma_transport");
                    java.sql.Time ora_plecare = result.getTime("ora_plecare");
                    int pret = result.getInt("pret");
                    int numar_locuri = result.getInt("numar_locuri");
                    Excursie ex = new Excursie(obiectiv, firma_transport, ora_plecare.toLocalTime(), pret, numar_locuri);
                    ex.setId((long) id);
                    logger.traceExit();
                    return ex;
                }
            }
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit("No excursie found with id {}", integer);

        return null;
    }

    @Override
    public Iterable<Excursie> findAll() {
        //logger.();
        Connection con=dbUtils.getConnection();
        List<Excursie> excursii=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from Excursie")) {
            try(ResultSet result=preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    String obiectiv = result.getString("obiectiv");
                    String firma_transport = result.getString("firma_transport");
                    java.sql.Time ora_plecare = result.getTime("ora_plecare");
                    int pret = result.getInt("pret");
                    int numar_locuri = result.getInt("numar_locuri");
                    Excursie ex = new Excursie(obiectiv, firma_transport, ora_plecare.toLocalTime(), pret, numar_locuri);
                    ex.setId((long) id);
                    excursii.add(ex);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        logger.traceExit();
        return excursii;
    }

        @Override
        public Iterable<Excursie> findBeetwenHours(LocalTime ora1, LocalTime ora2) {
            logger.traceEntry("finding excursie with hours {} and {} ",ora1, ora2);
            Connection con=dbUtils.getConnection();
            List<Excursie> excursii=new ArrayList<>();
            try(PreparedStatement preStmt=con.prepareStatement("select * from Excursie where ora_plecare between ? and ?")){
                preStmt.setTime(1,java.sql.Time.valueOf(ora1));
                preStmt.setTime(2,java.sql.Time.valueOf(ora2));
                try(ResultSet result=preStmt.executeQuery()) {
                    while (result.next()) {
                        int id = result.getInt("id");
                        String obiectiv = result.getString("obiectiv");
                        String firma_transport = result.getString("firma_transport");
                        java.sql.Time ora_plecare = result.getTime("ora_plecare");
                        int pret = result.getInt("pret");
                        int numar_locuri = result.getInt("numar_locuri");
                        Excursie ex = new Excursie(obiectiv, firma_transport, ora_plecare.toLocalTime(), pret, numar_locuri);
                        ex.setId((long) id);
                        excursii.add(ex);
                    }
                }
            }catch (SQLException ex){
                logger.error(ex);
                System.out.println("Error DB "+ex);
            }
            logger.traceExit();
            return excursii;
        }


}

