/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;

/**
 *
 * @author TheJPlay2006
 */
public class Cliente {
    private String cedula;
    private String nombre;
    private String telefono;
    private String email;

    public Cliente(String cedula, String nombre, String telefono, String email) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }

    // Getters y setters
    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }

    public void setCedula(String cedula) { this.cedula = cedula; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Cliente{" +
                "cedula='" + cedula + '\'' +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
