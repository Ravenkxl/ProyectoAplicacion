
package co.edu.uis.Modelo;

/**
 *
 * @author Karol Hernandez
 */
public class Libro extends Publicaciones {
    protected String genero;

    public Libro(String genero, String titulo, String autor, int isbn, int year) {
        super(titulo, autor, isbn, year);
        this.genero = genero;
    }


    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
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
        return "Libro{" + "genero=" + genero + '}';
    }
    

}
