package cl.esperanza.balance.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String mensaje, Throwable causa){
    super(mensaje, causa);
    }
}