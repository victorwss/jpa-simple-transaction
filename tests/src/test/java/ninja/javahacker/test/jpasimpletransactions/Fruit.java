package ninja.javahacker.test.jpasimpletransactions;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String color;

    public Fruit() {}

    public Fruit(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
