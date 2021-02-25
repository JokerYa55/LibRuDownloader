package app.service;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author vasil
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class UrlData {
    private String name;
    private String url;
    private String parentUrl;
    
    @Override
    public boolean equals(Object obj) {
        return ((UrlData) obj).getName().equalsIgnoreCase(name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }
}
