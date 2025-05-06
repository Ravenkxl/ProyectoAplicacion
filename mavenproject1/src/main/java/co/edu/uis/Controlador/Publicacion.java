
package co.edu.uis.Controlador;

import java.util.Scanner;
import co.edu.uis.Controlador.Datos;
import co.edu.uis.Modelo.Articulo;
import co.edu.uis.Modelo.Libro;
import java.util.List;
import co.edu.uis.Modelo.Publicaciones;
import co.edu.uis.Modelo.Revista;

/**
 *
 * @author Karol Hernandez
 */
public class Publicacion {
    Datos d = new Datos();
   public void menu(List publicaciones){
    Scanner sc = new Scanner(System.in);
       System.out.println("Bienvenido al sistema de biblioteca");
       int op =0;
       do {           
           System.out.println(" 1.Mostrar informacion de publicacion \n 2.Agregar publicacion \n 3.Buscar publicacion \n 4.Actualizar publicacion \n 5.Eliminar publicacion \n 6.Listar todas las publicaciones \n 7.Salir"); 
           op =sc.nextInt();
           switch(op){
               case 1->{
                   mostrarInformacion();
               }
               case 2->{
                   agregarPublicacion();
               }
               case 3->{
                   
               }
               case 4->{
                   actualizarPublicacion();
               }
               case 5->{
                   eliminarPublicacion();
               }
               case 6->{
                   listarPublicacion();
               }
           }
       } while (!(op==7));
   }
   public String mostrarInformacion(){
       Scanner sc = new Scanner(System.in);
       if(d.lista()!= null){
       System.out.println("Ingresar titulo de la publicacion a mostrar: ");
       String tit = sc.next();
       for (Object publicacion : d.lista()) {
           if(((Publicaciones) publicacion).getTitulo().equalsIgnoreCase(tit)){
               return publicacion.toString();
               
           }else{
               return "Publicacion no encontrada";
           }
       }
       }
           return "No hay ninguna publicacion registrada ";
       
   }
   public void agregarPublicacion(){
       Scanner sc = new Scanner(System.in);
       System.out.println("Ingresar el tipo de publicacion: (libro, revista, articulo)");
       String type =sc.next();
       System.out.println("Ingresar titulo: ");
       String tit=sc.next();
       System.out.println("Ingresar autor: ");
       String aut =sc.next();
       System.out.println("Ingresar ISBN: ");
       int isbn =sc.nextInt();
       System.out.println("Ingresar año de publicacion");
       int año =sc.nextInt();
       switch(type){
           case "libro"->{
            System.out.println("Ingresar genero del libro: ");
            String gen=sc.next();
            Publicaciones p= new Libro(gen, tit, aut, isbn, año);
            d.cargarLibro(gen, tit, aut, isbn, año);
           }
           case "revista"->{
               System.out.println("Ingresar numero de edicion: ");
               int no=sc.nextInt();
               Publicaciones p = new Revista(no, tit, aut, isbn, año);
               d.cargarRevista(no, tit, aut, isbn, no);
           }
           case "articulo"->{
               System.out.println("Ingresar nombre de la revista: ");
               String rev=sc.next();
               Publicaciones p = new Articulo(rev, tit, aut, isbn, año);
               d.cargarArticulo(rev, tit, aut, isbn, año);
           }
       }
       
   }
   public void actualizarPublicacion(){
       Scanner sc = new Scanner(System.in);
       List <Publicaciones> l=d.lista();
       if(l!= null){
       System.out.println("Ingresar titulo de la publicacion a mostrar: ");
       String tit = sc.next();
       for (Object publicacion : l) {
           if(((Publicaciones) publicacion).getTitulo().equalsIgnoreCase(tit)){
                System.out.println("Ingresar titulo: ");
                String titu=sc.next();
                System.out.println("Ingresar autor: ");
                String aut =sc.next();
                System.out.println("Ingresar ISBN: ");
                int isbn =sc.nextInt();
                System.out.println("Ingresar año de publicacion");
                int año =sc.nextInt();
                ((Publicaciones) publicacion).setTitulo(titu);
                ((Publicaciones) publicacion).setAutor(aut);
                ((Publicaciones) publicacion).setIsbn(isbn);
                ((Publicaciones) publicacion).setYear(año);
                if(((Publicaciones) publicacion) instanceof Libro){
                    System.out.println("Ingresar genero del libro: ");
                    String gen=sc.next();
                    ((Libro) publicacion).setGenero(gen);
                }
                if(((Publicaciones) publicacion) instanceof Revista){
                    System.out.println("Ingresar numero de edicion: ");
                    int ed=sc.nextInt();
                    ((Revista) publicacion).setNumeroEdicion(ed);
                }
                if(((Publicaciones) publicacion) instanceof Articulo){
                    System.out.println("Ingresar nombre de la revista: ");
                    String rev=sc.next();
                    ((Articulo)publicacion).setNombreRevista(rev);
                }
                d.guardarJson(l);
                
           }
       }
       }     
   }
   public void eliminarPublicacion(){
       Scanner sc = new Scanner(System.in);
       List <Publicaciones> l=d.lista();
       if(l!= null){
       System.out.println("Ingresar isbn de la publicacion a mostrar: ");
       int tit = sc.nextInt();
           for (int i = 0; i < l.size(); i++) {
               if(l.get(i).getIsbn()==tit){
                   l.remove(i);
                   break; 
           }   
           }
           d.guardarJson(l);
       }
   }
   public void listarPublicacion(){
       List <Publicaciones> l=d.lista();
       System.out.println(l);
   }
   
   
}
