package mmconsultoria.co.mz.mbelamova.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import mmconsultoria.co.mz.mbelamova.R;

public class Driver implements Parcelable {

    private static int radius=1;
    private String id;
    private String personId;
    private String personName;
    private String licenceNumber;
    private String licenceType;
    private Vehicle vehicle;

    //variavel de teste
    int imageSrc;
    private double rate=1;


    public Driver(String nome, int imageSrc) {
        this.personName = nome;
        this.imageSrc = imageSrc;
    }

    protected Driver(Parcel in) {
        id = in.readString();
        personId = in.readString();
        personName = in.readString();
        licenceNumber = in.readString();
        licenceType = in.readString();
        vehicle = in.readParcelable(Vehicle.class.getClassLoader());
    }

    public  Driver(){}

    public  Driver(String key){
        this.personId=key;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(personId);
        dest.writeString(personName);
        dest.writeString(licenceNumber);
        dest.writeString(licenceType);
        dest.writeParcelable(vehicle, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Driver> CREATOR = new Creator<Driver>() {
        @Override
        public Driver createFromParcel(Parcel in) {
            return new Driver(in);
        }

        @Override
        public Driver[] newArray(int size) {
            return new Driver[size];
        }
    };


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public String getLicenceType() {
        return licenceType;
    }

    public void setLicenceType(String licenceType) {
        this.licenceType = licenceType;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    // Preenchi com dados nao reais , teras de puxar da BD


    public int getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(int imageSrc) {
        this.imageSrc = imageSrc;
    }

    public static String []getListaNomes(){



        return new String[]{"Motorista 1","Motorista 2","Motorista 3","Motorista 4","Motorista 5","Motorista 6","Motorista 7","Motorista 8"};
    }
    public static int [] getListaImagens(){

        return new int[]{R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground,R.drawable.cell_na_mao,R.drawable.perfil_foto};
    }
    public static List<Driver> fillObject(){

        List<Driver> listModelo = new ArrayList<>();
        int [] imageDrawable = getListaImagens();
        String [] nomes= getListaNomes();
        for (int i = 0 ; i<imageDrawable.length;i++){


            Driver l = new Driver();
            l.setPersonName(nomes[i]);
            l.setImageSrc(imageDrawable[i]);
            listModelo.add(l);
        }
        return listModelo;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
