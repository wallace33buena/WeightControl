package br.com.senac.weightcontrol.modelo;

public class WeightControlApp {
    private int id;
    private String peso;
    private String data;
    private String circ;

    public WeightControlApp(int id, String peso, String data, String circ){
        this.id=id;
        this.peso=peso;
        this.data=data;
        this.circ=circ;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCirc() {
        return circ;
    }

    public void setCirc(String circ) {
        this.circ = circ;
    }
}
