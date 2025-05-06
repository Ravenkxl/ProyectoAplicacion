
package co.edu.uis.Vista;
import co.edu.uis.Controlador.Datos;
/**
 *
 * @author Karol Hernandez
 */
public class NewClass{
String txtTitulo;
String Autor;
int año;
int isbn;

private Datos datos;

public NewClass(){
    datos=new Datos();
}


    public String getTxtTitulo() {
        return txtTitulo;
    }

    public void setTxtTitulo(String txtTitulo) {
        this.txtTitulo = txtTitulo;
    }

    public String getAutor() {
        return Autor;
    }

    public void setAutor(String Autor) {
        this.Autor = Autor;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public int getIsbn() {
        return isbn;
    }

    public void setIsbn(int isbn) {
        this.isbn = isbn;
    }

    public Datos getDatos() {
        return datos;
    }

    public void setDatos(Datos datos) {
        this.datos = datos;
    }



}