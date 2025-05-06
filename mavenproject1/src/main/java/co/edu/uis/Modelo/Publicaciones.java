
package co.edu.uis.Modelo;

/**
 *
 * @author Karol Hernandez
 */
public class Publicaciones {
    protected String titulo;
    protected String autor;
    protected int isbn;
    protected int year;

    public Publicaciones(String titulo, String autor, int isbn, int year) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.year = year;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getIsbn() {
        return isbn;
    }

    public void setIsbn(int isbn) {
        this.isbn = isbn;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Publicaciones{" + "titulo=" + titulo + ", autor=" + autor + ", isbn=" + isbn + ", year=" + year + '}';
    }
    
    
}
