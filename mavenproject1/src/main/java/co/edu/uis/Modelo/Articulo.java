
package co.edu.uis.Modelo;

/**
 *
 * @author Karol Hernandez
 */
public class Articulo extends Publicaciones{
    protected String nombreRevista;

    public Articulo(String nombreRevista, String titulo, String autor, int isbn, int year) {
        super(titulo, autor, isbn, year);
        this.nombreRevista = nombreRevista;
    }
    

    public String getNombreRevista() {
        return nombreRevista;
    }

    public void setNombreRevista(String nombreRevista) {
        this.nombreRevista = nombreRevista;
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
        return "Articulo{" + "nombreRevista=" + nombreRevista + '}';
    }
    

}
