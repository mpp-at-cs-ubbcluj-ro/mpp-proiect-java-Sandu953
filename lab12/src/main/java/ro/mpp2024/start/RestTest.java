package ro.mpp2024.start;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ro.mpp2024.model.Excursie;

import java.util.concurrent.Callable;

public class RestTest {
    public static final String URL = "http://localhost:8080/api/Excursie";

    private final RestTemplate restTemplate = new RestTemplate();

    private <T> T execute(Callable<T> callable) throws ServiceException {
        try {
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) { // server down, resource exception
            throw new ServiceException(e);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public Excursie[] getAll() throws ServiceException {
        return execute(() -> restTemplate.getForObject(URL, Excursie[].class));
    }

    public Excursie getById(String id) throws ServiceException {
        return execute(() -> restTemplate.getForObject(String.format("%s/%s", URL, id), Excursie.class));
    }

    public Excursie create(Excursie user) throws ServiceException {
        return execute(() -> restTemplate.postForObject(URL, user, Excursie.class));
    }

    public void update(Excursie user) throws ServiceException {
        execute(() -> {
            restTemplate.put(URL, user);
            return null;
        });
    }

    public void delete(String id) throws ServiceException {
        execute(() -> {
            restTemplate.delete(String.format("%s/%s", URL, id));
            return null;
        });
    }
}
