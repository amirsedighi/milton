package io.meshk.milton.command.model;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import io.meshk.milton.command.serdes.DataFactory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static io.meshk.milton.command.util.Constants.DATE_FORMAT;

@Entity
@Getter
@Setter
public class User implements IdentifiedDataSerializable, Comparator<Map.Entry<String, User>> {

    public static final int CLASS_ID = 1000;

    @Id
    @SequenceGenerator(name = "userSeqGen", sequenceName = "userSequence", initialValue = 1, allocationSize = 1)
    @GeneratedValue(generator = "userSeqGen")
    private Long id;

    private String username;

    private String fname;

    private String lname;

    private LocalDate birthdate;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Permission> permissions;

    public User() {
    }

    @Override
    public int getFactoryId() {
        return DataFactory.FACTORY_ID;
    }

    @Override
    public int getClassId() {
        return CLASS_ID;
    }


    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(id);
        out.writeString(username);
        out.writeString(fname);
        out.writeString(lname);
        out.writeString(birthdate.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        id = in.readLong();
        username = in.readString();
        fname = in.readString();
        lname = in.readString();
        birthdate =  LocalDate.parse(in.readString(), DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    @Override
    public int compare(Map.Entry<String, User> o1, Map.Entry<String, User> o2) {
        User u1 = o1.getValue();
        User u2 = o2.getValue();
        return u1.getUsername().compareTo(u2.getUsername());
    }
}
