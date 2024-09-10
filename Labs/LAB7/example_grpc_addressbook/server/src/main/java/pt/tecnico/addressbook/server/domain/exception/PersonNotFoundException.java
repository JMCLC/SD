package pt.tecnico.addressbook.server.domain.exception;

public class PersonNotFoundException extends IllegalArgumentException {
    private final String email;

    public PersonNotFoundException(String email) {
        super("Person with email " + email + " is not present in the address book");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
