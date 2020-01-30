package mmconsultoria.co.mz.mbelamova.mpesaapi;


public class Mpesa {
    protected String phoneNumber;
    protected String value;
    protected String transactionReference;
    protected String serviceProvider;
    protected String thirdPartyReference;
    protected String message;
	protected String authorization;
    protected String origin;

    private Mpesa() {

    }

    public static Mpesa init(String origin, String provider, String authorization) {
        Mpesa mpesa = new Mpesa();
        mpesa.origin = origin;
        mpesa.serviceProvider = provider;
        mpesa.authorization = authorization;
        return mpesa;
    }

    public Mpesa phoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
        return this;
    }

    public Mpesa value(double value){
        this.value = String.valueOf(value);
        return this;
    }

    public Mpesa message(String message){
        this.message = message;
        return this;
    }

    public Mpesa thirdPartRef(String reference){
        this.thirdPartyReference = reference;
        return this;
    }

    public Mpesa transactionRef(String reference){
        this.transactionReference = reference;
        return this;
    }

    public Executor makeRequest(){
       Executor executor = new Executor();
        executor.with(this);
        return executor;
    }

    

}