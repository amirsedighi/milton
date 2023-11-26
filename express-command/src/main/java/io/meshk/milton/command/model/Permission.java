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
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import static io.meshk.milton.command.util.Constants.DATE_FORMAT;

@Entity
@Getter
@Setter
public class Permission implements IdentifiedDataSerializable, Comparator<Map.Entry<String, Permission>> {

    public static final int CLASS_ID = 1001;
    @Id
    @SequenceGenerator(name = "permissionSeqGen", sequenceName = "permissionSequence", initialValue = 1, allocationSize = 1)
    @GeneratedValue(generator = "permissionSeqGen")
    private Long id;

    private String title;

    private String description;

    private LocalDate publishdate;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<User> users;

    public Permission() {
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
        out.writeString(title);
        out.writeString(description);
        out.writeString(publishdate.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        id = in.readLong();
        title = in.readString();
        description = in.readString();
        publishdate =  LocalDate.parse(in.readString(), DateTimeFormatter.ofPattern(DATE_FORMAT)); ;
    }


    @Override
    public int compare(Map.Entry<String, Permission> o1, Map.Entry<String, Permission> o2) {
        Permission p1 = o1.getValue();
        Permission p2 = o2.getValue();
        return p1.getTitle().compareTo(p2.getTitle());
    }
}