package interfaces;

public interface IContainer<T> {

    void setResult(boolean result);

    void setLogMessage(String logMessage);

    void setUserMessage(String userMessage);

    int getRequest();

    boolean getResult();

    String getLogMessage();

    String getUserMessage();

    T getObject();
}
