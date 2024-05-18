package ro.mpp2024;

import javax.persistence.*;
import java.io.Serializable;



import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
public class Entitate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;



    // private static final long serialVersionUID = 7331115341259248461L;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Override equals
     *
     * @param o Object
     * @return bool
     */
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Entity<?> entity)) return false;
//        return getId().equals(entity.getId());
//    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entitate entity = (Entitate) o;
        return id == entity.id;
    }

    /**
     * override hashcode
     *
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    /**
     * override toString
     *
     * @return String
     */
    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }
}