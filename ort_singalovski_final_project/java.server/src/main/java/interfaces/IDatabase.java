package interfaces;

import com.google.cloud.storage.Bucket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public interface IDatabase {

    Bucket storageService();

    FirebaseDatabase databaseService();

    DataSnapshot databaseSnapshot();

    FirebaseAuth authService();

    void writeToDatabase(String writeLocation, Object obj);

    void writeToStorage(String fileLocation, String fileName, byte[] data, String fileDesc);
}
