package interfaces;

public interface IUser {

    String getEmail();

    String getFirstName();

    String getLastName();

    String getPassword();

    void setFirstNameError(String firstNameError);

    void setLastNameError(String lastNameError);

    void setEmailError(String emailError);

    void setPasswordError(Boolean passwordError);

    String getFirstNameError();

    String getLastNameError();

    String getEmailError();

    Boolean getPasswordError();
}
