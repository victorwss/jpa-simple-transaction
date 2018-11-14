package ninja.javahacker.test.jpasimpletransactions;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Entity
@Table(name = "fruits")
public class Fruit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private String color;
}
