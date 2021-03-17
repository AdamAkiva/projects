package interfaces;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface IContainer<T> {

    void sendContainerOverSocket(IContainer<T> container) throws IOException, InterruptedException, ExecutionException;

    void setResult(boolean result);

    void setLogMessage(String logMessage);

    void setUserMessage(String userMessage);

    int getRequest();

    boolean getResult();

    String getLogMessage();

    String getUserMessage();

    T getObject();
}
