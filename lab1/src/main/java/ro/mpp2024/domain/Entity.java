package ro.mpp2024.domain;


import java.io.Serializable;
import java.util.Objects;

public class Entity<ID> implements Serializable {

    // private static final long serialVersionUID = 7331115341259248461L;
    protected ID id;
    public ID getId() {
        return id;
    }
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * Override equals
     * @param o Object
     * @return bool
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity<?> entity)) return false;
        return getId().equals(entity.getId());
    }

    /**
     * override hashcode
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    /**
     * override toString
     * @return String
     */
    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }
}