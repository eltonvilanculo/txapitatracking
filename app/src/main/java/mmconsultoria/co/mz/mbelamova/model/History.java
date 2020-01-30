package mmconsultoria.co.mz.mbelamova.model;

public class History {
    private String idViagem;
    private String nomeMoto;
    private String localPartida;
    private String localDestino;
    private String dataViagem;
    private double valorViagem;

    public History(String idViagem, String nomeMoto, String localPartida, String localDestino, String dataViagem, double valorViagem) {
        this.idViagem = idViagem;
        this.nomeMoto = nomeMoto;
        this.localPartida = localPartida;
        this.localDestino = localDestino;
        this.dataViagem = dataViagem;
        this.valorViagem = valorViagem;
    }


    public String getIdViagem() {
        return idViagem;
    }

    public void setIdViagem(String idViagem) {
        this.idViagem = idViagem;
    }

    public String getNomeMoto() {
        return nomeMoto;
    }

    public void setNomeMoto(String nomeMoto) {
        this.nomeMoto = nomeMoto;
    }

    public String getLocalPartida() {
        return localPartida;
    }

    public void setLocalPartida(String localPartida) {
        this.localPartida = localPartida;
    }

    public String getLocalDestino() {
        return localDestino;
    }

    public void setLocalDestino(String localDestino) {
        this.localDestino = localDestino;
    }

    public String getDataViagem() {
        return dataViagem;
    }

    public void setDataViagem(String dataViagem) {
        this.dataViagem = dataViagem;
    }

    public double getValorViagem() {
        return valorViagem;
    }

    public void setValorViagem(double valorViagem) {
        this.valorViagem = valorViagem;
    }
}
