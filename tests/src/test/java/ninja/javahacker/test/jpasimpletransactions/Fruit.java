package ninja.javahacker.test.jpasimpletransactions;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Entity
@Table(name = "fruits")
@Getter
public class Fruit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private String color;

    protected Fruit() {}

    public Fruit(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
