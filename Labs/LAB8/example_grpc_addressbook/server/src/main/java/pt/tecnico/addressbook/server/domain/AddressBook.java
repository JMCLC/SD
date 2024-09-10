package pt.tecnico.addressbook.server.domain;

import pt.tecnico.addressbook.grpc.AddressBookList;
import pt.tecnico.addressbook.grpc.PersonInfo.PhoneNumber;
import pt.tecnico.addressbook.grpc.PersonInfo.PhoneType;
import pt.tecnico.addressbook.grpc.PersonInfo;
import pt.tecnico.addressbook.server.domain.exception.DuplicatePersonInfoException;
import pt.tecnico.addressbook.server.domain.exception.PersonNotFoundException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AddressBook {

    private ConcurrentHashMap<String, Person> people = new ConcurrentHashMap<>();

    public AddressBook() {
    }

    public void addPerson(String name, String email, int phoneNumber, PhoneType type, String education_degree) throws DuplicatePersonInfoException {
        if(people.putIfAbsent(email, new Person(name, email, phoneNumber, type, education_degree)) != null) {
            throw new DuplicatePersonInfoException(email);
        }
    }

    public AddressBookList proto() {
        return AddressBookList.newBuilder()
                .addAllPeople(people.values().stream().map(Person::proto).collect(Collectors.toList()))
                .build();
    }

    public PersonInfo searchPerson(String email) throws PersonNotFoundException {
        if (!people.containsKey(email)) {
            throw new PersonNotFoundException(email);
        } 
        Person person = people.get(email);
        PhoneNumber phone = PhoneNumber.newBuilder().setNumber(person.getPhoneNumber()).setType(person.getType()).build();
        return PersonInfo.newBuilder().setName(person.getName()).setEmail(email).setPhone(phone).setEducationDegree(person.getEducationDegree()).build();
    }

    public AddressBookList listAll(String education_degree) {
        return AddressBookList.newBuilder()
        .addAllPeople(people.values().stream().filter(person -> person.getEducationDegree().equals(education_degree)).map(Person::proto).collect(Collectors.toList()))
        .build();
    }
}
