package bg.sofia.uni.fmi.mjt.tagger.containers;

import java.util.Objects;

public class CaseInsensitiveString {

    private String string;

    public CaseInsensitiveString(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseInsensitiveString that = (CaseInsensitiveString) o;
        return Objects.equals(string.toUpperCase(), that.string.toUpperCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(string.toUpperCase());
    }
}
