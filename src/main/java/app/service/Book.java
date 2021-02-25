package app.service;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author vasil
 */
@Data
@AllArgsConstructor
public class Book {
    private String category;
    private String author;
    private String name;
}
