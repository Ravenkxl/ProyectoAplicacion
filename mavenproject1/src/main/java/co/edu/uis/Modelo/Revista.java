
package co.edu.uis.Modelo;

/**
 *
 * @author Karol Hernandez
 */
public class Revista extends Publicaciones {
    protected int numeroEdicion;

    public Revista(int numeroEdicion, String titulo, String autor, int isbn, int year) {
        super(titulo, autor, isbn, year);
        this.numeroEdicion = numeroEdicion;
    }

    public int getNumeroEdicion() {
        return numeroEdicion;
    }

    public void setNumeroEdicion(int numeroEdicion) {
        this.numeroEdicion = numeroEdicion;
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
        return "Revista{" + "numeroEdicion=" + numeroEdicion + '}';
    }
    
}
