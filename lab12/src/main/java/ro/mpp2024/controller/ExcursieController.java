package ro.mpp2024.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mpp2024.model.Excursie;
import ro.mpp2024.persistance.ExcursieRepo;

import java.util.List;

@RestController
@RequestMapping("/api/Excursie")
@CrossOrigin(origins = "*")
public class ExcursieController {

    private ExcursieRepo excursieRepo;

    @Autowired
    public void setExcursieRepo(ExcursieRepo excursieRepo) {
        this.excursieRepo = excursieRepo;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Excursie> getAll(){
        System.out.println("Get all Excursies ...");
        List<Excursie> Excursies = (List<Excursie>) excursieRepo.findAll();
        return Excursies;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getById(@PathVariable Long id){
        System.out.println("Get by id "+id);
        Excursie Excursie = excursieRepo.findOne(id.intValue());
        if (Excursie==null)
            return new ResponseEntity<>("Excursie not found", HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(Excursie, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable Long id){
        System.out.println("Deleting Excursie ... "+id);
        Excursie Excursie = excursieRepo.deleteE(id.intValue());
        return new ResponseEntity<>(Excursie, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody Excursie Excursie){
        System.out.println("Creating Excursie ...");
        Excursie Excursie1 = excursieRepo.saveE(Excursie);
        if (Excursie1 == null)
            return new ResponseEntity<>("Error while adding Excursie", HttpStatus.BAD_REQUEST);
        else
            return new ResponseEntity<>(Excursie1, HttpStatus.OK);
    }

    @RequestMapping( method = RequestMethod.PUT)
    public ResponseEntity<?> update( @RequestBody Excursie Excursie){
        System.out.println("Updating Excursie ...");
        //Excursie.setId(id);
        Excursie Excursie1 = excursieRepo.updateE(Excursie.getId().intValue(), Excursie);
        if (Excursie1 == null)
            return new ResponseEntity<>("Error while updating Excursie", HttpStatus.BAD_REQUEST);
        else
            return new ResponseEntity<>(Excursie1, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String userError(Exception e) {
        return e.getMessage();
    }
}