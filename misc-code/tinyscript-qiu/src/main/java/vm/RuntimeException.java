package vm;

public class RuntimeException extends Exception {
    @SuppressWarnings("unused")
    private String _msg;

    public RuntimeException(String msg) {
        this._msg = msg;
    }

}
