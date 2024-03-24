package ro.mpp2024.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.domain.Agentie;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;


public class AgentieRepo implements IAgentieRepo {
    private JdbcUtils dbUtils;


    private static final Logger logger = LogManager.getLogger();

    public AgentieRepo(Properties props) {
        logger.info("Initializing AgentieRepo with properties: {} ", props);
        dbUtils = new JdbcUtils(props);

    }

    @Override
    public int size() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select count(*) as [SIZE] from Agentie")) {
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
    public void save(Agentie entity) {
        logger.traceEntry("saving agentie {} ", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("insert into Agentie values (?,?)")) {
            preStmt.setInt(1, entity.getId().intValue());
            preStmt.setString(2, entity.getUsername());
            //preStmt.setString(3,criptare(entity.getPassword(),secretKey));
            int result = preStmt.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.traceExit();

    }

    @Override
    public void save(String username, String password) {
        logger.traceEntry("saving agentie {} ", username);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("insert into Agentie(username, password) values (?,?)")) {
            preStmt.setString(1, username);
            preStmt.setString(2, hash(password));
            int result = preStmt.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.traceExit();
    }

    @Override
    public void delete(Integer integer) {
        logger.traceEntry("deleting agentie with {}", integer);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("delete from Agentie where id=?")) {
            preStmt.setInt(1, integer);
            int result = preStmt.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public void update(Integer integer, Agentie entity) {
        //To do
        logger.traceEntry("update agentie {} ", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = con.prepareStatement("update Agentie set username = ? where id = ?")) {
            preparedStatement.setString(1, entity.getUsername());
            //preparedStatement.setString(2, criptare(entity.getPassword(), secretKey));
            preparedStatement.setInt(3, integer);

            int result = preparedStatement.executeUpdate();
            logger.trace("Updated {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.traceExit();
    }

    @Override
    public Agentie findOne(Integer integer) {
        logger.traceEntry("finding agentie with id {} ", integer);
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("select * from Agentie where id=?")) {
            preStmt.setInt(1, integer);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("id");
                    String username = result.getString("username");

                    Agentie ag = new Agentie(username);
                    ag.setId((long) id);
                    logger.traceExit();
                    return ag;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit("No agentie found with id {}", integer);

        return null;
    }

    @Override
    public Iterable<Agentie> findAll() {
        //logger.();
        Connection con = dbUtils.getConnection();
        List<Agentie> agentii = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Agentie")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    String username = result.getString("username");
                    String password = result.getString("password");
                    Agentie ag = new Agentie(username);
                    agentii.add(ag);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit();
        return agentii;
    }


    @Override
    public boolean loginByUsernamePassword(String username, String password) {
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Agentie where username=?")) {
            preStmt.setString(1, username);
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    String username2 = result.getString("username");
                    String password2 = result.getString("password");
                    if (hash(password).equals(password2)) {
                        return true;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit();
        return false;
    }

    public static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input.getBytes());

            // Convert byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


//    public static String criptare(String text, SecretKey secretKey) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//        byte[] textCriptat = cipher.doFinal(text.getBytes());
//        return Base64.getEncoder().encodeToString(textCriptat);
//    }
//
//    public static String decriptare(String textCriptat, SecretKey secretKey) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//        byte[] textDecriptat = cipher.doFinal(Base64.getDecoder().decode(textCriptat));
//        return new String(textDecriptat);
//    }

}
