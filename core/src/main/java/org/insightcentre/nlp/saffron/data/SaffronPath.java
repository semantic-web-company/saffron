package org.insightcentre.nlp.saffron.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.File;
import java.util.Objects;

/**
 * A path that can be interpolated with saffron.home
 * @author John McCrae
 */
public class SaffronPath {
    private final String path;

    @JsonCreator
    public SaffronPath(String path) {
        this.path = path;
    }
    
    public static SaffronPath fromFile(File file) {
        return new SaffronPath(file.getAbsolutePath());
    }
    
    public File toFile() {
        return new File(path);
    }

    @Override
    @JsonValue
    public String toString() {
        return path;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.path);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SaffronPath other = (SaffronPath) obj;
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        return true;
    }
    
    
}
