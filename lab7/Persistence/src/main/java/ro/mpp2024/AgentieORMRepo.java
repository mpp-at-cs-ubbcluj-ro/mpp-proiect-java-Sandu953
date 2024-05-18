package ro.mpp2024;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.SessionFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AgentieORMRepo implements IAgentieRepo{
    private SessionFactory sessionFactory;

    public AgentieORMRepo(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public int size() {
        return 0;
    }

    @Override
    public void save(Agentie entity) {
        try(var session = sessionFactory.openSession()){
            var transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }



    @Override
    public void update(Integer integer, Agentie entity) {
        try(var session = sessionFactory.openSession()){
            var transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Integer idEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Agentie findOne(Integer idEntity) {
        try(var session = sessionFactory.openSession()){
            var cititor = session.get(Agentie.class, idEntity);
            return cititor;
        }
        catch (Exception e){
            return null;
        }
    }

    @Override
    public List<Agentie> findAll() {
        try(var session = sessionFactory.openSession()){
            return session.createQuery("SELECT U FROM Agentie U", Agentie.class).list();
        }
        catch (Exception e){
            return List.of();
        }
    }


    public Agentie findByUsername(String username) {
        try(var session = sessionFactory.openSession()){
            var query = session.createQuery("SELECT U FROM Agentie U WHERE U.username = :username", Agentie.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        }
        catch (Exception e){
            return null;
        }
    }

    @Override
    public boolean loginByUsernamePassword(String username, String password) {
        try(var session = sessionFactory.openSession()){
            var query = session.createQuery("SELECT U FROM Agentie U WHERE U.username = :username AND U.password = :password", Agentie.class);
            query.setParameter("username", username);
            query.setParameter("password", hash(password));
            if(query.uniqueResult() != null)
                return true;
            return false;
        }
        catch (Exception e){
            return false;
        }
    }

    public Agentie findBy(String username, String password) {
        try(var session = sessionFactory.openSession()){
            var query = session.createQuery("SELECT U FROM Agentie U WHERE U.username = :username AND U.password = :password", Agentie.class);
            query.setParameter("username", username);
            query.setParameter("password", hash(password));
            return query.uniqueResult();
        }
        catch (Exception e){
            return null;
        }
    }

    public  Agentie findByUser(String username) {
        try(var session = sessionFactory.openSession()){
            var query = session.createQuery("SELECT U FROM Agentie U WHERE U.username = :username", Agentie.class);
            query.setParameter("username", username);
            //query.setParameter("password", hash(password));
            return query.uniqueResult();
        }
        catch (Exception e){
            return null;
        }
    }

    @Override
    public void save(String username, String password) {

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
}